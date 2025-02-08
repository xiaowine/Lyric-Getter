package cn.lyric.getter.ui

import android.app.Application
import cn.lyric.getter.BuildConfig
import cn.lyric.getter.tool.ActivityTools
import cn.lyric.getter.tool.ConfigTools.config
import cn.lyric.getter.tool.Tools.xpActivation
import cn.xiaowine.dsp.DSP
import cn.xiaowine.dsp.data.MODE
import cn.xiaowine.xkt.AcTool
import cn.xiaowine.xkt.LogTool
import com.google.android.material.color.DynamicColors

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        AcTool.init(this)
        ActivityTools.application = this
        LogTool.init("Lyrics Getter", { BuildConfig.DEBUG })
        xpActivation = DSP.init(this, BuildConfig.APPLICATION_ID, MODE.HOOK, false)
        if (xpActivation) config.updateTime = System.currentTimeMillis()
    }
}
