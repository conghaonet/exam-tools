package com.app2m.exam

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import org.jetbrains.anko.*

/**
 * 加上@ JvmOverloads 的 目的 是 让 Java 代码 也能 识别 默认 参数
 * 因为 添加 了 注解 标记， 所以 必须 补上 关键字 constructor
 * 例：class QuestionAdapter @JvmOverloads constructor(val data: List<String>)
 */
class QuestionAdapter (val data: MutableList<String>) : RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder>() {
    private lateinit var ankoContext : AnkoContext<QuestionAdapter>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        ankoContext = AnkoContext.createReusable(parent.context, this)
//        var questionUI = QuestionUI()
//        return QuestionViewHolder(questionUI.createView(ankoContext), questionUI)
        return QuestionViewHolder(QuestionUI())
    }


    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(data[position], position)
    }

    inner class QuestionViewHolder(private val ui: QuestionUI) : RecyclerView.ViewHolder(ui.createView(ankoContext)){
        fun bind(value : String, index: Int) {
            ui.itemValue.text = value
            ui.itemValue.setOnClickListener {
                itemClickListener?.onClickItem(index)
            }
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
    lateinit var itemValue: TextView
    override fun createView(ui: AnkoContext<QuestionAdapter>) = ui.apply {
        linearLayout {
            lparams(width = matchParent, height = dip(48))
            orientation = LinearLayout.HORIZONTAL
            itemValue = textView {
                text = "data"
                textSize = 16f
                setOnClickListener {  }
            }
        }
    }.view
}