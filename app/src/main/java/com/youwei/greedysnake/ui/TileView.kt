package com.youwei.greedysnake.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import java.util.*

open class TileView : View {
    /**
     * 视图默认大小
     */
    var tileSize = 32

    /**
     * 地图上所能容纳的X、Y轴格数
     */
    var tileCountX = 0
    var tileCountY = 0

    /**
     * 起始坐标X、Y
     */
    var startCoordinatesX = 0
    var startCoordinatesY = 0

    /**
     * 存放各坐标对应的图片（二维数组）
     */
    var tileImage: Array<IntArray>? = null

    /**
     * 放置图片的数组
     */
    var tileBitmap: Array<Bitmap?>? = null

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    /**
     * View大小发生更改时触发
     *
     * @param w    当前View的宽度
     * @param h    当前View的高度
     * @param oldw 旧View的宽度
     * @param oldh 旧View的高度
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // 地图数据初始化
        tileCountX = Math.floor((w / tileSize).toDouble()).toInt()
        tileCountY = Math.floor((h / tileSize).toDouble()).toInt()
        // 够分成一格的分成一格, 剩下不够一格的分成两份,左边一份,右边一份
        startCoordinatesX = ((w - (tileSize * tileCountX)) / 2);
        startCoordinatesY = ((h - (tileSize * tileCountY)) / 2);
        tileImage = Array(tileCountX) { IntArray(tileCountY) }
        clearTiles()
    }

    /**
     * 将所有坐标初始化为0
     */
    fun clearTiles() {
        for (x in 0 until tileCountX) {
            for (y in 0 until tileCountY) {
                setTile(0, x, y)
            }
        }
    }

    /**
     * 给地图数组赋值
     */
    fun setTile(i: Int, x: Int, y: Int) {
        tileImage!![x][y] = i
    }

    /**
     * 加载三幅小图
     */
    fun loadTile(key: Int, drawable: Drawable) {
        val createBitmap = Bitmap.createBitmap(tileSize, tileSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(createBitmap)
        drawable.setBounds(0, 0, tileSize, tileSize)
        drawable.draw(canvas)
        tileBitmap!![key] = createBitmap
    }

    fun resetTiles(tileCount: Int) {
        tileBitmap = arrayOfNulls<Bitmap>(tileCount)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }
}