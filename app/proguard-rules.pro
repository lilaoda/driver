# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android Developer\sdk/tools/proguard/proguard-android.txt
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

#-----------------------------项目中的实体类-----------------------------------------#
#项目中的bean实体类不混淆
-keep class bus.driver.bean.** {*;}
-keep class bus.driver.bean.param.{*;}
-keep class bus.driver.bean.event$* { *; }
-keep class bus.driver.data.remote.HttpResult{*;}
#保持本地数据库表不混淆
-keep class bus.driver.data.local.entity.** {*;}

#-----------------------------第三方jar包-----------------------------------------#
#butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * { @butterknife.* <fields>;}
-keepclasseswithmembernames class * { @butterknife.* <methods>;}

#commons-beanutils-1.9.3.jar
-keep class org.apache.commons.beanutils.**{*;}
-dontwarn org.apache.commons.beanutils.**

# OrmLite 混淆代码
#-keep class com.j256.ormlite.** {*;}
#-dontwarn com.j256.ormlite.**
-keep class com.j256.**
-keepclassmembers class com.j256.** { *; }
-keep enum com.j256.**
-keepclassmembers enum com.j256.** { *; }
-keep interface com.j256.**
-keepclassmembers interface com.j256.** { *; }
-keepattributes *DatabaseField*
-keepattributes *DatabaseTable*
-keepattributes *SerializedName*
#-keepclassmembersclass * {@com.j256.ormlite.field.DatabaseField*;}

#greenDao
-keep class org.greenrobot.greendao.**{*;}
-keep public interface org.greenrobot.greendao.**
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties
-keep class data.db.dao.*$Properties {
    public static <fields>;
}
-keepclassmembers class data.db.dao.** {
    public static final <fields>;
  }
-keep class net.sqlcipher.database.**{*;}
-keep public interface net.sqlcipher.database.**
-dontwarn net.sqlcipher.database.**
-dontwarn org.greenrobot.greendao.**

#BaseRecylcerViewHelper
-keep class com.chad.library.adapter.** {*;}
-keep public class * extends com.chad.library.adapter.base.BaseQuickAdapter
-keep public class * extends com.chad.library.adapter.base.BaseViewHolder
-keepclassmembers  class **$** extends com.chad.library.adapter.base.BaseViewHolder {
     <init>(...);
}

# LeakCanary
-keep class com.squareup.leakcanary.** { *; }

#guava
#-dontwarn sun.misc.Unsafe
#-keep class com.google.common.** { *; }

# Guava 19.0
-dontwarn java.lang.ClassValue
-dontwarn com.google.j2objc.annotations.Weak
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

#picasso
-keep class com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**

#picasso
-keep class com.squareup.picasso.** { *; }
-dontwarn com.squareup.picasso.**

#logger
-dontwarn com.orhanobut.logger.**
-keep class com.orhanobut.logger.**{ *;}

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.** { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }
-dontwarn com.google.gson.**

#xUtils
-keep class com.lidroid.xutils.** { *; }
-keep public interface com.lidroid.xutils.** {*;}
-dontwarn com.lidroid.xutils.**

#rxjava
-dontwarn rx.**
-keep class rx.** { *; }

#retrofit2
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

#rxlifecycle
-dontwarn com.trello.**
-keep class com.trello.** { *; }

#rxbinding
-dontwarn com.jakewharton.**
-keep class com.jakewharton.** { *; }

# filedownloader uses okhttp3-lib, so need add below proguard rules.
#-dontwarn okhttp3.*
#-dontwarn okio.**

#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}

#okio
-dontwarn okio.**
-keep class okio.**{*;}

# RxJava RxAndroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

-dontwarn javax.annotation.**
-dontwarn javax.inject.**

 # eventbus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

#3D 地图 V5.0.0之后：
-keep   class com.amap.api.maps.**{*;}
-keep   class com.autonavi.**{*;}
-keep   class com.amap.api.trace.**{*;}

#定位
-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}

#搜索
-keep   class com.amap.api.services.**{*;}

#2D地图
-keep class com.amap.api.maps2d.**{*;}
-keep class com.amap.api.mapcore2d.**{*;}

#导航
-keep class com.amap.api.navi.**{*;}
-keep class com.autonavi.**{*;}

#-----------------------------基本指令区-------------------------------------------#
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-dontoptimize
-verbose
-ignorewarning
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions,InnerClasses
-keepattributes SourceFile,LineNumberTable
-keepattributes *JavascriptInterface*
-keepattributes EnclosingMethod
-optimizations !code/simplification/cast,!field/*,!class/merging/*
#记录混淆日志输出
-dump proguard/class_files.txt
-printseeds proguard/seeds.txt
-printusage proguard/unused.txt
-printmapping proguard/mapping.txt

#-----------------------------常规类------------------------------------------#
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

#support.v4
-keep class android.support.** { *; }
-keep class android.support.v4.** { *; }
-keep public class * extends android.support.v4.**
-keep interface android.support.v4.app.** { *; }
-dontwarn android.support.**

#-keep class android.support.v7.** { *; }
#-keep public class * extends android.support.v7.**
#-keep interface android.support.v7.app.** { *; }

#-support-v7-appcompat.pro
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }
-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}

# 保持 native 方法和资源
-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclassmembers class **.R$* {
    public static <fields>;
}

-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}
-keepclassmembers class * {
    public void *ButtonClicked(android.view.View);
}
-keepclassmembers class * {
    void *(**On*Event);
}

# 保持自定义控件类
-keepclassmembers public class * extends android.view.View {
    void set*(***);
    *** get*();
}
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

#保持Parcelable、Serializable、enum
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator CREATOR;
}
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-keepclassmembers enum * {
  public static **[] values();
  public static ** valueOf(java.lang.String);
}

#---------------------------------webview---------------------------------------#
-keepclassmembers class fqcn.of.javascript.interface.for.Webview {
   public *;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, jav.lang.String);
}

#---------------------------------webview与js互相调用的类---------------------------------#


#---------------------------------反射相关的类和方法--------------------------------------#
















