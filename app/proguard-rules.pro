# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Keep entry points
-keep class com.fyp.cls.activities.LoadingActivity { *; }
-keep class com.fyp.cls.activities.SignIn { *; }
-keep class com.fyp.cls.activities.MainActivity { *; }
# Add similar rules for other entry point activities

# Keep critical classes that should not be obfuscated
-keep class com.fyp.cls.fireBase.MessagingService { *; }
# Add other critical class rules here

# Keep classes from specific libraries
-keep class com.intuit.** { *; }
-keep class com.makeramen.** { *; }
-keep class com.google.firebase.** { *; }
-keep class de.hdodenhof.** { *; }
-keep class org.apache.commons.** { *; }
-keep class com.squareup.picasso.** { *; }
-keep class com.facebook.shimmer.** { *; }
-keep class com.google.android.gms.** { *; }
-keep class androidx.camera.** { *; }
-keep class com.github.clans.** { *; }
-keep class com.github.bumptech.glide.** { *; }
-keep class org.jsoup.** { *; }
# Add similar rules for other libraries you're using

# Keep custom Serializable and Parcelable classes
-keep class com.fyp.cls.activities.** implements java.io.Serializable
-keep class com.fyp.cls.activities.** implements android.os.Parcelable
# Add other rules for your custom Serializable/Parcelable classes

# Preserve names of methods for annotations
-keepclassmembers class * {
    @androidx.annotation.* <methods>;
}

# Preserve Google Play Services Application ID
-keepnames class com.google.android.gms.common.GooglePlayServicesUtil {*;}

# Remove logging calls in release builds
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

-printmapping proguard-mapping.txt