package com.youwei.greedysnake.base

import android.app.Activity
import android.os.Bundle
import android.content.pm.ActivityInfo
import android.view.WindowManager

abstract class BaseActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(layout)
        initView()
        setListener()
    }

    abstract val layout: Int

    abstract fun initView()

    abstract fun setListener()
}