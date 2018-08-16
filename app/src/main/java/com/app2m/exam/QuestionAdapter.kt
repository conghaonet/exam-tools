package com.app2m.exam

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.app2m.exam.data.QuestionVo
import org.jetbrains.anko.*

/**
 * 加上@ JvmOverloads 的 目的 是 让 Java 代码 也能 识别 默认 参数
 * 因为 添加 了 注解 标记， 所以 必须 补上 关键字 constructor
 * 例：class QuestionAdapter @JvmOverloads constructor(val data: List<String>)
 */
class QuestionAdapter (val data: MutableList<QuestionVo>?) : RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder>() {
    private lateinit var ankoContext : AnkoContext<QuestionAdapter>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        ankoContext = AnkoContext.createReusable(parent.context, this)
//        var questionUI = QuestionUI()
//        return QuestionViewHolder(questionUI.createView(ankoContext), questionUI)
        return QuestionViewHolder(QuestionUI())
    }


    override fun getItemCount(): Int = data?.size ?: 0

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(data!![position], position)
    }

    inner class QuestionViewHolder(private val ui: QuestionUI) : RecyclerView.ViewHolder(ui.createView(ankoContext)){
        fun bind(value : QuestionVo, index: Int) {
            ui.seq.text = if(index<9) "0${index+1}." else "${index+1}."
            val questionType = when(value.type) {
                201 -> "单选"
                202 -> "多选"
                203 -> "单选"
                else -> "其它"
            }
            ui.itemValue.text = "【$questionType】${value.content.title}"
            ui.itemValue.setOnClickListener {
                itemClickListener?.onClickItem(index)
            }
            ui.answer.text = value.correctResponse?.values.toString().replace("[","").replace("]","")
        }
    }

    fun setOnItemClickListener(listener: ItemClickListener) {
        itemClickListener = listener
    }
    private var itemClickListener: ItemClickListener? = null
    interface ItemClickListener {
        fun onClickItem(position: Int)
    }
}

class QuestionUI : AnkoComponent<QuestionAdapter> {
    lateinit var seq: TextView
    lateinit var itemValue: TextView
    lateinit var answer: TextView
    override fun createView(ui: AnkoContext<QuestionAdapter>) = ui.apply {
        verticalLayout {
            lparams(width = matchParent, height = wrapContent) {
                topMargin = dip(4)
                bottomMargin = dip(4)
            }
            linearLayout {
                orientation = LinearLayout.HORIZONTAL
                seq = textView {
                    textSize = 16f
                    setPadding(0,0, dip(4),0)
                }.lparams(width = wrapContent, height = wrapContent)
                itemValue = textView {
                    text = "data"
                    textSize = 16f
                }.lparams(width = matchParent, height = wrapContent)
            }.lparams(width = matchParent, height = wrapContent) {
                leftMargin = dip(8)
                rightMargin = dip(8)
            }
            linearLayout {
                orientation = LinearLayout.HORIZONTAL
                textView("答案：") {
                    textSize = 16f
                    setPadding(0,0, dip(4),0)
                }.lparams(width = wrapContent, height = wrapContent)
                answer = textView {
                    textSize = 16f
                }.lparams(width = matchParent, height = wrapContent)
            }.lparams(width = matchParent, height = wrapContent) {
                leftMargin = dip(8)
                rightMargin = dip(8)
            }
            view {
                backgroundColor = R.color.material_grey_100
            }.lparams(width = matchParent, height = dip(1)) {
                topMargin = dip(4)
            }
        }
    }.view
}