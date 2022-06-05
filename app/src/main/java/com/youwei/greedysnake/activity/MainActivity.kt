package com.youwei.greedysnake.activity

import android.content.Intent
import android.view.View
import com.youwei.greedysnake.base.BaseActivity
import com.youwei.greedysnake.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), View.OnClickListener {

    override val layout: Int
        get() = R.layout.activity_main

    override fun initView() {

    }

    override fun setListener() {
        startGame.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.startGame -> {
                startActivity(Intent(this, GameActivity::class.java))
            }
        }
    }
}