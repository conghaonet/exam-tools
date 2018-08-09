package com.app2m.exam

import android.app.Activity
import android.content.*
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.app2m.exam.base.BaseActivity
import com.app2m.exam.data.QuestionVo
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.nestedScrollView
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import java.net.URL


private const val JSON_REQUEST_CODE = 1
private const val TAG = "MainActivity"

class MainActivity : BaseActivity() {
    private lateinit var questions: List<QuestionVo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivityUIaa().setContentView(this)


//        setContentView(R.layout.activity_main)

/*
        btnMain2Activity.setOnClickListener {
            startActivity<Main2Activity>()
        }
*/
/*
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
*/
/*
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
*/

    }
    fun openFile(view: View) {
        var intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, JSON_REQUEST_CODE)
        (view as Button).text = "CLICKED"
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

    fun parseXML(result: String): List<String> {
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
        if(JSON_REQUEST_CODE == requestCode && Activity.RESULT_OK == resultCode) {
            data?.let {
                var realPath = FileChooser.getRealPath(this@MainActivity, it.data)
                toast(realPath)
            }
        }
    }
}

class MainActivityUIaa : AnkoComponent<MainActivity> {
    override fun createView(ui: AnkoContext<MainActivity>) = with(ui) {
        nestedScrollView {
            lparams(width = matchParent, height = matchParent)
            verticalLayout {
                button("Main2Activity") {
                    allCaps = false
                    setOnClickListener {
                        toast("go to Main2Activity")
                    }
                }.lparams(width = matchParent, height = wrapContent)

                button("Read question data") {
                    allCaps = false
                }.lparams(width = matchParent, height = wrapContent)

                button {
                    text = "Clipboard Manager"
                    allCaps = false
                }.lparams(width = matchParent, height = wrapContent)

                button {
                    text = "选择文件"
                    allCaps = false
                    //TODO: 应该写为onClick，可能是依赖包缺失
                    setOnClickListener {
                        ui.owner.openFile(it)
                    }

                }.lparams(width = matchParent, height = wrapContent)

            }.lparams(width = matchParent, height = wrapContent)
        }
    }
}