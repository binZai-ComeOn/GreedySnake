package com.youwei.greedysnake

import android.app.Application
import com.tencent.mmkv.MMKV

class MyAppliction : Application(){

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this);
    }

}