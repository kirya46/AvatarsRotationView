package com.example.kirillstoianov.avatarsrotationview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.support.v4.content.ContextCompat
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

    val otherUserAvatar by lazy {
        ContextCompat.getDrawable(context, R.drawable.oval_5)
    }

    val valueAnimator by lazy {
        ValueAnimator.ofFloat(0f, 1f).apply {
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            duration = 4000
            addUpdateListener {
                val angle = it.animatedValue as Float
                animatedAngle = 360 * angle
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
        val drawable = ContextCompat.getDrawable(context, R.drawable.oval_5)

        val drawableLeft: Int = width / 2 - calculateOwnUserAvatarRadius().toInt()
        val drawableRight: Int = width / 2 + calculateOwnUserAvatarRadius().toInt()
        val drawableTop: Int = height / 2 - calculateOwnUserAvatarRadius().toInt()
        val drawableBottom: Int = height / 2 + calculateOwnUserAvatarRadius().toInt()

        drawable?.setBounds(drawableLeft, drawableTop, drawableRight, drawableBottom)
        drawable?.draw(canvas)
    }

    private fun drawOtherUsersAvatars(canvas: Canvas) {

        val x =
            width / 2 + (calculateInnerCircleRadius() - circleStrokeWith) * Math.cos(animatedAngle * Math.PI / 180).toFloat()
        val y =
            height / 2 + (calculateInnerCircleRadius() - circleStrokeWith) * Math.sin(animatedAngle * Math.PI / 180).toFloat()
        val drawableLeft: Int = (x - calculateOtherUsersAvatarRadius() / 2).toInt()
        val drawableRight: Int = (x + calculateOtherUsersAvatarRadius() / 2).toInt()
        val drawableTop: Int = (y - calculateOtherUsersAvatarRadius() / 2).toInt()
        val drawableBottom: Int = (y + calculateOtherUsersAvatarRadius() / 2).toInt()

        otherUserAvatar?.setBounds(drawableLeft, drawableTop, drawableRight, drawableBottom)
        otherUserAvatar?.draw(canvas)
    }

    private fun culculateOuterCircleRadius(): Float = Math.min(width, height) / 2f - circleStrokeWith

    private fun calculateInnerCircleRadius(): Float {
        return Math.min(width, height) / 2f - circleStrokeWith - calculateOtherUsersAvatarRadius()
    }

    private fun calculateOwnUserAvatarRadius(): Float = (width / 3f) / 2

    private fun calculateOtherUsersAvatarRadius(): Float = width / 6f

    fun startAnimate() {
        valueAnimator.start()
    }

}