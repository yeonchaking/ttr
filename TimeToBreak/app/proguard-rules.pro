# Keep the MyApplication class and its members
-keep class com.greentea.ttb.MyApplication { *; }

# Keep the MainActivity class and its members
-keep class com.greentea.ttb.MainActivity { *; }

# Hilt 관련 규칙
-keep class dagger.hilt.internal.** { *; }
-keep class com.greentea.ttb.Hilt_** { *; }
-dontwarn dagger.hilt.internal.**
-dontwarn com.google.common.collect.**

# Room 관련 규칙
-keep class androidx.room.RoomDatabase { *; }
-keepclassmembers class androidx.room.RoomDatabase {
    public static final java.util.Map<java.lang.String, java.lang.String> MIGRATION;
}
-dontwarn androidx.room.RoomDatabase
