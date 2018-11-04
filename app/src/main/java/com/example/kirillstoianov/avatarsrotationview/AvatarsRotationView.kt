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

            fun setState(state: State): AvatarItem {
                avatarItem.state = state
                return avatarItem
            }

            fun setSize(size: Size): AvatarItem {
                avatarItem.size = size
                return avatarItem
            }

            fun setPosition(position: Position): AvatarItem {
                avatarItem.position = position
                return avatarItem
            }

            fun setOffsetAngle(offsetAngle: Float): AvatarItem {
                avatarItem.offsetAngle = offsetAngle
                return avatarItem
            }

            fun setDrawableResId(drawableResId: Int): AvatarItem {
                avatarItem.drawableResId = drawableResId
                return avatarItem
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

    private val angleAnimator by lazy {
        ValueAnimator.ofFloat(0f, 1f).apply {
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            duration = 40_000
            addUpdateListener {
                animatedAngle = it.animatedValue as Float * 360
            }
        }
    }

    private var largeSize: Float = calculateOwnUserAvatarRadius()
    private var smallSize: Float = calculateOtherUsersAvatarRadius()

    private val showAnimator by lazy {
        ValueAnimator.ofFloat(0f, culculateOuterCircleRadius()).apply {
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
        ValueAnimator.ofFloat(culculateOuterCircleRadius(), 0f).apply {
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

        avatarItems.forEach { item ->
            //todo: draw items
        }
    }


    private fun culculateOuterCircleRadius(): Float = Math.min(width, height) / 2f - circleStrokeWith

    private fun calculateInnerCircleRadius(): Float {
        return Math.min(width, height) / 2 - circleStrokeWith - calculateOtherUsersAvatarRadius()
    }

    private fun calculateOwnUserAvatarRadius(): Float = (width / 3f) / 2

    private fun calculateOtherUsersAvatarRadius(): Float = width / 6f

    private fun getBitmap(avatarItem: AvatarItem): Bitmap {

        //check cache
        val exist = lruCache.get(avatarItem)
        if (exist != null) return exist

        val sourceBitmap = BitmapFactory.decodeResource(resources, avatarItem.drawableResId)

        val avatarDiameter = when (avatarItem.size) {
            AvatarItem.Size.LARGE -> {
                calculateOtherUsersAvatarRadius().toInt() * 2
            }
            AvatarItem.Size.SMALL -> {
                calculateOtherUsersAvatarRadius().toInt()
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