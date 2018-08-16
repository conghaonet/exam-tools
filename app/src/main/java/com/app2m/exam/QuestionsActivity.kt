package com.app2m.exam

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.app2m.exam.data.QuestionVo
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.nestedScrollView

class QuestionsActivity : AppCompatActivity(), AnkoLogger {
    val adapter: QuestionAdapter by lazy {
        QuestionAdapter(questions)
    }

    val questions: MutableList<QuestionVo>? by lazy {
        intent.getSerializableExtra("questions") as MutableList<QuestionVo>
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
                    text = "clear"
                    onClick {
                        owner.questions?.let {
                            it.clear()
                            owner.adapter.notifyDataSetChanged()
                        }
                    }
                }
                button {
                    text = "remove first element"
                    onClick {
                        owner.questions?.let {
                            if(!it.isEmpty()) {
                                it.removeAt(0)
                                owner.adapter.notifyDataSetChanged()
                            }
                        }
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