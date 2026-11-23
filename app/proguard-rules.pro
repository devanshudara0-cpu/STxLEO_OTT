# ==========================================
# LEOVONIX Production ProGuard & R8 Rules
# Target: Maximum Obfuscation & Anti-Decompile Protection
# Repackage into: com.stleo.ott.internal
# ==========================================

# 1. Repackage all obfuscated classes into com.stleo.ott.internal
-repackageclasses 'com.stleo.ott.internal'
-allowaccessmodification
-dontusemixedcaseclassnames

# 2. General optimization & warnings suppression
-dontwarn **
-ignorewarnings
-dontnote **

# 3. Preserve essential attributes for annotations & reflection
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# 4. Android Framework Essentials
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference

-keepclassmembers class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Preserve custom views inflated from XML layouts
-keep public class com.stleo.ott.ui.AutofitRecyclerView { *; }
-keep public class com.stleo.ott.ui.MaxRecyclerView { *; }
-keep public class com.stleo.ott.widget.FlowLayout { *; }
-keep public class com.stleo.ott.utils.PercentageCropImageView { *; }
-keep public class com.stleo.ott.ui.download.button.** { *; }
-keep public class com.stleo.ott.ui.player.live.LivePreviewTimeBar { *; }
-keep public class com.discord.panels.** { *; }

# 5. JNI & Native Methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# 6. Jackson & Kotlinx Serialization Data Classes (CRITICAL FOR PARSING)
-keepclassmembers class * {
    @com.fasterxml.jackson.annotation.JsonProperty *;
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties *;
    @kotlinx.serialization.SerialName *;
    @kotlinx.serialization.Serializable *;
}
-keep class kotlinx.serialization.** { *; }
-keepclassmembers class **$$serializer {
    *;
}
-keepclassmembers class * {
    *** Companion;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 7. LEOVONIX Plugin Runtime Bridge (CRITICAL: Keeps Movie/Series plugins working)
-keep public class com.stleo.ott.MainAPI { *; }
-keep public class com.stleo.ott.MainAPIKt { *; }
-keep public class com.stleo.ott.LoadResponse** { *; }
-keep public class com.stleo.ott.SearchResponse** { *; }
-keep public class com.stleo.ott.TvType { *; }
-keep public class com.stleo.ott.SubtitleFile { *; }
-keep public class com.stleo.ott.Episode { *; }
-keep public class com.stleo.ott.AnimeSearchResponse { *; }
-keep public class com.stleo.ott.MovieSearchResponse { *; }
-keep public class com.stleo.ott.TvSeriesSearchResponse { *; }
-keep public class com.stleo.ott.AnimeLoadResponse { *; }
-keep public class com.stleo.ott.MovieLoadResponse { *; }
-keep public class com.stleo.ott.TvSeriesLoadResponse { *; }
-keep public class com.stleo.ott.plugins.** { *; }
-keep public class com.stleo.ott.utils.ExtractorApi** { *; }
-keep public class com.stleo.ott.utils.ExtractorLink** { *; }
-keep public class com.stleo.ott.utils.Qualities** { *; }
-keep public class com.stleo.ott.APIHolder { *; }
-keep public class com.stleo.ott.AcraApplication { *; }
-keep public class com.stleo.ott.MainActivity { *; }
-keep public class com.stleo.ott.CommonActivity { *; }

# 8. Media3 / ExoPlayer & Cast
-keep class androidx.media3.** { *; }
-keep class com.google.android.gms.cast.** { *; }

# 9. Jetpack Compose
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
}