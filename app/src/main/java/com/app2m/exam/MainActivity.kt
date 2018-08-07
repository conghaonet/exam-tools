package com.app2m.exam

import android.app.Activity
import android.content.*
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
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
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
                        data?.data?.let {
                            Log.d(TAG, "Uri.scheme ==== ${it.scheme}")
                            Log.d(TAG, "Uri.path ==== ${it.path}")
                            if("file" == it.scheme) {
                                toast("path = ${it.path}")
                            } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4(KitKat)以后的版本
                                // DocumentProvider
                                if(DocumentsContract.isDocumentUri(this@MainActivity, it)) {
                                    // ExternalStorageProvider
                                    if (isExternalStorageDocument(it)) {
                                        var docId = DocumentsContract.getDocumentId(it)
                                        var split = docId.split(":")
                                        Log.d(TAG, "ExternalStorageProvider file: ${Environment.getExternalStorageDirectory()}/${split[1]}")
                                    }
                                    // DownloadsProvider
                                    else if (isDownloadsDocument(it)) {
                                        var docId = DocumentsContract.getDocumentId(it)
                                        var contentUri: Uri = ContentUris.withAppendedId(
                                                Uri.parse("content://downloads/public_downloads"), docId.toLong())
                                        var path = getDataColumn(this@MainActivity, contentUri, null, null)
                                        Log.d(TAG, "DownloadsProvider file: $path")
                                    }
                                    // MediaProvider
                                    else if (isMediaDocument(it)) {
                                        var docId = DocumentsContract.getDocumentId(it)
                                        var split = docId.split(":")
                                        var type = split[0]
                                        var contentUri: Uri? = null
                                        when(type) {
                                            "image" -> {
                                                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                                            } "video"-> {
                                                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                                            } "audio"-> {
                                                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                                            }
                                        }
                                        var selection = "_id=?"
                                        var selectionArgs = arrayOf(split[1])
                                        contentUri?.let {
                                            //TODO: 调用系统相册有BUG，待修复
                                            Log.d(TAG, "MediaProvider file: ${getDataColumn(this@MainActivity, contentUri, selection, selectionArgs)}")
                                        }

                                    }
                                }
                                // MediaStore (and general)
                                else if("content" == it.scheme) {
                                    Log.d(TAG, "MediaStore file: ${getDataColumn(this@MainActivity, it, null, null)}")
                                }
                            }
//                            var projections = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME)
                            var courser: Cursor? = contentResolver.query(it, null, null, null, null)
                            courser?.let {
                                it.moveToFirst()
//                                val idIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
//                                val dataIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//                                val displayIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

                                for (index in 0 until  it.columnCount) {
                                    Log.d(TAG, "COLUMN_${it.getColumnName(index)} = ${it.getString(index)}")
                                }
                            }
                            courser?.close()
                        }
                    }
                }
            }
        }
    }

    private fun getDataColumn(context: Context, uri: Uri, selection: String?, selectionArgs: Array<String>?) : String {
        var path = ""
        val columnData = "_data"
        val projection = arrayOf(columnData)
        var cursor: Cursor? = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
        cursor?.let {
            it.moveToFirst()
            val columnIndexData = it.getColumnIndexOrThrow(columnData)
            path = cursor.getString(columnIndexData)
            it.close()
        }
        return path
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri) : Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri) : Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri) : Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }
}
