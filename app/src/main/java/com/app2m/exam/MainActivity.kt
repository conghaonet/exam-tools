package com.app2m.exam

import android.app.Activity
import android.content.*
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.app2m.exam.base.BaseActivity
import com.app2m.exam.data.QuestionVo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.nestedScrollView
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.File
import java.io.StringReader
import java.net.URL

private const val JSON_REQUEST_CODE_QUESTIONS = 1
private const val JSON_REQUEST_CODE_SINGLE_QUESTION = 2

class MainActivity : BaseActivity(), AnkoLogger {
    private var questions: List<QuestionVo>? = null
    lateinit var permissionUtils : PermissionUtils
    var btnClipboardManagerText: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        btnClipboardManagerText = "Clipboard Manager"
        MainActivityUI().setContentView(this)
        permissionUtils = PermissionUtils(this)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        permissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    fun readJsonInAssetsFile() {
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
                    toast("读取题目数据总数：${questions?.size}")
                }
            }
        }
    }
    fun readClipboardMsg() {
        val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if(cm.hasPrimaryClip()) {
            val clip = cm.primaryClip
            var clipDescription: ClipDescription? = clip?.description
            var clipLabel: CharSequence? = clipDescription?.label
            var clipText: CharSequence? = clip?.getItemAt(0)?.text
            toast("label: $clipLabel \ntext: $clipText")
        } else {
            toast("ClipboardManager is empty!")
        }
    }
    fun openFileForQuestions(view: View) {
        permissionUtils.checkStoragePermission(Runnable {
            var intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(intent, JSON_REQUEST_CODE_QUESTIONS)
            (view as Button).text = "List<QuestionVo>"
        })
    }
    fun openFileForSingleQuestion(btn: Button) {
        var intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, JSON_REQUEST_CODE_SINGLE_QUESTION)
        btn.text = "QuestionVo"
    }

    private fun getAnswers() {
        questions?.let {
            doAsync {
                for(index in it.indices) {
                    var questionVo = it[index]
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
        if(JSON_REQUEST_CODE_QUESTIONS == requestCode && Activity.RESULT_OK == resultCode) {
            data?.data?.run {
                FileUtil.getRealPath(this@MainActivity, this)?.let {
                    var file = File(it)
                    doAsync {
                        questions = file.convert2DataObject()
                        uiThread {
                            getAnswers()
                            toast("读取题目数据总数：${questions?.size}")
                        }
                    }
                }
            }
        } else if(JSON_REQUEST_CODE_SINGLE_QUESTION == requestCode && Activity.RESULT_OK == resultCode) {
            data?.data?.run {
                FileUtil.getRealPath(this@MainActivity, this)?.let {
                    var file = File(it)
                    doAsync {
                        var questionVo = file.convert2DataObject<QuestionVo>()
                        uiThread {
                            toast("QuestionVo：${questionVo?.class_name}")
                        }
                    }
                }
            }
        }
    }
}

class MainActivityUI : AnkoComponent<MainActivity>, AnkoLogger {
    override fun createView(ui: AnkoContext<MainActivity>) = with(ui) {
        nestedScrollView {
            lparams(width = matchParent, height = matchParent)
            verticalLayout {
                button("Main2Activity") {
                    allCaps = false
                    setOnClickListener {
                        info("====MainActivity====", RuntimeException("abcde"))

                        startActivity<Main2Activity>()
                    }
                }.lparams(width = matchParent, height = wrapContent)

                button("Read question data") {
                    allCaps = false
                    setOnClickListener {
                        ui.owner.readJsonInAssetsFile()
                    }
                }.lparams(width = matchParent, height = wrapContent)

                button {
                    text = owner.btnClipboardManagerText
                    allCaps = false
                    onClick {
                        ui.owner.readClipboardMsg()
                    }
                }.lparams(width = matchParent, height = wrapContent)

                button {
                    text = "选择文件 读取完整试卷(List<QuestionVo>)"
                    allCaps = false
                    //依赖anko-sdk25-coroutines
                    onClick {
                        ui.owner.openFileForQuestions(this@button)
                    }

                }.lparams(width = matchParent, height = wrapContent)
                button {
                    text = "选择文件 读取单个题目(QuestionVo)"
                    allCaps = false
                    //依赖anko-sdk25-coroutines
                    onClick {
                        ui.owner.openFileForSingleQuestion(this@button)
                    }

                }.lparams(width = matchParent, height = wrapContent)

            }.lparams(width = matchParent, height = wrapContent)
        }
    }
}