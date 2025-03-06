package cn.lyric.getter.hook.app

import android.os.Build
import cn.lyric.getter.hook.BaseHook
import cn.lyric.getter.tool.HookTools
import cn.lyric.getter.tool.HookTools.dexKitBridge
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.ClassUtils.setStaticObject
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder

object Oppo : BaseHook() {
    init {
        System.loadLibrary("dexkit")
    }
    override fun init() {
        loadClass("android.os.SystemProperties").methodFinder().first { name == "get" }.createHook {
            after {
                setStaticObject(Build::class.java, "BRAND", "oppo")
                setStaticObject(Build::class.java, "MANUFACTURER", "Oppo")
                setStaticObject(Build::class.java, "DISPLAY", "Color")

            }
        }
        HookTools.mediaMetadataCompatLyric()
        HookTools.getApplication {
            dexKitBridge(it.classLoader) { dexKitBridge ->
                val sboppo = dexKitBridge.findClass {
                    matcher {
                        usingStrings("非蓝牙连接成功自动播放的场景不需要拦截")
                    }
                }.single()
                loadClass("com.allsaints.localpush.LocalPushReceiver").methodFinder().first{ name == "goto" }.createHook {
                    after{ params ->
                        //" 测试1 ${params.result }".log()
                        //" 测试1传入 ${Arrays.toString(params.args)  }".log()
                    }
                }
                loadClass(sboppo.name).methodFinder().first{ name == "b" }.createHook {
                    after{ params ->
                        //" 测试2 ${params.result }".log()
                        //" 测试2传入 ${Arrays.toString(params.args)  }".log()
                    }
                }
                loadClass("com.allsaints.music.player.thirdpart.MediaSessionHelper").methodFinder().first{ name == "do" }.createHook {
                    after{ params ->
                        //" 测试3 ${params.result }".log()
                        //" 测试3传入 ${Arrays.toString(params.args)  }".log()
                    }
                }
                loadClass("com.allsaints.music.player.thirdpart.MediaSessionHelper").methodFinder().first{ name == "G" }.createHook {
                    after{ params ->
                        //" 测试4 ${params.result} }".log()
                        //" 测试4传入 ${Arrays.toString(params.args)  }".log()
                    }
                    before {
                        val field = it.thisObject.javaClass.getDeclaredField("b")
                        field.isAccessible = true // 确保可以访问私有字段
                        //field.setBoolean(it.thisObject, true) // 设置值为 true
                        val fieldc = it.thisObject.javaClass.getDeclaredField("const")
                        fieldc.isAccessible = true // 确保可以访问私有字段
                        //fieldc.setBoolean(it.thisObject, true) // 设置值为 true
                        val fieldd = it.thisObject.javaClass.getDeclaredField("catch")
                        fieldd.isAccessible = true // 确保可以访问私有字段
                        //fieldd.setBoolean(it.thisObject, true) // 设置值为 true
                    }
                }
                loadClass("com.allsaints.music.player.thirdpart.MediaSessionHelper").methodFinder().first{ name == "F" }.createHook {
                    after{ params ->
                        //" 测试5 ${params.result}}".log()
                        //" 测试5传入 ${Arrays.toString(params.args)  }".log()
                    }
                }
                loadClass("com.allsaints.music.player.thirdpart.MediaSessionHelper").methodFinder().first{ name == "class" }.createHook {
                    after{ params ->
                        //" 测试6 ${{params.result}}".log()
                        //" 测试6传入 ${Arrays.toString(params.args)  }".log()
                    }
                }
                loadClass("com.allsaints.music.player.thirdpart.MediaSessionHelper").methodFinder().first{ name == "C" }.createHook {
                    after{ params ->
                        //" 测试7 ${params.result}".log()
                        //" 测试7传入 ${Arrays.toString(params.args)  }".log()
                    }
                }
                loadClass("com.allsaints.music.player.PlayManager").methodFinder().first{ name == "z4" }.createHook {
                    after{ params ->
                        //" 测试8 ${params.result}".log()
                        //" 测试8传入 ${Arrays.toString(params.args)  }".log()
                    }
                }
                loadClass("com.allsaints.music.player.PlayManager").methodFinder().first{ name == "y4" }.createHook {
                    after{ params ->
                        //" 测试9 ${params.result}".log()
                        //" 测试9传入 ${Arrays.toString(params.args)  }".log()
                    }
                }
                /** 强开蓝牙歌词 */
                loadClass(sboppo.name).methodFinder().first{ name == "l" }.createHook {
                    after{ params ->
                        //" 测试10 ${params.result}".log()
                        params.result = true
                       // " 测试10传入 ${Arrays.toString(params.args)  }".log()
                    }
                }
            }
        }
    }
}