package com.youwei.greedysnake.ui

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.view.View
import com.tencent.mmkv.MMKV
import com.youwei.greedysnake.R
import com.youwei.greedysnake.activity.GameActivity
import com.youwei.greedysnake.bean.Coordinate
import java.util.*

class SnakeView : TileView {

    companion object {
        /**
         * 蛇头、蛇身的标识符
         */
        private const val SNAKE_Head = 1
        private const val SNAKE_BODY = 2

        /**
         * 方向标识符
         */
        const val UP = 1
        const val DOWN = 2
        const val RIGHT = 3
        const val LEFT = 4

        /**
         * 当前方向
         */
        var directionCurrent = RIGHT

        /**
         * 下一个方向
         */
        var directionNext = RIGHT

        /**
         * 五种模式，分别对应暂停、重载、正在运行、结算、退出
         */
        private const val PAUSE = 0
        private const val READY = 1
        private const val RUNING = 2
        private const val LOSE = 3
        private const val QUIT = 4

        /**
         * 当前模式
         */
        var MODE: Int = READY
    }

    /**
     * 延迟蛇的移动时间
     */
    var moveDelay = 500
    var moveLast: Long = 0
    var count = 0

    /**
     *存储蛇的所有坐标
     */
    var snakeCoordinate: ArrayList<Coordinate> = ArrayList<Coordinate>()

    /**
     * 存储食物的所有坐标
     */
    var foodCoordinate: ArrayList<Coordinate> = ArrayList<Coordinate>()

    /**
     * 存储障碍物坐标
     */
    var obstacleCoordinate: ArrayList<Coordinate> = ArrayList<Coordinate>()

    /**
     * 得分
     */
    var score = 0

    /**
     * 随机生成食物的坐标
     */
    var random = Random()

    /**
     * 通过Handle更新UI
     */
    private var handler = MyHandler()

    inner class MyHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            // 更新视图
            updateView()
            // 请求重绘，不断调用onDraw方法
            invalidate()
        }

        // 调用sleep后,在一段时间后再sendmessage进行UI更新
        fun sleep(delayMillis: Int) {
            removeMessages(0) //清空消息队列
            sendMessageDelayed(obtainMessage(0), delayMillis.toLong())
        }
    }

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        isFocusable = true
        initGame()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initGame()
    }

    /**
     * 更新视图
     */
    fun updateView() {
        if (MODE == RUNING) {
            // 如果指定的延时时间未到，则不执行括号里面方法
            val currentTimeMillis = System.currentTimeMillis()
            if (currentTimeMillis - moveLast > moveDelay) {
                clearTiles()
                updateSnake()
                updateFood()
                moveLast = currentTimeMillis
            }
            handler.sleep(moveDelay)
        }
    }

    /**
     * 更新食物
     */
    private fun updateFood() {
        for (c in foodCoordinate) {
            setTile(SNAKE_BODY, c.x!!, c.y!!)
        }
    }

    /**
     * 更新蛇的状态
     */
    private fun updateSnake() {
        var growSnake = false
        val head = snakeCoordinate.get(0)
        var newHead = Coordinate(1, 1)
        directionCurrent = directionNext
        // 蛇移动的方向
        when (directionCurrent) {
            UP -> {
                newHead = Coordinate(head.x, head.y!! - 1)
            }
            DOWN -> {
                newHead = Coordinate(head.x, head.y!! + 1)
            }
            LEFT -> {
                newHead = Coordinate(head.x!! - 1, head.y)
            }
            RIGHT -> {
                newHead = Coordinate(head.x!! + 1, head.y)
            }
        }
        // 检测投是否撞墙（屏幕的四个边）
        if (newHead.x!! < 0 || newHead.y!! < 0 || newHead.x!! > tileCountX - 1 || newHead.y!! > tileCountY - 1) {
            setMode(LOSE)
            return
        }
        // 检测头是否撞到自己
        val snakeSize = snakeCoordinate.size
        for (snakeindex in 0 until snakeSize) {
            val get = snakeCoordinate[snakeindex]
            if (get.equals(newHead)) {
                setMode(LOSE)
            }
        }
        // 检测蛇头撞到障碍物
        for (index in obstacleCoordinate.indices) {
            val c: Coordinate = obstacleCoordinate[index]
            if (c.equals(newHead)) {
                setMode(LOSE)
                return
            }
        }
        // 检测蛇是否吃到食物
        val foodcount: Int = foodCoordinate.size
        for (foodindex in 0 until foodcount) {
            val c: Coordinate = foodCoordinate.get(foodindex)
            if (c.equals(newHead)) {
                foodCoordinate.remove(c)
                addRandomFood()
                score++
                // 蛇每吃到一个食物，延时就会减少，蛇的速度就会加快
                moveDelay -= 5
                growSnake = true
                if (moveDelay < 150) {
                    count++
                    if (count == 4) {
                        addrandomObstacle(count)
                        count = 0
                    }
                }
            }
        }
        snakeCoordinate.add(0, newHead)
        if (!growSnake) {
            snakeCoordinate.removeAt(snakeCoordinate.size - 1)
        }
        //添加蛇的图片
        var index = 0
        for (c in snakeCoordinate) {
            if (index == 0) {
                setTile(SNAKE_Head, c.x!!, c.y!!)
            } else {
                setTile(SNAKE_BODY, c.x!!, c.y!!)
            }
            index++
        }
    }

    /**
     * 添加随机的食物
     */
    fun addRandomFood() {
        var newCoord: Coordinate? = null
        var found = false
        while (!found) {
            // 设置新食物生成的位置
            val newX: Int = random.nextInt(50)
            val newY: Int = random.nextInt(30)
            newCoord = Coordinate(newX, newY)
            var collision = false
            val snakelength: Int = snakeCoordinate.size
            //遍历snake, 看新添加的粽子是否与snake的所在坐标冲突，如果是，重新生成坐标
            for (index in 0 until snakelength) {
                if (snakeCoordinate[index].equals(newCoord)) {
                    collision = true
                }
            }
            for (index in obstacleCoordinate.indices) {
                if (obstacleCoordinate[index].equals(newCoord)) {
                    collision = true
                }
            }
            found = !collision
        }
        // 储存已产生坐标
        foodCoordinate.add(newCoord!!)
    }

    private fun addrandomObstacle(count: Int) {
        var newCoord: Coordinate? = null
        if (count == 4) {
            val newX: Int = 1 + random.nextInt(tileCountX)
            val newY: Int = 3 + random.nextInt(tileCountY)
            newCoord = Coordinate(newX, newY)
            // 遍历snake, 看新添加的apple是否在snake体内, 如果是,重新生成坐标
            // 储存已产生坐标
            foodCoordinate.add(newCoord)
        }
    }

    /**
     * 设置模式
     */
    private fun setMode(mode: Int) {
        var modeOld = MODE
        MODE = mode
        if (MODE == RUNING && modeOld != RUNING) {
            updateView()
        }
        if (MODE == LOSE) {
            // 结算
            settlement!!.onResult(score)
        }
    }

    /**
     * 初始化游戏
     */
    fun initGame() {
        snakeCoordinate.clear()
        foodCoordinate.clear()
        // 初始化蛇的初始位置
        snakeCoordinate.add(Coordinate(8, 7))
        snakeCoordinate.add(Coordinate(6, 7))
        snakeCoordinate.add(Coordinate(5, 7))
        snakeCoordinate.add(Coordinate(4, 7))
        // 设置蛇的初始移动方向
        directionCurrent = RIGHT
        directionNext = RIGHT
        addRandomFood()
        score = 0
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        // 绘制游戏界面
        var paint = Paint()
        initSnakeView()
        //遍历地图绘制界面
        for (x in 0 until tileCountX) {
            for (y in 0 until tileCountY) {
                if (tileImage!![x][y] > 0) {
                    // 被加了图片的点tileImage是大于0的
                    canvas!!.drawBitmap(
                        tileBitmap!![tileImage!![x][y]]!!,
                        (startCoordinatesX + x * tileSize).toFloat(),
                        (startCoordinatesY + y * tileSize).toFloat(),
                        paint
                    )
                }
            }
        }
    }

    /**
     * 加载图片资源
     */
    fun initSnakeView() {
        isFocusable = true
        resetTiles(4)
        //从文件中加载图片
        loadTile(SNAKE_Head, resources.getDrawable(R.drawable.head, resources.newTheme()))
        loadTile(SNAKE_BODY, resources.getDrawable(R.drawable.zongzi, resources.newTheme()))
        updateView()
    }

    var settlement: Settlement? = null

    fun setSettlementListener(setttlement: Settlement) {
        this.settlement = setttlement
    }

    interface Settlement {
        fun onResult(score: Int)
    }
}