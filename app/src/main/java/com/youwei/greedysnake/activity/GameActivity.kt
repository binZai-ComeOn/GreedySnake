package com.youwei.greedysnake.activity

import android.app.AlertDialog
import android.hardware.SensorManager
import android.view.OrientationEventListener
import com.tencent.mmkv.MMKV
import com.youwei.greedysnake.R
import com.youwei.greedysnake.base.BaseActivity
import com.youwei.greedysnake.ui.SnakeView
import com.youwei.greedysnake.ui.SnakeView.Companion.MODE
import com.youwei.greedysnake.ui.SnakeView.Companion.directionCurrent
import com.youwei.greedysnake.ui.SnakeView.Companion.directionNext
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : BaseActivity() {
    private var orientationEventListener: OrientationEventListener? = null

    override val layout: Int
        get() = R.layout.activity_game

    override fun initView() {
        MODE = 2
    }

    override fun setListener() {
        orientationEventListener =
            object : OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
                override fun onOrientationChanged(orientation: Int) {
                    var orientation = orientation
                    if (orientation == ORIENTATION_UNKNOWN) {
                        return  //手机平放时，检测不到有效的角度
                    }
                    //只检测是否有四个角度的改变
                    orientation = if (orientation > 340 || orientation < 20) {
                        SnakeView.RIGHT
                    } else if (orientation in 71..109) {
                        SnakeView.UP
                    } else if (orientation in 161..199) {
                        SnakeView.LEFT
                    } else if (orientation in 251..289) {
                        SnakeView.DOWN
                    } else {
                        -1 // 无效方向
                    }
                    if (orientation != -1) {
                        // 设置蛇的下一个方向
                        if (orientation != directionCurrent) {
                            directionNext = orientation
                        }
                    }
                }
            }
        // 启动手机侧重方向监听
        orientationEventListener!!.enable()

        snakeview.setSettlementListener(object : SnakeView.Settlement {
            override fun onResult(score: Int) {

                var HighestScore = MMKV.defaultMMKV().getInt("HighestScore", 0)
                if (HighestScore < score) {
                    HighestScore = score
                    MMKV.defaultMMKV().putInt("HighestScore",score)
                }
                // 结算游戏
                val dialog = AlertDialog.Builder(this@GameActivity)
                dialog.setTitle("游戏结束")
                dialog.setMessage("最高分：$HighestScore\n当前得分：$score")
                dialog.setPositiveButton(
                    "确认"
                ) { dialog, which -> finish() }
                dialog.setCancelable(false)
                runOnUiThread { dialog.create().show() }
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        if (orientationEventListener != null) {
            orientationEventListener!!.disable()
        }
    }
}