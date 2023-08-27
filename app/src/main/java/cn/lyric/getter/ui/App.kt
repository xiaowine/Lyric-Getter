package cn.lyric.getter.ui

import android.app.Application
import cn.lyric.getter.BuildConfig
import cn.lyric.getter.tool.ActivityTools
import cn.xiaowine.xkt.AcTool
import cn.xiaowine.xkt.LogTool
import com.google.android.material.color.DynamicColors

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        AcTool.init(this)
        ActivityTools.context = this
        LogTool.init("Lyrics Getter", { BuildConfig.DEBUG })
    }
}
