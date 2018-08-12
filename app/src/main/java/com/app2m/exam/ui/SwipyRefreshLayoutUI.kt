package com.app2m.exam.ui

import android.graphics.Color
import android.view.Gravity
import com.app2m.exam.MainActivity
import com.app2m.exam.R
import com.app2m.exam.swipyRefreshLayout
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection
import org.jetbrains.anko.*

class SwipyRefreshLayoutUI<T> : AnkoComponent<T>, AnkoLogger {
    override fun createView(ui: AnkoContext<T>) = with(ui) {
        frameLayout {
            lparams(width= matchParent, height = matchParent)
            swipyRefreshLayout {
                backgroundColor = resources.getColor(R.color.material_blue_grey_800)
                direction = SwipyRefreshLayoutDirection.BOTH
                setOnRefreshListener {
                    when(it) {
                        SwipyRefreshLayoutDirection.TOP -> {
                            toast("is top")
                            this.isRefreshing = false
                        }
                        SwipyRefreshLayoutDirection.BOTTOM -> {
                            toast("is bottom")
                            this.isRefreshing = false
                        }
                    }
                }
                verticalLayout {
                    isClickable = true
                    button {
                        text = "BUTTON 1"
                    }.lparams(width= matchParent, height = wrapContent)
                    button {
                        text = "BUTTON 2"
                    }.lparams(width= matchParent, height = wrapContent)
                    button {
                        text = "BUTTON 3"
                    }.lparams(width= matchParent, height = wrapContent)
                    button {
                        text = "BUTTON 4"
                    }.lparams(width= matchParent, height = wrapContent)
                    button {
                        text = "BUTTON 5"
                    }.lparams(width= matchParent, height = wrapContent)
                    button {
                        text = "BUTTON 6"
                    }.lparams(width= matchParent, height = wrapContent)
                    textView {
                        text = "我是SwipyRefreshLayout，验证自定义View"
                        textColor = Color.WHITE
                        gravity = Gravity.CENTER
                        textSize = 18f
                    }.lparams(width= matchParent, height = 0, weight = 1f)
                }.lparams(width= matchParent, height = matchParent)

            }.lparams(width= matchParent, height = matchParent)
        }

    }
}