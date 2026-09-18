# =========================
# Core / Kotlin / Annotations
# =========================
-keepattributes *Annotation*, InnerClasses, Signature, EnclosingMethod, SourceFile, LineNumberTable
-renamesourcefileattribute SourceFile

# =========================
# Android / Kotlin / Coroutines
# =========================
-dontwarn kotlin.**
-dontwarn kotlinx.coroutines.**
-dontwarn org.jetbrains.annotations.**
-dontwarn org.json.**
-dontwarn androidx.**

# =========================
# WebView & JavaScript Bridge
# =========================
-keepattributes JavascriptInterface
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
-keep class com.example.deepexport.data.web.** { *; }

# =========================
# Models & Room
# =========================
-keep class com.example.deepexport.domain.model.** { *; }
-keep class com.example.deepexport.data.local.entity.** { *; }
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# =========================
# ViewModels & Architecture Components
# =========================
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keep class * extends android.app.Application { *; }

# =========================
# Hilt / Dagger
# =========================
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class dagger.** { *; }
-dontwarn dagger.hilt.**
-dontwarn dagger.**
-dontwarn javax.inject.**

# =========================
# Jetpack Compose
# =========================
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}

# =========================
# AndroidX FileProvider
# =========================
-keep class androidx.core.content.FileProvider { *; }
-keep class androidx.core.content.FileProvider$SimplePathStrategy { *; }

