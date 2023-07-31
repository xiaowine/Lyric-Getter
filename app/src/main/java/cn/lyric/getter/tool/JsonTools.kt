package cn.lyric.getter.tool

import com.google.gson.Gson
import java.io.InputStreamReader

object JsonTools {
    val gson = Gson()

    inline fun <reified T> String.parseJSON(): T {
        return Gson().fromJson(this, T::class.java)
    }
    inline fun <reified T> InputStreamReader.parseJSON(): T {
        return Gson().fromJson(this, T::class.java)
    }

    inline fun <reified T> Class<*>.toJSON(): String {
        return Gson().toJson(this, T::class.java)
    }
}