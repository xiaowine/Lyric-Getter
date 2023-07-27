package cn.lyric.getter.tool

import android.content.Context
import android.content.Intent
import android.util.Log
import cn.lyric.getter.api.data.DataType
import cn.lyric.getter.api.data.LyricData

object EventTools {
    const val TAG = "Lyrics Getter"

    fun sendLyric(context: Context, lyric: String) {
        sendLyric(context, lyric, false, "", false, "", context.packageName, 0)
    }

    fun sendLyric(context: Context, lyric: String, delay: Int) {
        sendLyric(context, lyric, false, "", false, "", context.packageName, delay)
    }


    /**
     * 发送歌词
     *
     * @param context               Context
     * @param lyric                 歌词
     * @param customIcon            是否传入自定义图标
     * @param base64Icon            Base64图标，仅在customIcon为true时生效
     * @param useOwnMusicController 音乐软件自己控制歌词暂停
     * @param serviceName           音乐服务名称，仅在useOwnMusicController为false时生效
     * @param packageName           音乐包名
     * @param delay                 歌词显示时间，单位ms，默认0
     */
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