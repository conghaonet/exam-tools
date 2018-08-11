package com.app2m.exam

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.nio.charset.Charset



fun File.getText(charset: Charset = Charsets.UTF_8, isTrim : Boolean = false) : String? {
    lateinit var text : StringBuilder
    if(this.exists()) {
        try {
            this.bufferedReader(charset).use {
                var line = it.readLine()
                text = StringBuilder()
                while (line != null) {
                    if (isTrim) {
                        text.append(line.trim())
                    } else {
                        text.append(line)
                    }
                    line = it.readLine()
                }
            }
        } catch (e: IOException) {
            throw e
        }
    }
    return text.toString()
}

/**
 *
 */
@Deprecated("This function is deprecated use convert2DataObject() instead..", ReplaceWith("this.convert2DataObject(charset)"))
fun<T> File.convert2Json(charset: Charset = Charsets.UTF_8) : T? {
    val text = this.getText(charset, true)
    text?.let {
        val typeToken = object : TypeToken<T>() {}.type
        return Gson().fromJson(text, typeToken)
    }
    return null
}

//只有内联(inline)函数才可以被具体化(reified)
inline fun<reified T : Any> File.convert2DataObject (charset: Charset = Charsets.UTF_8) : T? {
    val text = this.getText(charset, true)
    var obj : T? = null
    text?.run {
        try {
            if(startsWith("[") && endsWith("]")) {
                val typeToken = object : TypeToken<T>() {}.type
                obj =  Gson().fromJson(this, typeToken)
            } else if(startsWith("{") && endsWith("}")) {
                obj = Gson().fromJson(this, T::class.java)
            } else {
                Log.i("MyExtFun.kt", "不是json数据！！")
            }
        } catch (e: Exception) {
            Log.e("MyExtFun.kt", "json格式错误！！")
        }
    }
    return obj
}