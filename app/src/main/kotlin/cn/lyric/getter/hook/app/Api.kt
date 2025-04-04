package cn.lyric.getter.hook.app

import android.media.MediaMetadata
import cn.lyric.getter.BuildConfig
import cn.lyric.getter.api.data.ExtraData
import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.HookTools.eventTools
import cn.lyric.getter.tool.HookTools.isApi
import cn.xiaowine.xkt.LogTool.log
import cn.xiaowine.xkt.Tool.isNot
import cn.xiaowine.xkt.Tool.isNotNull
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.github.kyuubiran.ezxhelper.finders.ConstructorFinder.`-Static`.constructorFinder
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder

object Api : BaseHook() {
    override fun init() {
        super.init()
        hook()
    }

    fun hook(classLoader: ClassLoader? = null) {
        isApi(classLoader) { clazz ->
            clazz.constructorFinder().first().createHook {
                before { hookParam ->
                    hookParam.thisObject.objectHelper().getObjectOrNullAs<Int>("API_VERSION").isNotNull { version ->
                        if (version == BuildConfig.API_VERSION || version == 7 /* 不知名 API 版本 */) {
                            hookParam.thisObject.objectHelper().setObject("hasEnable", true)
                            clazz.methodFinder().first { name == "onMediaData" }.isNotNull {
                                it.createHook {
                                    after { hookParam ->
                                        val metadata = hookParam.args[0] as MediaMetadata

                                        eventTools.sendMediaData(ExtraData().apply {
                                            this.packageName = packageName
                                            this.mediaMetadata = metadata
                                            this.artist = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST) ?: "Unknown Artist"
                                            this.album = metadata.getString(MediaMetadata.METADATA_KEY_ALBUM) ?: "Unknown Album"
                                            this.title = metadata.getString(MediaMetadata.METADATA_KEY_TITLE) ?: "Unknown Title"
                                        })
                                    }
                                }
                            }
                            clazz.methodFinder().first { name == "sendLyric" }.createHook {
                                after { hookParam ->
                                    val extraData = ExtraData()
                                    val extra = (hookParam.args[1]).objectHelper().getObjectOrNullAs<HashMap<String, Any>>("extra")!!
                                    extraData.mergeExtra(extra)
                                    eventTools.sendLyric(
                                        hookParam.args[0] as String,
                                        extraData
                                    )
                                }
                            }
                            clazz.methodFinder().first { name == "clearLyric" }.createHook {
                                after {
                                    eventTools.cleanLyric()
                                }
                            }
                            return@before
                        }
                    }
                    "The APIs do not match".log()
                }
            }

        }.isNot {
            "Not found Api class".log()
        }
    }
}