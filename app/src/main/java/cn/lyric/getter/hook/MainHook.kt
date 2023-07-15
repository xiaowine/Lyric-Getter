package cn.lyric.getter.hook

import cn.lyric.getter.hook.app.Salt
import cn.lyric.getter.hook.app.APlayer
import cn.lyric.getter.hook.app.Api
import cn.lyric.getter.hook.app.Apple
import cn.lyric.getter.hook.app.Kugou
import cn.lyric.getter.hook.app.Kuwo
import cn.lyric.getter.hook.app.Luna
import cn.lyric.getter.hook.app.Meizu
import cn.lyric.getter.hook.app.MiPlayer
import cn.lyric.getter.hook.app.MobileMusic
import cn.lyric.getter.hook.app.Netease
import cn.lyric.getter.hook.app.QQMusic
import cn.lyric.getter.hook.app.RPlayer
import cn.lyric.getter.hook.app.SystemUi
import cn.lyric.getter.hook.app.Toside
import cn.lyric.getter.tool.Tools.TAG
import com.github.kyuubiran.ezxhelper.EzXHelper
import com.github.kyuubiran.ezxhelper.Log
import com.github.kyuubiran.ezxhelper.LogExtensions.logexIfThrow
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage


class MainHook : IXposedHookLoadPackage, IXposedHookZygoteInit /* Optional */ {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {

        EzXHelper.initHandleLoadPackage(lpparam)
        EzXHelper.setLogTag(TAG)
        EzXHelper.setToastTag(TAG)
        when (lpparam.packageName) {
            "com.android.systemui" -> initHooks(SystemUi)
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
            "com.salt.music" -> initHooks(Salt)
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