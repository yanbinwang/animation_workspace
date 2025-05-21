package com.example.material.activity

import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.Scene
import android.transition.Transition
import android.transition.TransitionInflater
import android.transition.TransitionManager
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.example.animation.R

class AnimationsActivity2 : BaseActivity() {
    private val DELAY = 100
    private val viewsToAnimate = ArrayList<View>()
    private var scene0: Scene? = null
    private var scene1: Scene? = null
    private var scene2: Scene? = null
    private var scene3: Scene? = null
    private var scene4: Scene? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animations2)
        //初始化
        setUpWindowAnimations()
        setupLayout()
        setupToolbar()
    }

    private fun setUpWindowAnimations() {
        window.enterTransition = TransitionInflater.from(this).inflateTransition(R.transition.transition_slide_from_bottom)
        window.enterTransition.addListener(object : Transition.TransitionListener {
            override fun onTransitionStart(transition: Transition) {
            }

            override fun onTransitionCancel(transition: Transition) {
            }

            override fun onTransitionPause(transition: Transition) {
            }

            override fun onTransitionResume(transition: Transition) {
            }

            override fun onTransitionEnd(transition: Transition) {
                window.enterTransition.removeListener(this)
                TransitionManager.go(scene0)
            }
        })
    }

    private fun setupLayout() {
        val activityRoot = findViewById<LinearLayout>(R.id.buttons_group)
        val sceneRoot = findViewById<FrameLayout>(R.id.scene_root)
        scene0 = Scene.getSceneForLayout(sceneRoot, R.layout.view_animations_scene0, this)
        scene0?.setEnterAction {
            for (i in viewsToAnimate.indices) {
                val child = viewsToAnimate[i]
                child.animate()
                    .setStartDelay((i * DELAY).toLong())
                    .scaleX(1f)
                    .scaleY(1f)
            }
        }
        scene0?.setExitAction {
            TransitionManager.beginDelayedTransition(activityRoot)
            val title = scene0?.sceneRoot?.findViewById<TextView>(R.id.scene0_title)
            title?.scaleX = 0f
            title?.scaleY = 0f
        }
        scene1 = Scene.getSceneForLayout(sceneRoot, R.layout.view_animations_scene1, this)
        scene2 = Scene.getSceneForLayout(sceneRoot, R.layout.view_animations_scene2, this)
        scene3 = Scene.getSceneForLayout(sceneRoot, R.layout.view_animations_scene3, this)
        scene4 = Scene.getSceneForLayout(sceneRoot, R.layout.view_animations_scene4, this)
        val button1 = findViewById<Button>(R.id.sample3_button1)
        button1.setOnClickListener {
            TransitionManager.go(scene1, ChangeBounds())
        }
        val button2 = findViewById<Button>(R.id.sample3_button2)
        button2.setOnClickListener {
            TransitionManager.go(scene2, TransitionInflater.from(this@AnimationsActivity2).inflateTransition(R.transition.transition_slide_and_changebounds))
        }
        val button3 = findViewById<Button>(R.id.sample3_button3)
        button3.setOnClickListener {
            TransitionManager.go(scene3, TransitionInflater.from(this@AnimationsActivity2).inflateTransition(R.transition.transition_slide_and_changebounds_sequential))
        }
        val button4 = findViewById<Button>(R.id.sample3_button4)
        button4.setOnClickListener {
            TransitionManager.go(scene4, TransitionInflater.from(this@AnimationsActivity2).inflateTransition(R.transition.transition_slide_and_changebounds_sequential_with_interpolators))
        }
        viewsToAnimate.add(button1)
        viewsToAnimate.add(button2)
        viewsToAnimate.add(button3)
        viewsToAnimate.add(button4)
    }

}