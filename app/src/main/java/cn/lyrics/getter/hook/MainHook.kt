package cn.lyrics.getter.hook

import cn.lyrics.getter.hook.app.APlayer
import cn.lyrics.getter.hook.app.Api
import cn.lyrics.getter.hook.app.Apple
import cn.lyrics.getter.hook.app.Kugou
import cn.lyrics.getter.hook.app.Kuwo
import cn.lyrics.getter.hook.app.Luna
import cn.lyrics.getter.hook.app.Meizu
import cn.lyrics.getter.hook.app.MiPlayer
import cn.lyrics.getter.hook.app.MobileMusic
import cn.lyrics.getter.hook.app.Netease
import cn.lyrics.getter.hook.app.QQMusic
import cn.lyrics.getter.hook.app.RPlayer
import cn.lyrics.getter.hook.app.SystemUi
import cn.lyrics.getter.hook.app.Toside
import cn.lyrics.getter.tool.Tools.TAG
import com.github.kyuubiran.ezxhelper.EzXHelper
import com.github.kyuubiran.ezxhelper.Log
import com.github.kyuubiran.ezxhelper.LogExtensions.logexIfThrow
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage


private const val systemUiPackage = "com.android.systemui"

class MainHook : IXposedHookLoadPackage, IXposedHookZygoteInit /* Optional */ {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {

        EzXHelper.initHandleLoadPackage(lpparam)
        EzXHelper.setLogTag(TAG)
        EzXHelper.setToastTag(TAG)
        when (lpparam.packageName) {
            systemUiPackage -> initHooks(SystemUi)
            "com.tencent.qqmusic" -> initHooks(QQMusic)
            "com.miui.player" -> initHooks(MiPlayer)
            "com.netease.cloudmusic" -> initHooks(Netease)
            "com.kugou.android", "com.kugou.android.lite" -> initHooks(Kugou)
            "cn.kuwo.player" -> initHooks(Kuwo)
            "remix.myplayer" -> initHooks(APlayer)
            "cmccwm.mobilemusic" -> initHooks(MobileMusic)
            "com.meizu.media.music" -> initHooks(Meizu)
            "com.r.rplayer" -> initHooks(RPlayer)
            "cn.toside.music.mobile" -> initHooks(Toside)
            "com.apple.android.music" -> initHooks(Apple)
            "com.luna.music" -> initHooks(Luna)
            else -> initHooks(Api)
        }
    }

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        EzXHelper.initZygote(startupParam)
    }

    private fun initHooks(vararg hook: BaseHook) {
        hook.forEach {
            runCatching {
                if (it.isInit) return@forEach
                it.init()
                it.isInit = true
                Log.i("Inited hook: ${it.javaClass.name}")
            }.logexIfThrow("Failed init hook: ${it.javaClass.name}")
        }
    }
}