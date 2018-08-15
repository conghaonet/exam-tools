package com.app2m.exam

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import org.jetbrains.anko.*

class QuestionAdapter(val data: List<String>) : RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder>() {
    val questionUI : QuestionUI by lazy {
        QuestionUI()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
//        return QuestionViewHolder(TextView(parent.context))
        return QuestionViewHolder(questionUI.createView(AnkoContext.create(parent.context, parent)))
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
//        holder.view.text = data[position]
        holder.bind(data[position])
    }

    inner class QuestionViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(value : String) {
            questionUI.itemValue.text = value
        }
    }
}

class QuestionUI : AnkoComponent<ViewGroup> {
    lateinit var itemValue: TextView
    override fun createView(ui: AnkoContext<ViewGroup>): View {
        return with(ui) {
            linearLayout {
                lparams(width = matchParent, height = dip(48))
                orientation = LinearLayout.HORIZONTAL
                itemValue = textView {
                    textSize = 16f
                }
            }
        }
    }
}