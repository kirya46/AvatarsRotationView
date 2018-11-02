package com.example.kirillstoianov.avatarsrotationview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.support.annotation.FloatRange
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator


/**
 * Created by Kirill Stoianov on 02.11.18.
 */
class AvatarsRotationView(context: Context) : View(context) {

    var animatedAngle: Float = 0f

    private val circlePaint: Paint
    private val circleStrokeWith = 16f
    private val outerCirclePath = Path()
    private val innerCirclePath = Path()

    private val bitmapPaint by lazy {
        Paint(Paint.FILTER_BITMAP_FLAG)
    }

    val ownerUserAvatar by lazy {
        val bm = BitmapFactory.decodeResource(resources, R.drawable.oval_5)
        val img = Bitmap.createScaledBitmap(
            bm,
            calculateOwnUserAvatarRadius().toInt()*2,
            calculateOwnUserAvatarRadius().toInt()*2,
            true
        )
        bm.recycle()
        return@lazy img
    }

    val otherUserAvatar by lazy {
        val bm = BitmapFactory.decodeResource(resources, R.drawable.oval_5)
        val img = Bitmap.createScaledBitmap(
            bm,
            calculateOtherUsersAvatarRadius().toInt(),
            calculateOtherUsersAvatarRadius().toInt(),
            true
        )
        bm.recycle()
        return@lazy img
    }

    val valueAnimator by lazy {
        ValueAnimator.ofFloat(0f, 360f).apply {
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            duration = 20000
//            this.setEvaluator(FloatEvaluator())
            addUpdateListener {
                val angle = it.animatedValue as Float
                animatedAngle =angle
                invalidate()
            }
        }
    }

    init {

        setLayerType(View.LAYER_TYPE_HARDWARE, null)

        circlePaint = Paint().apply {
            this.color = Color.parseColor("#eaeaea")
            this.strokeWidth = circleStrokeWith
            this.style = Paint.Style.STROKE
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.apply { drawCircles(this) }
        canvas?.apply { drawOwnerUserAvatar(this) }
        canvas?.apply { drawOtherUsersAvatars(this) }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(Math.min(widthMeasureSpec, heightMeasureSpec), Math.min(widthMeasureSpec, heightMeasureSpec))
    }

    private fun drawCircles(canvas: Canvas) {
        outerCirclePath.addCircle(width / 2f, height / 2f, culculateOuterCircleRadius(), Path.Direction.CCW)
        innerCirclePath.addCircle(width / 2f, height / 2f, calculateInnerCircleRadius(), Path.Direction.CCW)
        canvas.drawPath(outerCirclePath, circlePaint)
        canvas.drawPath(innerCirclePath, circlePaint)
    }

    private fun drawOwnerUserAvatar(canvas: Canvas) {

        val drawableLeft = width / 2 - calculateOwnUserAvatarRadius()
        val drawableTop = height / 2 - calculateOwnUserAvatarRadius()
//        val drawableRight: Int = width / 2 + calculateOwnUserAvatarRadius().toInt()
//        val drawableBottom: Int = height / 2 + calculateOwnUserAvatarRadius().toInt()

        canvas.drawBitmap(ownerUserAvatar,drawableLeft,drawableTop,bitmapPaint)
    }

    private fun drawOtherUsersAvatars(canvas: Canvas) {

        val x =
            width / 2 + (calculateInnerCircleRadius() - circleStrokeWith) * Math.cos(Math.toRadians(animatedAngle.toDouble())).toFloat()
        val y =
            height / 2 + (calculateInnerCircleRadius() - circleStrokeWith) * Math.sin(Math.toRadians(animatedAngle.toDouble())).toFloat()
        val drawableLeft = (x - calculateOtherUsersAvatarRadius() / 2)
        val drawableTop = (y - calculateOtherUsersAvatarRadius() / 2)

        Log.wtf("TEST","X: $x Y: $y") //todo remove this line


        canvas.drawBitmap(otherUserAvatar, drawableLeft, drawableTop, bitmapPaint)
    }

    private fun culculateOuterCircleRadius(): Float = Math.min(width, height) / 2f - circleStrokeWith

    private fun calculateInnerCircleRadius(): Float {
        return Math.min(width, height) / 2f - circleStrokeWith - calculateOtherUsersAvatarRadius()
    }

    private fun calculateOwnUserAvatarRadius(): Float = (width / 3f) / 2

    private fun calculateOtherUsersAvatarRadius(): Float = width / 6f

    fun startAnimate() {

        valueAnimator.start()

//        val handler = Handler()
//        val runnable = object : Runnable {
//            private var i = 0
//
//            override fun run() {
//                i++
//                if (i == 360) {
//                    i = 0
//                    handler.removeCallbacks(this)
//                }
//                animatedAngle = i
//                invalidate()
//
//                handler.post(this)
//            }
//        }
//
//        handler.post(runnable)
    }

}