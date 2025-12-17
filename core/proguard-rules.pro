# Add project specific ProGuard rules here.

##---------------Begin: proguard configuration for Gson  ----------
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

##---------------Begin: proguard configuration for Retrofit  ----------
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

##---------------Begin: proguard configuration for OkHttp  ----------
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**

##---------------Begin: proguard configuration for Room  ----------
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

##---------------Begin: proguard configuration for SQLCipher  ----------
-keep,includedescriptorclasses class net.sqlcipher.** { *; }
-keep,includedescriptorclasses interface net.sqlcipher.** { *; }

##---------------Begin: proguard configuration for Coroutines  ----------
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

##---------------Begin: proguard configuration for Kotlin  ----------
-keep class kotlin.Metadata { *; }
-keepclassmembers class **$WhenMappings {
    <fields>;
}

##---------------Begin: proguard configuration for Domain Models  ----------
-keep class com.dicoding.moviecatalog.core.domain.model.** { *; }
-keep class com.dicoding.moviecatalog.core.data.source.remote.response.** { *; }
-keep class com.dicoding.moviecatalog.core.data.source.local.entity.** { *; }