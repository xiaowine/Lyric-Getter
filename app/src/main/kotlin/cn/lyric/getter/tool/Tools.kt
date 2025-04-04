package cn.lyric.getter.tool

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageInfo
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.widget.RemoteViews
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.IconCompat
import cn.lyric.getter.R
import cn.xiaowine.xkt.LogTool.log
import java.io.DataOutputStream

object Tools {
    var xpActivation: Boolean = false

    val getPhoneName by lazy {
        val marketName = getSystemProperties("ro.product.marketname")
        val vivomarketName = getSystemProperties("ro.vivo.market.name")
        if (bigFirstText(Build.BRAND) == "Vivo") {
            bigFirstText(vivomarketName)
        } else {
            if (marketName.isNotEmpty()) bigFirstText(marketName) else bigFirstText(Build.BRAND) + " " + Build.MODEL
        }
    }

    fun bigFirstText(st: String): String {
        val formattedBrand = st.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }
        return formattedBrand
    }

    fun restartTheScopedSoftware(context: Context) {
        var s = ""
        context.resources.getStringArray(R.array.need_module).forEach {
            s += "${if (it == "com.android.systemui") "killall" else "am force-stop"} $it && "
        }
        s += "echo 0"
        shell(s, true)
    }

    private fun shell(command: String, isSu: Boolean) {
        command.log()
        runCatching {
            if (isSu) {
                val p = Runtime.getRuntime().exec("su")
                val outputStream = p.outputStream
                DataOutputStream(outputStream).apply {
                    writeBytes(command)
                    flush()
                    close()
                }
                outputStream.close()
            } else {
                Runtime.getRuntime().exec(command)
            }
        }
    }

    fun dp2px(context: Context, dpValue: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dpValue,
            context.resources.displayMetrics
        ).toInt()
    }

    fun PackageInfo.getVersionCode() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        longVersionCode.toInt()
    } else {
        @Suppress("DEPRECATION")
        versionCode
    }

    @SuppressLint("PrivateApi")
    fun getSystemProperties(key: String): String {
        val ret: String = try {
            Class.forName("android.os.SystemProperties")
                .getDeclaredMethod("get", String::class.java).invoke(null, key) as String
        } catch (iAE: IllegalArgumentException) {
            throw iAE
        } catch (_: Exception) {
            ""
        }
        return ret
    }

    /**
     * 发送小米澎湃焦点通知
     * @param text 为通知内容
     * @param context 为应用的 context
     * */
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    @SuppressLint("NotificationPermission")
    fun sendNotification(text: String, a: String, context: Context) {
        createNotificationChannel(context)
        val res: Resources = context.resources
        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val builder = NotificationCompat.Builder(context, "lyricgetter")
            .setContentTitle(text)
            .setSmallIcon(IconCompat.createWithResource(context, R.drawable.ic_android_black_24dp))
            .setTicker(text).setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true) // 设置为常驻通知
            .setContentIntent(
                PendingIntent.getActivity(
                    context, 0, launchIntent, PendingIntent.FLAG_MUTABLE
                )
            )

        val remoteViews = RemoteViews(context.packageName, R.layout.focustest_layout)
        remoteViews.setTextViewText(R.id.focustextView, text)
        remoteViews.setImageViewResource(R.id.focusimageView, R.drawable.lycaon_icon)
        val focus = Bundle()
        val cus = Bundle()
        cus.putString("ticker", text)
        cus.putBoolean("enableFloat", true) // 通知是否弹出
        cus.putBoolean("updatable", true)
        cus.putString("aodTitle", text)
        focus.putParcelable("miui.focus.param.custom", cus)
        focus.putString("miui.focus.ticker", text)
        focus.putBoolean("miui.enableFloat", true)
        focus.putBoolean("miui.updatable", true)
        focus.putParcelable("miui.focus.rv", remoteViews)
        focus.putParcelable("miui.focus.rvNight", remoteViews)
        builder.addExtras(focus)
        val notification = builder.build()
        NotificationManagerCompat.from(context).notify("lyricgetter".hashCode(), notification)
    }

    /**
     * 创建通知渠道
     * @param context 为应用的 context
     * */
    private fun createNotificationChannel(context: Context) {
        val notificationManager =
            context.getSystemService("notification") as NotificationManager
        val notificationChannel = NotificationChannel(
            "lyricgetter", "lyricgetter", NotificationManager.IMPORTANCE_MIN
        )
        notificationChannel.setSound(null, null)
        notificationManager.createNotificationChannel(notificationChannel)
    }

    /**
     * 关闭通知
     * @param context 为应用的 context
     * */
    @SuppressLint("NotificationPermission")
    fun cancelNotification(context: Context) {
        (context.getSystemService("notification") as NotificationManager).cancel("lyricgetter".hashCode())
    }
}