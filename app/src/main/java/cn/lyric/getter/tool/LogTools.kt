package cn.lyric.getter.tool

import android.util.Log
import cn.lyric.getter.BuildConfig

object LogTools {
    private const val maxLength = 4000
    const val TAG = "Lyrics Getter"
    private const val XP_TAG = "LSPosed-Bridge"


    fun Any?.log() {
        if (!BuildConfig.DEBUG) return
        val content = if (this is Throwable) Log.getStackTraceString(this) else this.toString()
        if (content.length > maxLength) {
            val chunkCount = content.length / maxLength
            for (i in 0..chunkCount) {
                val max = 4000 * (i + 1)
                val value = if (max >= content.length) {
                    content.substring(maxLength * i)
                } else {
                    content.substring(maxLength * i, max)
                }
                Log.d(TAG, value)
                Log.d(XP_TAG, "$TAG:$value")
            }

        } else {
            Log.d(TAG, content)
            Log.d(XP_TAG, "$TAG:$content")
        }
    }
}
