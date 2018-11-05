package com.example.kirillstoianov.avatarsrotationview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.LruCache
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import java.util.*


/**
 * Created by Kirill Stoianov on 02.11.18.
 */
class AvatarsRotationView(context: Context) : View(context) {

    companion object {
        val TAG: String = AvatarsRotationView::class.java.simpleName
    }


    class AvatarItem {

        class Builder(context: Context) {

            private val avatarItem: AvatarItem = AvatarItem()

            fun setState(state: State): Builder {
                avatarItem.state = state
                return this
            }

            fun setSize(size: Size): Builder {
                avatarItem.size = size
                return this
            }

            fun setPosition(position: Position): Builder {
                avatarItem.position = position
                return this
            }

            fun setOffsetAngle(offsetAngle: Float): Builder {
                avatarItem.offsetAngle = offsetAngle
                return this
            }

            fun setDrawableResId(drawableResId: Int): Builder {
                avatarItem.drawableResId = drawableResId
                return this
            }

            fun build(): AvatarItem = avatarItem
        }

        var state: State = State.SHOW
        var size: Size = Size.LARGE
        var position: Position = Position.FIRST_CIRCLE
        var offsetAngle: Float = Random().nextInt(360).toFloat()
        var drawableResId: Int = 0

        enum class State { SHOW, HIDE }
        enum class Size { LARGE, SMALL }
        enum class Position { FIRST_CIRCLE, SECOND_CIRCLE }
    }

    var lruCache: LruCache<AvatarItem, Bitmap?> = LruCache(8)

    var avatarItems: ArrayList<AvatarItem> = ArrayList()
        set(value) {
            field = value
            invalidate()
        }


    private var animatedAngle: Float = 0f
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
    private val angleAnimator by lazy {
        ValueAnimator.ofFloat(0f, 1f).apply {
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            duration = 40_000
            addUpdateListener {
                animatedAngle = it.animatedValue as Float * 360
                invalidate()
            }
        }
    }

    private var largeSize: Float = calculateOwnUserAvatarRadius()
    private var smallSize: Float = getLargeAvatarRadius()

    private val showAnimator by lazy {
        ValueAnimator.ofFloat(0f, calculateOuterCircleRadius()).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = 350
            addUpdateListener {
                largeSize = it.animatedValue as Float
                if ((it.animatedValue as Float) < calculateInnerCircleRadius()) {
                    smallSize = it.animatedValue as Float
                }
            }
        }
    }

    private val hideAnimator by lazy {
        ValueAnimator.ofFloat(calculateOuterCircleRadius(), 0f).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = 350
            addUpdateListener {
                largeSize = it.animatedValue as Float
                if ((it.animatedValue as Float) < calculateInnerCircleRadius()) {
                    smallSize = it.animatedValue as Float
                }
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
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(Math.min(widthMeasureSpec, heightMeasureSpec), Math.min(widthMeasureSpec, heightMeasureSpec))
    }

    fun startAnimate() {
        angleAnimator.start()
    }

    fun animateShow() {
        showAnimator.cancel()
        showAnimator.end()
        hideAnimator.cancel()
        hideAnimator.end()

        showAnimator.start()
    }

    fun animateHide() {
        showAnimator.cancel()
        showAnimator.end()
        hideAnimator.cancel()
        hideAnimator.end()

        hideAnimator.start()
    }

    private fun drawCircles(canvas: Canvas) {
        outerCirclePath.reset()
        innerCirclePath.reset()
        outerCirclePath.addCircle(width / 2f, height / 2f, calculateOuterCircleRadius().toFloat(), Path.Direction.CCW)
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

        avatarItems.forEach { item ->
            val cx = when (item.position) {
                AvatarItem.Position.FIRST_CIRCLE -> {
                    width / 2 + (calculateInnerCircleRadius() - circleStrokeWith) * Math.cos(Math.toRadians(item.offsetAngle + animatedAngle.toDouble())).toFloat()
                }
                AvatarItem.Position.SECOND_CIRCLE -> {
                    width / 2 + (calculateOuterCircleRadius() - circleStrokeWith) * Math.cos(Math.toRadians(item.offsetAngle + animatedAngle.toDouble())).toFloat()
                }
            }

            val cy = when (item.position) {
                AvatarItem.Position.FIRST_CIRCLE -> {
                    height / 2 + (calculateInnerCircleRadius() - circleStrokeWith) * Math.sin(Math.toRadians(item.offsetAngle + animatedAngle.toDouble())).toFloat()
                }
                AvatarItem.Position.SECOND_CIRCLE -> {
                    height / 2 + (calculateOuterCircleRadius() - circleStrokeWith) * Math.sin(Math.toRadians(item.offsetAngle + animatedAngle.toDouble())).toFloat()
                }
            }

            val avatarLeft = when (item.size) {
                AvatarItem.Size.LARGE -> {
                    (cx - getLargeAvatarRadius()-largeSize / 2)
                }
                AvatarItem.Size.SMALL -> {
                    (cx - getSmallAvatarRadius() / 2)
                }
            }

            val avatarTop = when (item.size) {
                AvatarItem.Size.LARGE -> {
                    (cy - getLargeAvatarRadius() / 2)
                }
                AvatarItem.Size.SMALL -> {
                    (cy - getSmallAvatarRadius() /2)
                }
            }

            canvas.drawBitmap(getBitmap(item), avatarLeft, avatarTop, bitmapPaint)
        }
    }


    private fun calculateOuterCircleRadius(): Float = Math.min(width, height) / 2f - circleStrokeWith

    private fun calculateInnerCircleRadius(): Float {
        return Math.min(width, height) / 2 - circleStrokeWith - getLargeAvatarRadius()
    }

    private fun calculateOwnUserAvatarRadius(): Float = (width / 3f) / 2

    private fun getLargeAvatarRadius(): Float = width / 6f

    private fun getSmallAvatarRadius(): Float = width / 8f

    private fun getBitmap(avatarItem: AvatarItem): Bitmap {

        //check cache
        val exist = lruCache.get(avatarItem)
        if (exist != null) return exist

        val sourceBitmap = BitmapFactory.decodeResource(resources, avatarItem.drawableResId)

        val avatarDiameter = when (avatarItem.size) {
            AvatarItem.Size.LARGE -> {
                getLargeAvatarRadius().toInt()
            }
            AvatarItem.Size.SMALL -> {
                getSmallAvatarRadius().toInt()
            }
        }

        val resultBitmap = Bitmap.createScaledBitmap(
            sourceBitmap,
            avatarDiameter,
            avatarDiameter,
            true
        )
        sourceBitmap.recycle()

        lruCache.put(avatarItem, resultBitmap)

        return resultBitmap
    }
}