package com.example.material.activity

import android.content.Intent
import android.os.Bundle
import android.transition.Fade
import android.transition.TransitionManager
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.example.animation.R
import com.example.material.bean.SampleBean
import com.example.material.utils.function.orFalse
import com.example.material.utils.function.orZero

class AnimationsActivity : BaseActivity() {
    private var savedWidth: Int? = null
    private var sizeChanged: Boolean? = null
    private var positionChanged: Boolean? = null
    private var sampleEntity: SampleBean? = null
    private var square: ImageView? = null
    private var viewRoot: ViewGroup? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animations)
        initSystemBar(true, true)
        val bundle = intent.extras
        // 以下为判断bundle是否为空，以及bundle是否包含关键词“bundle”
        if (bundle != null && bundle.containsKey(EXTRA_SAMPLE)) {
            // 如果传递有值，则获取赋值
            sampleEntity = bundle.getParcelable(EXTRA_SAMPLE)
            val squareGreen = findViewById<ImageView>(R.id.square_green)
            sampleEntity?.color?.let { DrawableCompat.setTint(squareGreen.drawable, it) }
            val title: TextView = findViewById(R.id.title)
            title.text = sampleEntity?.name
            title.setTextColor(ContextCompat.getColor(this, android.R.color.black))
            // 初始化
            setUpWindowAnimations()
            setupLayout()
            setupToolbar()
        } else {
            finish()
        }
    }

    private fun setUpWindowAnimations() {
        window.reenterTransition = Fade()
    }

    private fun setupLayout() {
        square = findViewById(R.id.square_green)
        viewRoot = findViewById(R.id.sample3_root)
        findViewById<Button>(R.id.sample3_button1).setOnClickListener {
            changeLayout()
        }
        findViewById<Button>(R.id.sample3_button2).setOnClickListener {
            changePosition()
        }
        findViewById<Button>(R.id.sample3_button3).setOnClickListener {
            val intent = Intent(this, AnimationsActivity2::class.java)
            transitionTo(intent)
        }
    }

    private fun changeLayout() {
        TransitionManager.beginDelayedTransition(viewRoot)
        val params = square?.layoutParams
        if (sizeChanged.orFalse) {
            params?.width = savedWidth.orZero
        } else {
            savedWidth = params?.width
            params?.width = 200
        }
        sizeChanged = !sizeChanged.orFalse
        square?.layoutParams = params
    }

    private fun changePosition() {
        TransitionManager.beginDelayedTransition(viewRoot)
        val lp = square?.layoutParams as? LinearLayout.LayoutParams
        if (positionChanged.orFalse) {
            lp?.gravity = Gravity.CENTER
        } else {
            lp?.gravity = Gravity.START
        }
        positionChanged = !positionChanged.orFalse
        square?.layoutParams = lp
    }

}