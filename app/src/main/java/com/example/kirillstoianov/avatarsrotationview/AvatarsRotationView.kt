package com.example.kirillstoianov.avatarsrotationview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.view.View
import android.view.animation.LinearInterpolator


/**
 * Created by Kirill Stoianov on 02.11.18.
 */
class AvatarsRotationView(context: Context) : View(context) {

    companion object {
        val TAG: String = AvatarsRotationView::class.java.simpleName
    }

    var animatedAngle: Float = 0f

    private val circleStrokeWith: Int = 4
    private val outerCirclePath = Path()
    private val innerCirclePath = Path()

    private val bitmapPaint by lazy {
        Paint(Paint.FILTER_BITMAP_FLAG).apply {
            isAntiAlias = true
        }
    }

    private val circlePaint by lazy {
        Paint().apply {
            this.color = Color.parseColor("#eaeaea")
            this.strokeWidth = circleStrokeWith.toFloat()
            this.style = Paint.Style.STROKE
            this.isAntiAlias = true
        }
    }

    private val ownerUserAvatar by lazy {
        val bm = BitmapFactory.decodeResource(resources, R.drawable.oval_5)
        val img = Bitmap.createScaledBitmap(
            bm,
            calculateOwnUserAvatarRadius().toInt() * 2,
            calculateOwnUserAvatarRadius().toInt() * 2,
            true
        )
        bm.recycle()
        return@lazy img
    }

    private val otherUserAvatar by lazy {
        val bm = BitmapFactory.decodeResource(resources, R.drawable.img_subsboost_female1)
        val img = Bitmap.createScaledBitmap(
            bm,
            calculateOtherUsersAvatarRadius().toInt(),
            calculateOtherUsersAvatarRadius().toInt(),
            true
        )
        bm.recycle()
        return@lazy img
    }

    private val valueAnimator by lazy {
        ValueAnimator.ofFloat(0f, 1f).apply {
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            duration = 40_000
            addUpdateListener {
                animatedAngle = it.animatedValue as Float * 360
                postInvalidate()
            }
        }
    }

    init {
        setLayerType(View.LAYER_TYPE_NONE, null)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.apply { drawCircles(this) }
        canvas?.apply { drawOwnerUserAvatar(this) }

        canvas?.apply { drawOtherUsersAvatars(this) }
        canvas?.apply { drawTest(this) }
        canvas?.apply { drawTest2(this) }
        canvas?.apply { drawTest3(this) }
        canvas?.apply { drawTest4(this) }
        canvas?.apply { drawTest5(this) }
        canvas?.apply { drawTest6(this) }
        canvas?.apply { drawTest7(this) }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(Math.min(widthMeasureSpec, heightMeasureSpec), Math.min(widthMeasureSpec, heightMeasureSpec))
    }

    private fun drawCircles(canvas: Canvas) {
        outerCirclePath.reset()
        innerCirclePath.reset()
        outerCirclePath.addCircle(width / 2f, height / 2f, culculateOuterCircleRadius().toFloat(), Path.Direction.CCW)
        innerCirclePath.addCircle(width / 2f, height / 2f, calculateInnerCircleRadius().toFloat(), Path.Direction.CCW)
        canvas.drawPath(outerCirclePath, circlePaint)
        canvas.drawPath(innerCirclePath, circlePaint)
    }

    private fun drawOwnerUserAvatar(canvas: Canvas) {

        val drawableLeft = width / 2 - calculateOwnUserAvatarRadius()
        val drawableTop = height / 2 - calculateOwnUserAvatarRadius()
//        val drawableRight: Int = width / 2 + calculateOwnUserAvatarRadius().toInt()
//        val drawableBottom: Int = height / 2 + calculateOwnUserAvatarRadius().toInt()

        canvas.drawBitmap(ownerUserAvatar, drawableLeft.toFloat(), drawableTop.toFloat(), bitmapPaint)
    }

    private fun drawOtherUsersAvatars(canvas: Canvas) {

        val x =
            width / 2 + (calculateInnerCircleRadius() - circleStrokeWith) * Math.cos(Math.toRadians(animatedAngle.toDouble())).toFloat()
        val y =
            height / 2 + (calculateInnerCircleRadius() - circleStrokeWith) * Math.sin(Math.toRadians(animatedAngle.toDouble())).toFloat()
        val drawableLeft = (x - calculateOtherUsersAvatarRadius() / 2)
        val drawableTop = (y - calculateOtherUsersAvatarRadius() / 2)
        val drawableRight = (x + calculateOtherUsersAvatarRadius() / 2)
        val drawableBottom = y + calculateOtherUsersAvatarRadius() / 2

        canvas.drawBitmap(otherUserAvatar, drawableLeft, drawableTop, bitmapPaint)
//        canvas.drawOval(drawableLeft, drawableTop,drawableRight,drawableBottom, circlePaint)

    }

    private fun drawTest(canvas: Canvas) {

        val x =
            width / 2 + (culculateOuterCircleRadius() - circleStrokeWith) * Math.cos(Math.toRadians(270 + animatedAngle.toDouble())).toFloat()
        val y =
            height / 2 + (culculateOuterCircleRadius() - circleStrokeWith) * Math.sin(Math.toRadians(270 + animatedAngle.toDouble())).toFloat()
        val drawableLeft = (x - calculateOtherUsersAvatarRadius() / 2)
        val drawableTop = (y - calculateOtherUsersAvatarRadius() / 2)
        val drawableRight = (x + calculateOtherUsersAvatarRadius() / 2)
        val drawableBottom = y + calculateOtherUsersAvatarRadius() / 2

        canvas.drawBitmap(otherUserAvatar, drawableLeft, drawableTop, bitmapPaint)
//        canvas.drawOval(drawableLeft, drawableTop,drawableRight,drawableBottom, circlePaint)
    }

    private fun drawTest2(canvas: Canvas) {

        val x =
            width / 2 + (calculateInnerCircleRadius() - circleStrokeWith) * Math.cos(Math.toRadians(80 + animatedAngle.toDouble())).toFloat()
        val y =
            height / 2 + (calculateInnerCircleRadius() - circleStrokeWith) * Math.sin(Math.toRadians(80 + animatedAngle.toDouble())).toFloat()
        val drawableLeft = (x - calculateOtherUsersAvatarRadius() / 2)
        val drawableTop = (y - calculateOtherUsersAvatarRadius() / 2)
        val drawableRight = (x + calculateOtherUsersAvatarRadius() / 2)
        val drawableBottom = y + calculateOtherUsersAvatarRadius() / 2

        canvas.drawBitmap(otherUserAvatar, drawableLeft, drawableTop, bitmapPaint)
//        canvas.drawOval(drawableLeft, drawableTop,drawableRight,drawableBottom, circlePaint)
    }


    private fun drawTest3(canvas: Canvas) {

        val x =
            width / 2 + (calculateInnerCircleRadius() - circleStrokeWith) * Math.cos(Math.toRadians(170 + animatedAngle.toDouble())).toFloat()
        val y =
            height / 2 + (calculateInnerCircleRadius() - circleStrokeWith) * Math.sin(Math.toRadians(170 + animatedAngle.toDouble())).toFloat()
        val drawableLeft = (x - calculateOtherUsersAvatarRadius() / 2)
        val drawableTop = (y - calculateOtherUsersAvatarRadius() / 2)
        val drawableRight = (x + calculateOtherUsersAvatarRadius() / 2)
        val drawableBottom = y + calculateOtherUsersAvatarRadius() / 2

        canvas.drawBitmap(otherUserAvatar, drawableLeft, drawableTop, bitmapPaint)
//        canvas.drawOval(drawableLeft, drawableTop,drawableRight,drawableBottom, circlePaint)
    }

    private fun drawTest4(canvas: Canvas) {

        val x =
            width / 2 + (calculateInnerCircleRadius() - circleStrokeWith) * Math.cos(Math.toRadians(68 + animatedAngle.toDouble())).toFloat()
        val y =
            height / 2 + (calculateInnerCircleRadius() - circleStrokeWith) * Math.sin(Math.toRadians(68 + animatedAngle.toDouble())).toFloat()
        val drawableLeft = (x - calculateOtherUsersAvatarRadius() / 2)
        val drawableTop = (y - calculateOtherUsersAvatarRadius() / 2)
        val drawableRight = (x + calculateOtherUsersAvatarRadius() / 2)
        val drawableBottom = y + calculateOtherUsersAvatarRadius() / 2

        canvas.drawBitmap(otherUserAvatar, drawableLeft, drawableTop, bitmapPaint)
//        canvas.drawOval(drawableLeft, drawableTop,drawableRight,drawableBottom, circlePaint)
    }

    private fun drawTest5(canvas: Canvas) {

        val x =
            width / 2 + (culculateOuterCircleRadius() - circleStrokeWith) * Math.cos(Math.toRadians(125 + animatedAngle.toDouble())).toFloat()
        val y =
            height / 2 + (culculateOuterCircleRadius() - circleStrokeWith) * Math.sin(Math.toRadians(125 + animatedAngle.toDouble())).toFloat()
        val drawableLeft = (x - calculateOtherUsersAvatarRadius() / 2)
        val drawableTop = (y - calculateOtherUsersAvatarRadius() / 2)
        val drawableRight = (x + calculateOtherUsersAvatarRadius() / 2)
        val drawableBottom = y + calculateOtherUsersAvatarRadius() / 2

        canvas.drawBitmap(otherUserAvatar, drawableLeft, drawableTop, bitmapPaint)
//        canvas.drawOval(drawableLeft, drawableTop,drawableRight,drawableBottom, circlePaint)
    }

    private fun drawTest6(canvas: Canvas) {

        val x =
            width / 2 + (calculateInnerCircleRadius() - circleStrokeWith) * Math.cos(Math.toRadians(45 + animatedAngle.toDouble())).toFloat()
        val y =
            height / 2 + (calculateInnerCircleRadius() - circleStrokeWith) * Math.sin(Math.toRadians(45 + animatedAngle.toDouble())).toFloat()
        val drawableLeft = (x - calculateOtherUsersAvatarRadius() / 2)
        val drawableTop = (y - calculateOtherUsersAvatarRadius() / 2)
        val drawableRight = (x + calculateOtherUsersAvatarRadius() / 2)
        val drawableBottom = y + calculateOtherUsersAvatarRadius() / 2

        canvas.drawBitmap(otherUserAvatar, drawableLeft, drawableTop, bitmapPaint)
//        canvas.drawOval(drawableLeft, drawableTop,drawableRight,drawableBottom, circlePaint)
    }

    private fun drawTest7(canvas: Canvas) {

        val x =
            width / 2 + (culculateOuterCircleRadius() - circleStrokeWith) * Math.cos(Math.toRadians(300 + animatedAngle.toDouble())).toFloat()
        val y =
            height / 2 + (culculateOuterCircleRadius() - circleStrokeWith) * Math.sin(Math.toRadians(300 + animatedAngle.toDouble())).toFloat()
        val drawableLeft = (x - calculateOtherUsersAvatarRadius() / 2)
        val drawableTop = (y - calculateOtherUsersAvatarRadius() / 2)
        val drawableRight = (x + calculateOtherUsersAvatarRadius() / 2)
        val drawableBottom = y + calculateOtherUsersAvatarRadius() / 2

        canvas.drawBitmap(otherUserAvatar, drawableLeft, drawableTop, bitmapPaint)
//        canvas.drawOval(drawableLeft, drawableTop,drawableRight,drawableBottom, circlePaint)
    }

    private fun culculateOuterCircleRadius(): Float = Math.min(width, height) / 2f - circleStrokeWith

    private fun calculateInnerCircleRadius(): Float {
        return Math.min(width, height) / 2 - circleStrokeWith - calculateOtherUsersAvatarRadius()
    }

    private fun calculateOwnUserAvatarRadius(): Float = (width / 3f) / 2

    private fun calculateOtherUsersAvatarRadius(): Float = width / 6f

    fun startAnimate() {
        valueAnimator.start()
    }

}