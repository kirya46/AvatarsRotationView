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

        avatarsRoationView.avatarItems = getAvatarItems()

        //start view animation

        Handler().postDelayed({
            avatarsRoationView.startAnimate()
        }, 300)

        Handler().postDelayed({
            avatarsRoationView.animateShow()
        }, 600)
    }

    fun getAvatarItems(): ArrayList<AvatarsRotationView.AvatarItem> {
        val arrayList = ArrayList<AvatarsRotationView.AvatarItem>()

        val male1= AvatarsRotationView.AvatarItem.Builder(this@MainActivity)
            .setDrawableResId(R.drawable.img_subsboost_male1)
            .setState(AvatarsRotationView.AvatarItem.State.SHOW)
            .setPosition(AvatarsRotationView.AvatarItem.Position.FIRST_CIRCLE)
            .setSize(AvatarsRotationView.AvatarItem.Size.SMALL)
            .setOffsetAngle(50f)
            .build()

        val male2 = AvatarsRotationView.AvatarItem.Builder(this@MainActivity)
            .setDrawableResId(R.drawable.img_subsboost_male2)
            .setState(AvatarsRotationView.AvatarItem.State.HIDE)
            .setPosition(AvatarsRotationView.AvatarItem.Position.FIRST_CIRCLE)
            .setSize(AvatarsRotationView.AvatarItem.Size.LARGE)
            .setOffsetAngle(100f)
            .build()

        val male3 = AvatarsRotationView.AvatarItem.Builder(this@MainActivity)
            .setDrawableResId(R.drawable.img_subsboost_male3)
            .setState(AvatarsRotationView.AvatarItem.State.HIDE)
            .setPosition(AvatarsRotationView.AvatarItem.Position.FIRST_CIRCLE)
            .setSize(AvatarsRotationView.AvatarItem.Size.SMALL)
            .setOffsetAngle(150f)
            .build()

        val male4 = AvatarsRotationView.AvatarItem.Builder(this@MainActivity)
            .setDrawableResId(R.drawable.img_subsboost_male4)
            .setState(AvatarsRotationView.AvatarItem.State.HIDE)
            .setPosition(AvatarsRotationView.AvatarItem.Position.SECOND_CIRCLE)
            .setSize(AvatarsRotationView.AvatarItem.Size.LARGE)
            .setOffsetAngle(200f)
            .build()

        val male5 = AvatarsRotationView.AvatarItem.Builder(this@MainActivity)
            .setDrawableResId(R.drawable.img_subsboost_male5)
            .setState(AvatarsRotationView.AvatarItem.State.SHOW)
            .setPosition(AvatarsRotationView.AvatarItem.Position.FIRST_CIRCLE)
            .setSize(AvatarsRotationView.AvatarItem.Size.SMALL)
            .setOffsetAngle(250f)
            .build()

        val male6 = AvatarsRotationView.AvatarItem.Builder(this@MainActivity)
            .setDrawableResId(R.drawable.img_subsboost_male6)
            .setState(AvatarsRotationView.AvatarItem.State.HIDE)
            .setPosition(AvatarsRotationView.AvatarItem.Position.SECOND_CIRCLE)
            .setSize(AvatarsRotationView.AvatarItem.Size.LARGE)
            .setOffsetAngle(300f)
            .build()

        val male7 = AvatarsRotationView.AvatarItem.Builder(this@MainActivity)
            .setDrawableResId(R.drawable.img_subsboost_male7)
            .setState(AvatarsRotationView.AvatarItem.State.HIDE)
            .setPosition(AvatarsRotationView.AvatarItem.Position.FIRST_CIRCLE)
            .setSize(AvatarsRotationView.AvatarItem.Size.LARGE)
            .setOffsetAngle(350f)
            .build()

        val male8 = AvatarsRotationView.AvatarItem.Builder(this@MainActivity)
            .setDrawableResId(R.drawable.img_subsboost_male8)
            .setState(AvatarsRotationView.AvatarItem.State.HIDE)
            .setPosition(AvatarsRotationView.AvatarItem.Position.SECOND_CIRCLE)
            .setSize(AvatarsRotationView.AvatarItem.Size.LARGE)
            .setOffsetAngle(25f)
            .build()

        arrayList.add(male1)
        arrayList.add(male2)
        arrayList.add(male3)
        arrayList.add(male4)
        arrayList.add(male5)
        arrayList.add(male6)
        arrayList.add(male7)
        arrayList.add(male8)

        return arrayList
    }
}
