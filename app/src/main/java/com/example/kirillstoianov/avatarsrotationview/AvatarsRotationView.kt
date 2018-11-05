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

    /**
     * Avatar url for center image.
     */
    var ownerUserAvatarUrl: String? = null
        set(value) {
            field = value

            if (field == null) {
                ownerUserAvatarBitmap?.recycle()
                ownerUserAvatarBitmap = null
                invalidate()
                return
            }

            loadUserPhoto()
        }

    /**
     * Avatar items which must be rotate around center.
     */
    var avatarItems: ArrayList<AvatarItem> = ArrayList()
        set(value) {
            field = value
            invalidate()
        }

    /**
     * Rotation angle.
     * Used when draw [avatarItems].
     *
     * NOTE: this value will change with [angleAnimator].
     */
    private var animatedAngle: Float = 0f

    /**
     * Size of avatars with [AvatarItem.size] == [AvatarItem.Size.LARGE].
     * Used when draw [avatarItems].
     *
     * NOTE: this value will change with [showAnimator] and [hideLargeAnimator].
     */
    private var largeSize: Float = 1f

    /**
     * Size of avatars with [AvatarItem.size] == [AvatarItem.Size.SMALL].
     * Used when draw [avatarItems].
     *
     * NOTE: this value will change with [showAnimator] and [hideSmallAnimator].
     */
    private var smallSize: Float = 1f

    /**
     * Background circles stroke with.
     */
    private val circleStrokeWith: Int = 4

    /**
     * Outer background circle [Path].
     */
    private val outerCirclePath = Path()

    /**
     * Inner background circle [Path].
     */
    private val innerCirclePath = Path()

    /**
     * [LruCache] for bitmaps.
     */
    private var lruCache: LruCache<AvatarItem, Bitmap?> = LruCache(8)

    /**
     * Bitmap paint.
     */
    private val bitmapPaint by lazy {
        Paint(Paint.FILTER_BITMAP_FLAG).apply {
            isAntiAlias = true
        }
    }

    /**
     * Background circles paint.
     */
    private val circlePaint by lazy {
        Paint().apply {
            this.color = Color.parseColor("#eaeaea")
            this.strokeWidth = circleStrokeWith.toFloat()
            this.style = Paint.Style.STROKE
            this.isAntiAlias = true
        }
    }

    /**
     * User avatar bitmap for draw in center of view.
     */
    private var ownerUserAvatarBitmap: Bitmap? = null
        set(value) {
            field = value
            invalidate()
        }

    /**
     * Rotation angle animator.
     */
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

    /**
     * Show animator for all [avatarItems].
     */
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

    /**
     * Hide animator for [AvatarItem]'s where [AvatarItem.size] == [AvatarItem.Size.LARGE].
     */
    private val hideLargeAnimator by lazy {
        ValueAnimator.ofFloat(getLargeAvatarRadius(), 1f).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = 350
            addUpdateListener {
                largeSize = it.animatedValue as Float
            }
        }
    }

    /**
     * Hide animator for [AvatarItem]'s where [AvatarItem.size] == [AvatarItem.Size.SMALL].
     */
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


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.apply { drawBackgroundCirclesCircles(this) }
        canvas?.apply { drawCenterAvatar(this) }
        canvas?.apply { drawAvatarItems(this) }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(Math.min(widthMeasureSpec, heightMeasureSpec), Math.min(widthMeasureSpec, heightMeasureSpec))
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        avatarItems.forEach {
            val bitmap = lruCache.get(it)
            bitmap?.recycle()
        }
    }

    /**
     * Start play rotation animation for all [avatarItems].
     */
    fun startAnimate() {
        angleAnimator.start()
    }

    /**
     * Play show animation for [AvatarItem] where [AvatarItem.type] == [AvatarItem.Type.ANIMATED].
     */
    fun animateShow() {
        hideLargeAnimator.cancel()
        hideLargeAnimator.end()
        hideSmallAnimator.cancel()
        hideSmallAnimator.end()
        showAnimator.cancel()
        showAnimator.end()

        showAnimator.start()
    }

    /**
     * Play hide animation for [AvatarItem] where [AvatarItem.type] == [AvatarItem.Type.ANIMATED].
     */
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

    /**
     * Draw two background circles.
     */
    private fun drawBackgroundCirclesCircles(canvas: Canvas) {
        outerCirclePath.reset()
        innerCirclePath.reset()
        outerCirclePath.addCircle(width / 2f, height / 2f, getOuterCircleRadius(), Path.Direction.CCW)
        innerCirclePath.addCircle(width / 2f, height / 2f, getInnerCircleRadius(), Path.Direction.CCW)
        canvas.drawPath(outerCirclePath, circlePaint)
        canvas.drawPath(innerCirclePath, circlePaint)
    }

    /**
     * Draw center avatar.
     */
    private fun drawCenterAvatar(canvas: Canvas) {

        val drawableLeft = width / 2 - getCenterAvatarRadius()
        val drawableTop = height / 2 - getCenterAvatarRadius()
        //val drawableRight: Int = width / 2 + getCenterAvatarRadius().toInt()
        //val drawableBottom: Int = height / 2 + getCenterAvatarRadius().toInt()

        ownerUserAvatarBitmap?.apply { canvas.drawBitmap(this, drawableLeft, drawableTop, bitmapPaint) }
    }

    /**
     * Draw [avatarItems] around center.
     */
    private fun drawAvatarItems(canvas: Canvas) {

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

    /**
     * Get radius for outer background circle.
     */
    private fun getOuterCircleRadius(): Float =
        (Math.min(width, height) - getLargeAvatarRadius()) / 2f - circleStrokeWith

    /**
     * Get radius for inner background circle.
     */
    private fun getInnerCircleRadius(): Float {
        return (Math.min(width, height) - getLargeAvatarRadius()) / 2 - circleStrokeWith - getLargeAvatarRadius()
    }

    /**
     * Get radius of center user avatar.
     */
    private fun getCenterAvatarRadius(): Float = ((Math.min(width, height) - getLargeAvatarRadius()) / 3f) / 2

    /**
     * Get avatar radius for items where [AvatarItem.size] = [AvatarItem.Size.LARGE].
     */
    private fun getLargeAvatarRadius(): Float = Math.min(width, height) / 7f

    /**
     * Get avatar radius for items where [AvatarItem.size] = [AvatarItem.Size.SMALL].
     */
    private fun getSmallAvatarRadius(): Float = Math.min(width, height) / 9f

    /**
     * Get bitmap for destination [avatarItem].
     *
     * @param avatarItem - destination item for which [AvatarItem.drawableResId]
     * need to get bitmap.
     */
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

    /**
     * Load user avatar and save to local variable.
     *
     * @see ownerUserAvatarUrl
     * @see ownerUserAvatarBitmap
     */
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
                        ownerUserAvatarBitmap = img

                        return isFirstResource
                    }
                })
                .submit()
        }
    }

    /**
     * Holder for user avatar items configuration.
     */
    class AvatarItem {

        class Builder {

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

        /**
         * Items angle offset for start position.
         */
        var offsetAngle: Float = Random().nextInt(360).toFloat()

        /**
         * Drawable resource id for this item.
         */
        var drawableResId: Int = 0

        /**
         * Type for separate items which always must be shown
         * from items which can show/hide with animation.
         */
        enum class Type {
            /**
             * Items with this type will never animated
             * and always showed.
             */
            DEFAULT,

            /**
             * Items with this type can be hided and must be animated
             * when [AvatarsRotationView.animateShow]/[AvatarsRotationView.animateHide] called.
             */
            ANIMATED
        }

        /**
         * Item image size.
         */
        enum class Size { LARGE, SMALL }

        /**
         * Item position on inner or outer circle.
         */
        enum class Position { FIRST_CIRCLE, SECOND_CIRCLE }
    }
}