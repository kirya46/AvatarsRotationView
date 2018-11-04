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



        //start view animation
        avatarsRoationView.startAnimate()
        Handler().postDelayed({
            avatarsRoationView.animateShow()
        }, 300)
    }

    fun getAvatarItems():ArrayList<AvatarsRotationView.AvatarItem>{
        val arrayList = ArrayList<AvatarsRotationView.AvatarItem>()

        val firstBuild = AvatarsRotationView.AvatarItem.Builder(this@MainActivity)
        firstBuild.setDrawableResId(R.drawable.img_subsboost_male1)
        firstBuild.setState(AvatarsRotationView.AvatarItem.State.SHOW)
        firstBuild.setPosition(AvatarsRotationView.AvatarItem.Position.FIRST_CIRCLE)
        firstBuild.setSize(AvatarsRotationView.AvatarItem.Size.LARGE)
        firstBuild.setOffsetAngle(50f)

        arrayList.add(firstBuild.build())
        return arrayList
    }
}
