# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/james/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-dontwarn ch.qos.logback.**
-keep class ch.qos.logback.** {*;}
-dontwarn org.glassfish.**
-keep class org.glassfish.** {*;}
-dontwarn com.google.common.**
-keep class com.google.common.** {*;}
-dontwarn com.ullink.slack.**
-keep class com.ullink.slack.** {*;}
-dontwarn org.apache.http.**
-keep class org.apache.http.** {*;}