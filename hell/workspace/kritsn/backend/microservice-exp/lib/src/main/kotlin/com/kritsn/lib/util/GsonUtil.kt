package com.kritsn.lib.util

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

/**
 * ✅ Safely convert JSON string to object of type T
 */
inline fun <reified T> String?.fromJsonString(): T? {
    return try {
        // Edge case 1: null or blank string
        if (this.isNullOrBlank()) {
            println("⚠️ json string is null in fromJsonString.")
            return null
        }

        Gson().fromJson<T>(this, object : TypeToken<T>() {}.type)
    } catch (ex: JsonSyntaxException) {
        // Edge case 2: Malformed JSON
        println("❌ JSON parsing error in fromJsonString: ${ex.message}")
        null
    } catch (ex: Exception) {
        // Edge case 3: Any unexpected runtime exception
        println("❌ Unexpected error in fromJsonString: ${ex.message}")
        null
    }
}

/**
 * ✅ Convert Map<String, Any?> to Object of given Class<T>
 */
fun <T> mapToObject(map: Map<String, Any?>?, type: Class<T>): T? {
    return try {
        // Edge case 1: null or empty map
        if (map.isNullOrEmpty()) {
            println("⚠️ map is null in mapToObject.")
            return null
        }

        val json = Gson().toJson(map) // Convert map → JSON
        Gson().fromJson(json, type)   // Convert JSON → Object
    } catch (ex: JsonSyntaxException) {
        // Edge case 2: Invalid structure
        println("❌ JSON parsing error while converting Map: ${ex.message}")
        null
    } catch (ex: Exception) {
        // Edge case 3: Any unexpected runtime exception
        println("❌ Unexpected error while converting Map: ${ex.message}")
        null
    }
}


/**
 * Convert any object to Json String
 */
fun Any?.toJsonString(): String {
    return try {
        // Edge case 1: null or empty map
        if (this !=null) {
            println("⚠️ obj is null in toJsonString.")
            return ""
        }

        return Gson().toJson(this) // Convert obj → JSON
    }catch (ex: JsonSyntaxException) {
        // Edge case 2: Invalid structure
        println("❌ JSON parsing error in toJsonString: ${ex.message}")
        ""
    } catch (ex: Exception) {
        // Edge case 3: Any unexpected runtime exception
        println("❌ Unexpected error in toJsonString: ${ex.message}")
        ""
    }
}
