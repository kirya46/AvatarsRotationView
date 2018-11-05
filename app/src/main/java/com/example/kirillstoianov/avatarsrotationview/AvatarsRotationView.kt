package com.example.kirillstoianov.avatarsrotationview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.LruCache
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import java.util.*


/**
 * Created by Kirill Stoianov on 02.11.18.
 */
class AvatarsRotationView(context: Context) : View(context) {

    companion object {
        val TAG: String = AvatarsRotationView::class.java.simpleName
    }

    var avatarItems: ArrayList<AvatarItem> = ArrayList()
        set(value) {
            field = value
            invalidate()
        }


    private var lruCache: LruCache<AvatarItem, Bitmap?> = LruCache(8)

    //Animated values
    private var animatedAngle: Float = 0f
    private var largeSize: Float = 1f
    private var smallSize: Float = 1f

    //circles values
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

//    private val ownerUserAvatar by lazy {
//        val bm = BitmapFactory.decodeResource(resources, R.drawable.oval_5)
//        val img = Bitmap.createScaledBitmap(
//            bm,
//            getCenterAvatarRadius().toInt() * 2,
//            getCenterAvatarRadius().toInt() * 2,
//            true
//        )
//        bm.recycle()
//        return@lazy img
//    }

     var ownerUserAvatarUrl: String? = null
        set(value) {
            field = value

            if (value == null) {
                ownerUserAvatar?.recycle()
                ownerUserAvatar = null
                return
            }

            loadUserPhoto()
        }

    private var ownerUserAvatar: Bitmap? = null
    set(value) {
        field = value
        invalidate()
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

    private val showAnimator by lazy {
        ValueAnimator.ofFloat(1f, getLargeAvatarRadius()).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = 350
            addUpdateListener {
                largeSize = it.animatedValue as Float
                if ((it.animatedValue as Float) <= getSmallAvatarRadius()) {
                    smallSize = it.animatedValue as Float
                }
            }
        }
    }

    private val hideLargeAnimator by lazy {
        ValueAnimator.ofFloat(getLargeAvatarRadius(), 1f).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = 350
            addUpdateListener {
                largeSize = it.animatedValue as Float
            }
        }
    }

    private val hideSmallAnimator by lazy {
        ValueAnimator.ofFloat(getSmallAvatarRadius(), 1f).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = 350
            addUpdateListener {
                smallSize = it.animatedValue as Float
            }
        }
    }

    init {
        setLayerType(View.LAYER_TYPE_NONE, null)

        loadUserPhoto()
    }

    private fun loadUserPhoto() {
        ownerUserAvatarUrl?.apply {

            Glide
                .with(context)
                .asBitmap()
                .load(this)
                .apply(RequestOptions().circleCrop())
                .addListener(object : RequestListener<Bitmap> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        TODO("Set placeholder") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onResourceReady(
                        resource: Bitmap?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {

                        if (resource == null) return isFirstResource

                        val img = Bitmap.createScaledBitmap(
                            resource,
                            getCenterAvatarRadius().toInt() * 2,
                            getCenterAvatarRadius().toInt() * 2,
                            true
                        )
                        resource.recycle()
                        ownerUserAvatar = img

                        return isFirstResource
                    }
                })
                .submit()
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

    fun startAnimate() {
        angleAnimator.start()
    }

    fun animateShow() {
        hideLargeAnimator.cancel()
        hideLargeAnimator.end()
        hideSmallAnimator.cancel()
        hideSmallAnimator.end()
        showAnimator.cancel()
        showAnimator.end()

        showAnimator.start()
    }

    fun animateHide() {
        showAnimator.cancel()
        showAnimator.end()
        hideSmallAnimator.cancel()
        hideSmallAnimator.end()
        hideLargeAnimator.cancel()
        hideLargeAnimator.end()

        hideLargeAnimator.start()
        hideSmallAnimator.start()
    }

    private fun drawCircles(canvas: Canvas) {
        outerCirclePath.reset()
        innerCirclePath.reset()
        outerCirclePath.addCircle(width / 2f, height / 2f, getOuterCircleRadius(), Path.Direction.CCW)
        innerCirclePath.addCircle(width / 2f, height / 2f, getInnerCircleRadius(), Path.Direction.CCW)
        canvas.drawPath(outerCirclePath, circlePaint)
        canvas.drawPath(innerCirclePath, circlePaint)
    }

    private fun drawOwnerUserAvatar(canvas: Canvas) {

        val drawableLeft = width / 2 - getCenterAvatarRadius()
        val drawableTop = height / 2 - getCenterAvatarRadius()
        //val drawableRight: Int = width / 2 + getCenterAvatarRadius().toInt()
        //val drawableBottom: Int = height / 2 + getCenterAvatarRadius().toInt()

        ownerUserAvatar?.apply { canvas.drawBitmap(this, drawableLeft, drawableTop, bitmapPaint) }
    }

    private fun drawOtherUsersAvatars(canvas: Canvas) {

        avatarItems.forEach { item ->
            val cx = when (item.position) {
                AvatarItem.Position.FIRST_CIRCLE -> {
                    width / 2 + (getInnerCircleRadius() - circleStrokeWith) * Math.cos(Math.toRadians(item.offsetAngle + animatedAngle.toDouble())).toFloat()
                }
                AvatarItem.Position.SECOND_CIRCLE -> {
                    width / 2 + (getOuterCircleRadius() - circleStrokeWith) * Math.cos(Math.toRadians(item.offsetAngle + animatedAngle.toDouble())).toFloat()
                }
            }

            val cy = when (item.position) {
                AvatarItem.Position.FIRST_CIRCLE -> {
                    height / 2 + (getInnerCircleRadius() - circleStrokeWith) * Math.sin(Math.toRadians(item.offsetAngle + animatedAngle.toDouble())).toFloat()
                }
                AvatarItem.Position.SECOND_CIRCLE -> {
                    height / 2 + (getOuterCircleRadius() - circleStrokeWith) * Math.sin(Math.toRadians(item.offsetAngle + animatedAngle.toDouble())).toFloat()
                }
            }

            val avatarLeft: Float = if (item.type == AvatarItem.Type.ANIMATED) {
                when (item.size) {
                    AvatarItem.Size.LARGE -> {
                        (cx - largeSize / 2)
                    }
                    AvatarItem.Size.SMALL -> {
                        (cx - smallSize / 2)
                    }
                }
            } else {
                when (item.size) {
                    AvatarItem.Size.LARGE -> {
                        (cx - getLargeAvatarRadius() / 2)
                    }
                    AvatarItem.Size.SMALL -> {
                        (cx - getSmallAvatarRadius() / 2)
                    }
                }
            }


            val avatarTop = if (item.type == AvatarItem.Type.ANIMATED) {
                when (item.size) {
                    AvatarItem.Size.LARGE -> {
                        (cy - largeSize / 2)
                    }
                    AvatarItem.Size.SMALL -> {
                        (cy - smallSize / 2)
                    }
                }
            } else {
                when (item.size) {
                    AvatarItem.Size.LARGE -> {
                        (cy - getLargeAvatarRadius() / 2)
                    }
                    AvatarItem.Size.SMALL -> {
                        (cy - getSmallAvatarRadius() / 2)
                    }
                }
            }

            var bitmap = getBitmap(item)


            if (item.type == AvatarItem.Type.ANIMATED) {
                when (item.size) {
                    AvatarItem.Size.SMALL -> {
                        bitmap = Bitmap.createScaledBitmap(
                            getBitmap(item),
                            smallSize.toInt(),
                            smallSize.toInt(),
                            true
                        )
                    }
                    AvatarItem.Size.LARGE -> {
                        bitmap = Bitmap.createScaledBitmap(
                            getBitmap(item),
                            largeSize.toInt(),
                            largeSize.toInt(),
                            true
                        )
                    }
                }
            }

            canvas.drawBitmap(bitmap, avatarLeft, avatarTop, bitmapPaint)
        }
    }

    private fun getOuterCircleRadius(): Float =
        (Math.min(width, height) - getLargeAvatarRadius()) / 2f - circleStrokeWith

    private fun getInnerCircleRadius(): Float {
        return (Math.min(width, height) - getLargeAvatarRadius()) / 2 - circleStrokeWith - getLargeAvatarRadius()
    }

    private fun getCenterAvatarRadius(): Float = ((Math.min(width, height) - getLargeAvatarRadius()) / 3f) / 2

    private fun getLargeAvatarRadius(): Float = width / 7f

    private fun getSmallAvatarRadius(): Float = width / 9f

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


    class AvatarItem {

        class Builder(context: Context) {

            private val avatarItem: AvatarItem = AvatarItem()

            fun setType(type: Type): Builder {
                avatarItem.type = type
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

        var type: Type = Type.DEFAULT
        var size: Size = Size.LARGE
        var position: Position = Position.FIRST_CIRCLE
        var offsetAngle: Float = Random().nextInt(360).toFloat()
        var drawableResId: Int = 0

        enum class Type { DEFAULT, ANIMATED }
        enum class Size { LARGE, SMALL }
        enum class Position { FIRST_CIRCLE, SECOND_CIRCLE }
    }
}