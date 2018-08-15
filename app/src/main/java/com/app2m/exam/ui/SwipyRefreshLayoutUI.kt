package com.app2m.exam.ui

import android.graphics.Color
import android.view.Gravity
import android.widget.Button
import com.app2m.exam.Main2Activity
import com.app2m.exam.R
import com.app2m.exam.swipyRefreshLayout
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class SwipyRefreshLayoutUI<T> : AnkoComponent<T>, AnkoLogger {
    var btn1: Button? = null
    override fun createView(ui: AnkoContext<T>) = ui.apply {
        frameLayout {
            lparams(width= matchParent, height = matchParent)
            swipyRefreshLayout {
                backgroundColor = resources.getColor(R.color.material_blue_grey_800)
                direction = SwipyRefreshLayoutDirection.BOTH
                setOnRefreshListener { it ->
                    it?.let {
                        when(it) {
                            SwipyRefreshLayoutDirection.TOP -> {
                                toast("is top")
                                this.isRefreshing = false
                            }
                            SwipyRefreshLayoutDirection.BOTTOM -> {
                                toast("is bottom")
                                this.isRefreshing = false
                            }
                            SwipyRefreshLayoutDirection.BOTH -> {
                                //Keep empty
                            }
                        }
                    }
                }
                verticalLayout {
                    isClickable = true
                    btn1 = button {
                        text = "BUTTON 1"
                        onClick {
                            (owner as Main2Activity).onClickBtn1()
                        }
                    }.lparams(width= matchParent, height = wrapContent)
                    button {
                        text = "BUTTON 2"
                        onClick {
                            (owner as Main2Activity).onClickBtn2(this@button)
                        }
                    }.lparams(width= matchParent, height = wrapContent)
                    button {
                        text = "BUTTON 3"
                        onClick {
                            (owner as Main2Activity).onClickBtn2(this@button)
                        }
                    }.lparams(width= matchParent, height = wrapContent)
                    button {
                        text = "BUTTON 4"
                        onClick {
                            (owner as Main2Activity).onClickBtn2(this@button)
                        }
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
    }.view
}