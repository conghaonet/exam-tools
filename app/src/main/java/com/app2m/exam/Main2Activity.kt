package com.app2m.exam

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import com.app2m.exam.ui.SwipyRefreshLayoutUI
import org.jetbrains.anko.*

class Main2Activity : AppCompatActivity() {
    val myUI : SwipyRefreshLayoutUI<Main2Activity> by lazy {
        SwipyRefreshLayoutUI<Main2Activity>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myUI.setContentView(this)
        myUI.btn1?.text = "BTN 1"
    }

    fun onClickBtn1() {
        startActivity<QuestionsActivity>()
    }
    fun onClickBtn2(btn: Button) {
        myUI.btn1?.text = "${myUI.btn1?.text} changed by ${btn.text}"
        btn.text = "${btn.text} clicked"
    }
}

class Main2ActivityUI : AnkoComponent<Main2Activity> {
    override fun createView(ui: AnkoContext<Main2Activity>) = ui.apply {
        verticalLayout {
            gravity = Gravity.CENTER
            padding = dip(20)

            textView {
                gravity = Gravity.CENTER
                text = "Enter your request"
                textColor = Color.BLACK
                textSize = 24f
            }.lparams(width = matchParent) {
                margin = dip(20)
            }
        }
    }.view
}