import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.ScopedArtifacts
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.dokka.gradle.engine.parameters.KotlinPlatform
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier
import org.jetbrains.kotlin.gradle.dsl.JvmDefaultMode
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import java.io.File
import java.nio.file.Files
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import javax.tools.ToolProvider

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.dokka)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

val javaTarget = JvmTarget.fromTarget(libs.versions.jvmTarget.get())

abstract class StripNonYoutubeNewPipeTask : DefaultTask() {
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val allJars: ListProperty<RegularFile>

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val allDirectories: ListProperty<Directory>

    @get:OutputFile
    abstract val output: RegularFileProperty

    @TaskAction
    fun taskAction() {
        val tempDir = Files.createTempDirectory("newpipe-strip").toFile()
        try {
            val srcDir = File(tempDir, "src/org/schabi/newpipe/extractor").apply { mkdirs() }
            val clsDir = File(tempDir, "cls").apply { mkdirs() }
            val serviceListJava = File(srcDir, "ServiceList.java")
            serviceListJava.writeText(
                """
                package org.schabi.newpipe.extractor;

                import org.schabi.newpipe.extractor.services.youtube.YoutubeService;
                import java.util.List;

                public final class ServiceList {
                    private ServiceList() {}
                    public static final YoutubeService YouTube = new YoutubeService(0);
                    private static final List<StreamingService> SERVICES = List.of(YouTube);
                    public static List<StreamingService> all() {
                        return SERVICES;
                    }
                }
                """.trimIndent()
            )

            val jarFiles = allJars.get().map { it.asFile }
            val cp = jarFiles.joinToString(File.pathSeparator) { it.absolutePath }
            val compiler = ToolProvider.getSystemJavaCompiler()
                ?: error("System Java compiler is required to build YouTube-only ServiceList")
            val compileExit = compiler.run(
                null,
                null,
                null,
                "-source", libsTargetJava(),
                "-target", libsTargetJava(),
                "-cp", cp,
                "-d", clsDir.absolutePath,
                serviceListJava.absolutePath
            )
            if (compileExit != 0) {
                error("Failed to compile YouTube-only ServiceList.java (exit $compileExit)")
            }

            val customServiceListBytes = File(clsDir, "org/schabi/newpipe/extractor/ServiceList.class").readBytes()
            val excludedPrefixes = listOf(
                "org/schabi/newpipe/extractor/services/soundcloud/",
                "org/schabi/newpipe/extractor/services/bandcamp/",
                "org/schabi/newpipe/extractor/services/peertube/",
                "org/schabi/newpipe/extractor/services/media_ccc/",
            )

            val outFile = output.get().asFile
            outFile.parentFile?.mkdirs()

            val seenEntries = HashSet<String>()
            val mergedServices = LinkedHashMap<String, LinkedHashSet<String>>()

            JarOutputStream(outFile.outputStream().buffered()).use { jos ->
                fun shouldExclude(name: String): Boolean {
                    if (name == "org/schabi/newpipe/extractor/ServiceList.class") return true
                    if (excludedPrefixes.any { name.startsWith(it) }) return true
                    if (name.startsWith("META-INF/") && (name.endsWith(".SF") || name.endsWith(".RSA") || name.endsWith(".DSA"))) {
                        return true
                    }
                    return false
                }

                for (dir in allDirectories.get().map { it.asFile }) {
                    if (!dir.exists()) continue
                    val rootPath = dir.toPath()
                    dir.walkTopDown().filter { it.isFile }.forEach { f ->
                        val entryName = rootPath.relativize(f.toPath()).toString().replace(File.separatorChar, '/')
                        if (shouldExclude(entryName)) return@forEach
                        if (entryName.startsWith("META-INF/services/")) {
                            val lines = f.readLines().map { it.trim() }.filter { it.isNotEmpty() }
                            mergedServices.getOrPut(entryName) { LinkedHashSet() }.addAll(lines)
                            return@forEach
                        }
                        if (seenEntries.add(entryName)) {
                            jos.putNextEntry(JarEntry(entryName))
                            f.inputStream().use { it.copyTo(jos) }
                            jos.closeEntry()
                        }
                    }
                }

                for (jarFile in jarFiles) {
                    if (!jarFile.exists()) continue
                    JarFile(jarFile).use { jf ->
                        val entries = jf.entries()
                        while (entries.hasMoreElements()) {
                            val entry = entries.nextElement()
                            if (entry.isDirectory) continue
                            val entryName = entry.name
                            if (shouldExclude(entryName)) continue
                            if (entryName.startsWith("META-INF/services/")) {
                                jf.getInputStream(entry).bufferedReader().useLines { seq ->
                                    val set = mergedServices.getOrPut(entryName) { LinkedHashSet() }
                                    seq.map { it.trim() }.filter { it.isNotEmpty() }.forEach { set.add(it) }
                                }
                                continue
                            }
                            if (seenEntries.add(entryName)) {
                                jos.putNextEntry(JarEntry(entryName))
                                jf.getInputStream(entry).use { it.copyTo(jos) }
                                jos.closeEntry()
                            }
                        }
                    }
                }

                for ((svcName, impls) in mergedServices) {
                    if (seenEntries.add(svcName)) {
                        jos.putNextEntry(JarEntry(svcName))
                        jos.write((impls.joinToString("\n") + "\n").toByteArray(Charsets.UTF_8))
                        jos.closeEntry()
                    }
                }

                jos.putNextEntry(JarEntry("org/schabi/newpipe/extractor/ServiceList.class"))
                jos.write(customServiceListBytes)
                jos.closeEntry()
            }
        } finally {
            tempDir.deleteRecursively()
        }
    }

    private fun libsTargetJava(): String = "11"
}

abstract class GenerateGitHashTask : DefaultTask() {

    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val headFile: RegularFileProperty

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val headsDir: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val head = headFile.get().asFile

        val hash = try {
            if (head.exists()) {
                // Read the commit hash from .git/HEAD
                val headContent = head.readText().trim()
                if (headContent.startsWith("ref:")) {
                    val refPath = headContent.substring(5) // e.g., refs/heads/main
                    val commitFile = File(head.parentFile, refPath)
                    if (commitFile.exists()) commitFile.readText().trim() else ""
                } else headContent // If it's a detached HEAD (commit hash directly)
            } else "" // If .git/HEAD doesn't exist
        } catch (_: Throwable) {
            "" // Just set to an empty string if any exception occurs
        }.take(7) // Get the short commit hash

        val outFile = outputDir.file("git-hash.txt").get().asFile
        outFile.parentFile.mkdirs()
        outFile.writeText(hash)
    }
}

val generateGitHash = tasks.register<GenerateGitHashTask>("generateGitHash") {
    val gitDir = layout.projectDirectory.dir("../.git")

    headFile.set(gitDir.file("HEAD"))
    headsDir.set(gitDir.dir("refs/heads"))

    outputDir.set(layout.buildDirectory.dir("generated/git"))
}

android {
    @Suppress("UnstableApiUsage")
    testOptions {
        unitTests.isReturnDefaultValues = true
    }

    // Looks like google likes to add metadata only they can read https://gitlab.com/IzzyOnDroid/repo/-/work_items/491
    dependenciesInfo {
        // Disables dependency metadata when building APKs.
        includeInApk = false
        // Disables dependency metadata when building Android App Bundles.
        includeInBundle = false
    }

    androidComponents {
        onVariants { variant ->
            variant.sources.assets?.addGeneratedSourceDirectory(
                generateGitHash,
                GenerateGitHashTask::outputDir
            )
            val stripTask = tasks.register<StripNonYoutubeNewPipeTask>(
                "stripNonYoutubeNewPipe${variant.name.replaceFirstChar { it.uppercase() }}"
            )
            variant.artifacts.forScope(ScopedArtifacts.Scope.ALL)
                .use(stripTask)
                .toTransform(
                    ScopedArtifact.CLASSES,
                    StripNonYoutubeNewPipeTask::allJars,
                    StripNonYoutubeNewPipeTask::allDirectories,
                    StripNonYoutubeNewPipeTask::output
                )
        }
    }

    signingConfigs {
        // We just use SIGNING_KEY_ALIAS here since it won't change
        // so won't kill the configuration cache.
        if (System.getenv("SIGNING_KEY_ALIAS") != null) {
            create("prerelease") {
                val tmpFilePath = System.getProperty("user.home") + "/work/_temp/keystore/"
                val prereleaseStoreFile: File? = File(tmpFilePath).listFiles()?.first()

                storeFile = prereleaseStoreFile?.let { file(it) }
                storePassword = System.getenv("SIGNING_STORE_PASSWORD")
                keyAlias = System.getenv("SIGNING_KEY_ALIAS")
                keyPassword = System.getenv("SIGNING_KEY_PASSWORD")
            }
        }
    }

    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.stleo.ott"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()

        manifestPlaceholders["target_sdk_version"] = libs.versions.targetSdk.get()

        // Reads local.properties
        val localProperties = gradleLocalProperties(rootDir, project.providers)

        buildConfigField(
            "long",
            "BUILD_DATE",
            "${System.currentTimeMillis()}"
        )
        buildConfigField(
            "String",
            "SIMKL_CLIENT_ID",
            "\"" + (System.getenv("SIMKL_CLIENT_ID") ?: localProperties["simkl.id"]) + "\""
        )
        buildConfigField(
            "String",
            "SIMKL_CLIENT_SECRET",
            "\"" + (System.getenv("SIMKL_CLIENT_SECRET") ?: localProperties["simkl.secret"]) + "\""
        )
        buildConfigField(
            "String",
            "MAL_KEY",
            "\"" + (System.getenv("MAL_KEY") ?: localProperties["mal.key"]) + "\""
        )
        buildConfigField(
            "String",
            "ANILIST_KEY",
            "\"" + (System.getenv("ANILIST_KEY") ?: localProperties["anilist.key"]) + "\""
        )
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isDebuggable = false
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    flavorDimensions.add("state")
    productFlavors {
        create("stable") {
            dimension = "state"
        }
        create("prerelease") {
            dimension = "state"
            applicationIdSuffix = ".prerelease"
            if (signingConfigs.names.contains("prerelease")) {
                signingConfig = signingConfigs.getByName("prerelease")
            } else {
                logger.warn("No prerelease signing config!")
            }
            versionNameSuffix = "-PRE"
            versionCode = (System.currentTimeMillis() / 60000).toInt()
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.toVersion(javaTarget.target)
        targetCompatibility = JavaVersion.toVersion(javaTarget.target)
    }

    java {
        // Use Java 17 toolchain even if a higher JDK runs the build.
        // We still use Java 8 for now which higher JDKs have deprecated.
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(libs.versions.jdkToolchain.get()))
        }
    }

    lint {
        checkReleaseBuilds = false
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    packaging {
        jniLibs {
            // Enables legacy JNI packaging to reduce APK size (similar to builds before minSdk 23).
            // Note: This may increase app startup time slightly.
            useLegacyPackaging = true
        }
    }

    namespace = "com.stleo.ott"
}

dependencies {
    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.json)
    androidTestImplementation(libs.core)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.instancio.core)
    androidTestImplementation(libs.junit.ktx)
    androidTestImplementation(libs.kotlin.test)

    // Android Core & Lifecycle
    implementation(libs.core.ktx)
    implementation(libs.activity.ktx)
    implementation(libs.annotation)
    implementation(libs.appcompat)
    implementation(libs.fragment.ktx)
    implementation(libs.bundles.lifecycle)
    implementation(libs.bundles.navigation)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.kotlinx.serialization.json) // JSON Parser

    // Design & UI
    implementation(libs.preference.ktx)
    implementation(libs.material)
    implementation(libs.constraintlayout)

    // Coil Image Loading
    implementation(libs.bundles.coil)

    // Media 3 (ExoPlayer)
    implementation(libs.bundles.media3)
    implementation(libs.video)

    // FFmpeg Decoding
    implementation(libs.bundles.nextlib)

    // Anime-db for filler
    implementation(libs.anime.db)

    // PlayBack
    implementation(libs.colorpicker) // Subtitle Color Picker
    implementation(libs.newpipeextractor) // For Trailers
    implementation(libs.juniversalchardet) // Subtitle Decoding

    // UI Stuff
    implementation(libs.shimmer) // Shimmering Effect (Loading Skeleton)
    implementation(libs.palette.ktx) // Palette for Images -> Colors
    implementation(libs.tvprovider)
    implementation(libs.overlappingpanels) // Gestures
    implementation(libs.biometric) // Fingerprint Authentication
    implementation(libs.previewseekbar.media3) // SeekBar Preview
    implementation(libs.qrcode.kotlin) // QR Code for PIN Auth on TV

    // Extensions & Other Libs
    implementation(libs.jsoup) // HTML Parser
    implementation(libs.ksoup) // HTML Parser
    implementation(libs.rhino) // Run JavaScript
    implementation(libs.androidx.documentfile) // Scoped storage & SAF files
    coreLibraryDesugaring(libs.desugar.jdk.libs.nio) // NIO Flavor Needed for NewPipeExtractor
    implementation(libs.conscrypt.android) // To Fix SSL Fu*kery on Android 9
    implementation(libs.jackson.module.kotlin) // JSON Parser
    implementation(libs.zipline)

    // Temp/deprecated; will be removed once extensions have time to migrate from using it
    implementation("com.google.code.gson:gson:2.11.0")
    // Deprecated; will be removed once extensions have time to migrate from using it
    implementation("me.xdrop:fuzzywuzzy:1.4.0")

    // Downloading & Networking
    implementation(libs.work.runtime.ktx)
    implementation(libs.nicehttp) // HTTP Lib

    implementation(libs.bundles.compose)
    implementation(libs.activity.compose)
    implementation(libs.kotlinx.io.core) // Logcat parser

    implementation(project(":library"))
    implementation(project(":shared"))
}

tasks.register<Jar>("androidSourcesJar") {
    archiveClassifier.set("sources")
    from(android.sourceSets.getByName("main").java.directories) // Full Sources
}

tasks.register<Copy>("copyJar") {
    dependsOn("build", ":library:jvmJar")
    from(
        "build/intermediates/compile_app_classes_jar/prereleaseDebug/bundlePrereleaseDebugClassesToCompileJar",
        "../library/build/libs"
    )
    into("build/app-classes")
    include("classes.jar", "library-jvm*.jar")
    // Remove the version
    rename("library-jvm.*.jar", "library-jvm.jar")
}

// Merge the app classes and the library classes into classes.jar
tasks.register<Jar>("makeJar") {
    // Duplicates cause hard to catch errors, better to fail at compile time.
    duplicatesStrategy = DuplicatesStrategy.FAIL
    dependsOn(tasks.getByName("copyJar"))
    from(
        zipTree("build/app-classes/classes.jar"),
        zipTree("build/app-classes/library-jvm.jar")
    )
    destinationDirectory.set(layout.buildDirectory)
    archiveBaseName = "classes"
}

tasks.withType<KotlinJvmCompile> {
    compilerOptions {
        jvmTarget.set(javaTarget)
        jvmDefault.set(JvmDefaultMode.ENABLE)
        optIn.addAll(
            "com.stleo.ott.InternalAPI",
            "com.stleo.ott.Prerelease",
        )
    }
}

dokka {
    moduleName = "App"
    dokkaSourceSets {
        configureEach {
            suppress = name != "prereleaseDebug"
            analysisPlatform = KotlinPlatform.JVM
            displayName = "JVM"
            documentedVisibilities(
                VisibilityModifier.Public,
                VisibilityModifier.Protected
            )

            sourceLink {
                localDirectory = file("..")
                remoteUrl("https://github.com/devanshudara0-cpu/STxLEO_OTT/tree/main")
                remoteLineSuffix = "#L"
            }
        }
    }
}
