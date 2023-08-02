package cn.lyric.getter.tool

import android.content.Context
import android.content.Intent
import android.util.Log
import cn.lyric.getter.api.data.DataType
import cn.lyric.getter.api.data.LyricData
import cn.lyric.getter.config.XposedOwnSP.config
import cn.lyric.getter.tool.Tools.isNotNull

object EventTools {
    private var lastLyricData: LyricData? = null
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
        if (lyric.isEmpty()) return
        if (lastLyricData.isNotNull()) {
            if (config.outputRepeatedLyrics || lastLyricData!!.lyric == lyric && lastLyricData!!.customIcon == customIcon && lastLyricData!!.base64Icon == base64Icon && lastLyricData!!.useOwnMusicController == useOwnMusicController && lastLyricData!!.serviceName == serviceName && lastLyricData!!.packageName == packageName && lastLyricData!!.delay == delay) return
        }
        context.sendBroadcast(Intent().apply {
            action = "Lyric_Data"
            val lyricData = LyricData().apply {
                this.type = DataType.UPDATE
                this.lyric = lyric
                this.customIcon = customIcon
                this.base64Icon = base64Icon
                this.useOwnMusicController = useOwnMusicController
                this.serviceName = serviceName
                this.packageName = packageName
                this.delay = delay
            }
            lastLyricData = lyricData
            putExtra("Data", lyricData)
            Log.d(TAG, lyricData.toString())
        })
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