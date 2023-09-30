package cn.lyric.getter.tool

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import cn.lyric.getter.api.data.DataType
import cn.lyric.getter.api.data.LyricData
import cn.lyric.getter.tool.ConfigTools.xConfig
import cn.xiaowine.xkt.Tool.observableChange
import cn.xiaowine.xkt.Tool.regexReplace

@SuppressLint("StaticFieldLeak")
object EventTools {
    lateinit var context: Context

    private var lastLyricData: LyricData? by observableChange(null) { _, _, newValue ->
        newValue?.run {
            val regexReplace = lyric.regexReplace(xConfig.regexReplace, "")
            if (regexReplace.isEmpty()) {
                cleanLyric(context)
            } else {
                context.sendBroadcast(Intent().apply {
                    action = "Lyric_Data"
                    putExtra("Data", newValue.apply {
                        lyric = regexReplace
                    })
                })
                Log.d(TAG, this.toString())
            }
        }
    }
    private const val TAG = "Lyrics Getter"

    fun sendLyric(context: Context, lyric: String) {
        sendLyric(context, lyric, false, "", false, "", context.packageName, 0, null)
    }

    fun sendLyric(context: Context, lyric: String, delay: Int) {
        sendLyric(context, lyric, false, "", false, "", context.packageName, delay, null)
    }

    fun sendLyric(context: Context, lyric: String, customIcon: Boolean, base64Icon: String, useOwnMusicController: Boolean, serviceName: String, packageName: String, delay: Int) {
        sendLyric(context, lyric, customIcon, base64Icon, useOwnMusicController, serviceName, packageName, delay, null)
    }
    fun sendLyric(
        context: Context,
        lyric: String,
        customIcon: Boolean,
        base64Icon: String,
        useOwnMusicController: Boolean,
        serviceName: String,
        packageName: String,
        delay: Int,
        extra: HashMap<String, Any>?
    ) {
        this.context = context
        lastLyricData = LyricData().apply {
            this.type = DataType.UPDATE
            this.lyric = lyric
            this.customIcon = customIcon
            this.base64Icon = base64Icon
            this.useOwnMusicController = useOwnMusicController
            this.serviceName = serviceName
            this.packageName = packageName
            this.delay = delay
            this.extra = extra
        }
    }


    fun cleanLyric(context: Context) {
        context.sendBroadcast(Intent().apply {
            action = "Lyric_Data"
            val lyricData = LyricData().apply {
                this.type = DataType.STOP
            }
            putExtra("Data", lyricData)
            Log.d(TAG, lyricData.toString())
        })
    }
}