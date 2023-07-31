package cn.lyric.getter.tool

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.InputStreamReader


object JsonTools {
    val gson = Gson()

    inline fun <reified T> String.parseJSON(): T {
        return gson.fromJson(this, T::class.java)
    }

    inline fun <reified T> InputStreamReader.parseJSON(): T {
        return gson.fromJson(this, T::class.java)
    }

    fun Any.toJSON(print: Boolean = false): String {
        return if (print) {
            val gson = GsonBuilder().setPrettyPrinting().create()
            gson.toJson(this, Any::class.java)
        } else {
            gson.toJson(this, Any::class.java)
        }
    }
}