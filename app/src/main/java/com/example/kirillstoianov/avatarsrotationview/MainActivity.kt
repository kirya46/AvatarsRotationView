package com.example.kirillstoianov.avatarsrotationview

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val avatarsRoationView = AvatarsRotationView(this@MainActivity)
        findViewById<ViewGroup>(R.id.container)
            .addView(avatarsRoationView)
        Handler().postDelayed({
            avatarsRoationView.startAnimate()
        }, 300)


//        val kf0 = Keyframe.ofFloat(.0f, 0f)
//        val kf1 = Keyframe.ofFloat(.25f, 90f)
//        val kf2 = Keyframe.ofFloat(.5f, 180f)
//        val kf3 = Keyframe.ofFloat(.75f, 270f)
//        val kf4 = Keyframe.ofFloat(1f, 360f)
//        val pvhRotation = PropertyValuesHolder.ofKeyframe("animatedAngle", kf0, kf1, kf2,kf3,kf4)
//        val objectAnimator = ObjectAnimator.ofPropertyValuesHolder(avatarsRoationView, pvhRotation).apply {
//            this.repeatMode = ValueAnimator.RESTART
//            this.repeatCount = ValueAnimator.INFINITE
//            this.interpolator = LinearInterpolator()
//            this.start()
//        }


//        val handler = Handler()
//        val runnable = object : Runnable {
//            private var i = 0
//
//            override fun run() {
//                i++
//                if (i > 360) {
//                    i = 0
//                }
//                avatarsRoationView.animatedAngle = i.toFloat()
//                            Log.wtf("TEST", "Angle: ${i.toFloat()}")
//
//                handler.post(this)
//            }
//        }
//
//        handler.post(runnable)
    }
}
