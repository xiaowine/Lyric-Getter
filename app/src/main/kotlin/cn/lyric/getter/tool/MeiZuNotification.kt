package cn.lyric.getter.tool

import android.app.Notification

object MeiZuNotification : Notification() {
    const val FLAG_ALWAYS_SHOW_TICKER_HOOK = 0x01000000
    const val FLAG_ONLY_UPDATE_TICKER_HOOK = 0x02000000
    const val FLAG_ALWAYS_SHOW_TICKER = 0x01000000
    const val FLAG_ONLY_UPDATE_TICKER = 0x02000000
}