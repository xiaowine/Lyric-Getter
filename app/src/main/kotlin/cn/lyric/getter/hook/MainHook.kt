package cn.lyric.getter.hook

import cn.lyric.getter.BuildConfig
import cn.lyric.getter.hook.app.APlayer
import cn.lyric.getter.hook.app.Api
import cn.lyric.getter.hook.app.Apple
import cn.lyric.getter.hook.app.Aqzscn
import cn.lyric.getter.hook.app.Bodian
import cn.lyric.getter.hook.app.Flamingo
import cn.lyric.getter.hook.app.Gramophone
import cn.lyric.getter.hook.app.Huawei
import cn.lyric.getter.hook.app.Kde
import cn.lyric.getter.hook.app.Kugou
import cn.lyric.getter.hook.app.Kuwo
import cn.lyric.getter.hook.app.LMusic
import cn.lyric.getter.hook.app.Luna
import cn.lyric.getter.hook.app.Meizu
import cn.lyric.getter.hook.app.MiPlayer
import cn.lyric.getter.hook.app.Mimicry
import cn.lyric.getter.hook.app.MobileMusic
import cn.lyric.getter.hook.app.MusicFree
import cn.lyric.getter.hook.app.Netease
import cn.lyric.getter.hook.app.NeteaseLite
import cn.lyric.getter.hook.app.QQMusic
import cn.lyric.getter.hook.app.Qinalt
import cn.lyric.getter.hook.app.RPlayer
import cn.lyric.getter.hook.app.Salt
import cn.lyric.getter.hook.app.MusicPlayer
import cn.lyric.getter.hook.app.Oppo
import cn.lyric.getter.hook.app.Poweramp
import cn.lyric.getter.hook.app.SystemUi
import cn.lyric.getter.hook.app.Toside
import cn.xiaowine.xkt.LogTool
import cn.xiaowine.xkt.LogTool.log
import com.github.kyuubiran.ezxhelper.EzXHelper
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage

class MainHook : IXposedHookLoadPackage, IXposedHookZygoteInit {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        EzXHelper.initHandleLoadPackage(lpparam)
        LogTool.init("Lyrics Getter", { BuildConfig.DEBUG }, BuildConfig.DEBUG)
        when (lpparam.packageName) {
            "com.android.systemui" -> initHooks(SystemUi)
            "com.tencent.qqmusic" -> initHooks(QQMusic)
            "com.miui.player" -> initHooks(MiPlayer)
            "com.netease.cloudmusic" -> initHooks(Netease)
            "com.netease.cloudmusic.lite" -> initHooks(NeteaseLite)
            "com.kugou.android", "com.kugou.android.lite" -> initHooks(Kugou)
            "cn.kuwo.player" -> initHooks(Kuwo)
            "remix.myplayer" -> initHooks(APlayer)
            "cmccwm.mobilemusic" -> initHooks(MobileMusic)
            "com.meizu.media.music" -> initHooks(Meizu)
            "com.r.rplayer" -> initHooks(RPlayer)
            "cn.toside.music.mobile" -> initHooks(Toside)
            "com.apple.android.music" -> initHooks(Apple)
            "com.luna.music" -> initHooks(Luna)
            "com.xuncorp.qinalt.music" -> initHooks(Qinalt)
            "com.xuncorp.suvine.music", "com.salt.music" -> initHooks(Salt)
            "com.hihonor.cloudmusic" -> initHooks(Netease)
            "cn.aqzscn.stream_music" -> initHooks(Aqzscn)
            "com.lalilu.lmusic" -> initHooks(LMusic)
            "cn.wenyu.bodian" -> initHooks(Bodian)
            "fun.upup.musicfree" -> initHooks(MusicFree)
            "com.mimicry.mymusic" -> initHooks(Mimicry)
            "yos.music.player" -> initHooks(Flamingo)
            "org.kde.kdeconnect_tp" -> initHooks(Kde)
            "com.huawei.music" -> initHooks(Huawei)
            "org.akanework.gramophone" -> initHooks(Gramophone)
            "music.hifistatus" -> initHooks(MusicPlayer)
            "com.heytap.music" -> initHooks(Oppo)
            "com.oppo.music" -> initHooks(Oppo)
            "com.maxmpz.audioplayer" -> initHooks(Poweramp)
            else -> initHooks(Api)
        }
    }

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        EzXHelper.initZygote(startupParam)
    }

    private fun initHooks(vararg hook: BaseHook) {
        hook.forEach {
            try {
                if (it.isInit) return@forEach
                it.init()
                it.isInit = true
                "Inited hook: ${it.javaClass.name}, Package Name: ${EzXHelper.hostPackageName}".log()
            } catch (e: Exception) {
                e.printStackTrace()
                "Init hook ${it.javaClass.name} failed".log()
            }
        }
    }
}
