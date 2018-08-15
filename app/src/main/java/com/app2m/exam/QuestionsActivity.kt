package com.app2m.exam

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.nestedScrollView

class QuestionsActivity : AppCompatActivity(), AnkoLogger {
    val data = mutableListOf(
            "给初学者的RxJava2.0教程（七）: Flowable",
            "Android之View的诞生之谜",
            "Android之自定义View的死亡三部曲之Measure",
            "Using ThreadPoolExecutor in Android ",
            "Kotlin 泛型定义与 Java 类似，但有着更多特性支持。",
            "Android异步的姿势，你真的用对了吗？",
            "Android 高质量录音库。",
            "Android 边缘侧滑效果，支持多种场景下的侧滑退出。"
    )
    val adapter: QuestionAdapter by lazy {
        QuestionAdapter(data)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        QuestionsActivityUI().setContentView(this)
        adapter.setOnItemClickListener(object : QuestionAdapter.ItemClickListener {
            override fun onClickItem(position: Int) {
                toast("position = $position")
            }
        })
    }
}

class QuestionsActivityUI: AnkoComponent<QuestionsActivity>, AnkoLogger {
    override fun createView(ui: AnkoContext<QuestionsActivity>) = ui.apply {
        nestedScrollView {
            verticalLayout {
                button {
                    text = "add one element"
                    onClick {
                        owner.data.add("new element ${owner.data.size + 1}")
                        owner.adapter.notifyDataSetChanged()
                    }
                }
                button {
                    text = "clear"
                    onClick {
                        owner.data.clear()
                        owner.adapter.notifyDataSetChanged()
                    }
                }
                recyclerView {
                    layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL,false)
                    adapter = owner.adapter
                }
            }
        }
    }.view
}