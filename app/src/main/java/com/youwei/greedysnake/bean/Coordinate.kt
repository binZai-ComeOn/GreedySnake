package com.youwei.greedysnake.bean

/**
 * 记录坐标位置
 */
class Coordinate {
    /**
     * x轴坐标
     */
    var x: Int? = null

    /**
     * y轴坐标
     */
    var y: Int? = null

    constructor(x: Int?, y: Int?) {
        this.x = x
        this.y = y
    }

    /**
     * 判断坐标是否相等
     */
    fun equals(coordinate: Coordinate?): Boolean {
        return x == coordinate!!.x && y == coordinate!!.y
    }
}