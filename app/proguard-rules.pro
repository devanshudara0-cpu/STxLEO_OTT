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
-keep public class com.lagradost.cloudstream3.ui.AutofitRecyclerView { *; }
-keep public class com.lagradost.cloudstream3.ui.MaxRecyclerView { *; }
-keep public class com.lagradost.cloudstream3.widget.FlowLayout { *; }
-keep public class com.lagradost.cloudstream3.utils.PercentageCropImageView { *; }
-keep public class com.lagradost.cloudstream3.ui.download.button.** { *; }
-keep public class com.lagradost.cloudstream3.ui.player.live.LivePreviewTimeBar { *; }
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
-keep public class com.lagradost.cloudstream3.MainAPI { *; }
-keep public class com.lagradost.cloudstream3.MainAPIKt { *; }
-keep public class com.lagradost.cloudstream3.LoadResponse** { *; }
-keep public class com.lagradost.cloudstream3.SearchResponse** { *; }
-keep public class com.lagradost.cloudstream3.TvType { *; }
-keep public class com.lagradost.cloudstream3.SubtitleFile { *; }
-keep public class com.lagradost.cloudstream3.Episode { *; }
-keep public class com.lagradost.cloudstream3.AnimeSearchResponse { *; }
-keep public class com.lagradost.cloudstream3.MovieSearchResponse { *; }
-keep public class com.lagradost.cloudstream3.TvSeriesSearchResponse { *; }
-keep public class com.lagradost.cloudstream3.AnimeLoadResponse { *; }
-keep public class com.lagradost.cloudstream3.MovieLoadResponse { *; }
-keep public class com.lagradost.cloudstream3.TvSeriesLoadResponse { *; }
-keep public class com.lagradost.cloudstream3.plugins.** { *; }
-keep public class com.lagradost.cloudstream3.utils.ExtractorApi** { *; }
-keep public class com.lagradost.cloudstream3.utils.ExtractorLink** { *; }
-keep public class com.lagradost.cloudstream3.utils.Qualities** { *; }
-keep public class com.lagradost.cloudstream3.APIHolder { *; }
-keep public class com.lagradost.cloudstream3.AcraApplication { *; }
-keep public class com.lagradost.cloudstream3.MainActivity { *; }
-keep public class com.lagradost.cloudstream3.CommonActivity { *; }

# 8. Media3 / ExoPlayer & Cast
-keep class androidx.media3.** { *; }
-keep class com.google.android.gms.cast.** { *; }

# 9. Jetpack Compose
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
}