package com.app2m.exam

import android.app.Activity
import android.content.ClipDescription
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.app2m.exam.data.QuestionVo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import java.net.URL
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.util.Log


private const val JSON_REQUEST_CODE = 1
private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var questions: List<QuestionVo>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnQuestionData.setOnClickListener {
            doAsync {
                assets.open("sampledata.json").bufferedReader().use {
                    var data = StringBuilder("")
                    var line = it.readLine()
                    while (line != null) {
                        data.append(line.trim())
                        line = it.readLine()
                    }
                    it.close()
                    val typeToken = object : TypeToken<List<QuestionVo>>() {}.type
                    questions = Gson().fromJson(data.toString(), typeToken)
                    uiThread {
                        getAnswers()
                        toast("读取题目数据总数：${questions.size}")
                    }
                }
            }
        }
        btnClipboard.setOnClickListener {
            val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            if(cm.hasPrimaryClip()) {
                val clip = cm.primaryClip
                var clipDescription: ClipDescription = clip.description
                var clipLabel: CharSequence? = clipDescription.label
                var clipText: CharSequence = clip.getItemAt(0).text
                toast("label: $clipLabel \ntext: $clipText")
            } else {
                toast("ClipboardManager is empty!")
            }
        }
        btnSelectFile.setOnClickListener {
            var intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(intent, JSON_REQUEST_CODE)
        }
    }

    private fun getAnswers() {
        doAsync {
            for(index in questions.indices) {
                var questionVo = questions[index]
                var url = questionVo.source.tech_info.source.location
                url = url.replace(REF_PATH_TAG, REF_PATH, true)
                val response = URL(url).readText()
                var correctAnswers = parseXML(response)
                questionVo.correctResponse = QuestionVo.CorrectResponse(correctAnswers)
            }
            uiThread {
                toast("答案获取完成")
            }
        }
    }

    private fun parseXML(result: String): List<String> {
        var results = ArrayList<String>()
        var factory = XmlPullParserFactory.newInstance()
        var xmlParser = factory.newPullParser()
        try {
            xmlParser.setInput(StringReader(result))
            var eventType = xmlParser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT){
                var nodeName = xmlParser.name
                when(eventType){
                    XmlPullParser.START_TAG -> {
                        if ("correctResponse" == nodeName){
                            while (eventType != XmlPullParser.END_DOCUMENT) {
                                eventType = xmlParser.next()
                                when(eventType) {
                                    XmlPullParser.START_TAG -> {
                                        if ("value" == xmlParser.name){
                                            results.add(xmlParser.nextText())
                                        }
                                    }
                                    XmlPullParser.END_TAG -> {
                                        if ("correctResponse" == xmlParser.name) {
                                            return results
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                eventType = xmlParser.next()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return results
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            JSON_REQUEST_CODE -> {
                when(resultCode) {
                    Activity.RESULT_OK -> {
                        val uri = data?.data
                        Log.d(TAG, "${uri?.scheme.toString()} \n${uri?.path}")
                        toast("${uri?.scheme.toString()} \n${uri?.path}")
                    }
                }
            }
        }
    }
}
