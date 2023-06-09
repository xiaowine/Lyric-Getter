package cn.lyric.getter.tool

import android.content.Context
import android.content.Intent
import android.util.Log
import cn.lyric.getter.api.data.DataType
import cn.lyric.getter.api.data.LyricData

object EventTools {
    private const val TAG = "Lyrics Getter Api"

    /**
     * 发送歌词
     *
     * @param [context] Context
     * @param [lyric] 歌词
     * @param [packageName] 音乐包名
     */
    fun sendLyric(context: Context, lyric: String, packageName: String) {
        sendLyric(context, lyric, false, "", false, "", packageName)
    }


    /**
     * 发送歌词
     *
     * @param [context] Context
     * @param [lyric] 歌词
     * @param [customIcon] 是否传入自定义图标
     * @param [base64Icon] Base64图标，仅在customIcon为true时生效
     * @param [useOwnMusicController] 音乐软件自己控制歌词暂停
     * @param [serviceName] 音乐服务名称，仅在useOwnMusicController为false时生效
     * @param [packageName] 音乐包名
     */
    fun sendLyric(
        context: Context,
        lyric: String,
        customIcon: Boolean,
        base64Icon: String,
        useOwnMusicController: Boolean,
        serviceName: String,
        packageName: String
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
        })
    }
}