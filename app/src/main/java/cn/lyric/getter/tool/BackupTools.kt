package cn.lyric.getter.tool

import android.app.Activity
import android.content.SharedPreferences
import android.net.Uri
import cn.xiaowine.xkt.AcTool.showToast
import cn.xiaowine.xkt.Tool.isNotNull
import org.json.JSONObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter


object BackupTools {
    fun handleReadDocument(activity: Activity, sp: SharedPreferences, data: Uri) {
        val edit = sp.edit()
        try {
            activity.contentResolver.openInputStream(data)?.let { loadFile ->
                BufferedReader(InputStreamReader(loadFile)).apply {
                    val sb = StringBuffer()
                    var line = readLine()
                    while (line.isNotNull()) {
                        sb.append(line)
                        line = readLine()
                    }
                    val read = sb.toString()
                    JSONObject(read).apply {
                        val key = keys()
                        while (key.hasNext()) {
                            val keys = key.next()
                            when (val value = get(keys)) {
                                is String -> {
                                    if (value.startsWith("Float:")) {
                                        edit.putFloat(keys, value.substring(value.indexOf("Float:")).toFloat() / 1000)
                                    } else {
                                        edit.putString(keys, value)
                                    }
                                }

                                is Boolean -> edit.putBoolean(keys, value)
                                is Int -> edit.putInt(keys, value)
                            }
                        }
                    }
                    close()
                }
            }
            edit.apply()
            "load ok".showToast()
        } catch (e: Exception) {
            "load fail\n${e.message}".showToast()
        }
    }

    fun handleCreateDocument(activity: Activity, sp: SharedPreferences, data: Uri) {
        try {
            activity.contentResolver.openOutputStream(data)?.let { saveFile ->
                BufferedWriter(OutputStreamWriter(saveFile)).apply {
                    write(JSONObject().also {
                        for (entry: Map.Entry<String, *> in sp.all) {
                            when (entry.value) {
                                Float -> it.put(entry.key, "Float:" + (entry.value as Float * 1000).toInt().toString())
                                else -> it.put(entry.key, entry.value)
                            }
                        }
                    }.toString())
                    close()
                }
            }
            "save ok".showToast()
        } catch (e: Exception) {
            "save fail\n${e.message}".showToast()
        }
    }
}