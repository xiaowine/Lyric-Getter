package cn.lyric.getter.tool

import kotlinx.serialization.json.Json
import java.io.InputStreamReader

object JsonTools {
    val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    inline fun <reified T> String.parseJSON(): T {
        return json.decodeFromString(this)
    }

    inline fun <reified T> InputStreamReader.parseJSON(): T {
        return json.decodeFromString(this.readText())
    }

    fun Any.toJSON(print: Boolean = false): String {
        return if (print) {
            json.encodeToString(this)
        } else {
            Json.encodeToString(this)
        }
    }
}