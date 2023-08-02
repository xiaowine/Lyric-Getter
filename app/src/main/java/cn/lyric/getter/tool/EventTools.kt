package cn.lyric.getter.tool

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import cn.lyric.getter.api.data.DataType
import cn.lyric.getter.api.data.LyricData

@SuppressLint("StaticFieldLeak")
object EventTools {
    lateinit var context: Context

    private var lastLyricData: LyricData? by Tools.observableChange(null) { _, newValue ->
        newValue?.run {
            if (lyric.isEmpty()) return@observableChange
            context.sendBroadcast(Intent().apply {
                action = "Lyric_Data"
                putExtra("Data", newValue)
            })
            Log.d(TAG, this.toString())
        }
    }
    private const val TAG = "Lyrics Getter"

    fun sendLyric(context: Context, lyric: String) {
        sendLyric(context, lyric, false, "", false, "", context.packageName, 0)
    }

    fun sendLyric(context: Context, lyric: String, delay: Int) {
        sendLyric(context, lyric, false, "", false, "", context.packageName, delay)
    }


    fun sendLyric(
        context: Context,
        lyric: String,
        customIcon: Boolean,
        base64Icon: String,
        useOwnMusicController: Boolean,
        serviceName: String,
        packageName: String,
        delay: Int
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