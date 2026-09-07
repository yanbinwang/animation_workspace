package com.example.material.activity

import android.os.Bundle
import android.transition.Explode
import android.transition.Transition
import android.transition.TransitionInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import com.example.animation.R
import com.example.material.bean.SampleBean

class TransitionActivity2 : BaseActivity() {
    private var type: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transition2)
        initSystemBar(true, true)
        val bundle = intent.extras
        // 以下为判断bundle是否为空，以及bundle是否包含关键词“bundle”
        if (bundle != null && bundle.containsKey(EXTRA_SAMPLE)) {
            // 如果传递有值，则获取赋值
            val sampleEntity = bundle.getParcelable<SampleBean>(EXTRA_SAMPLE)
            type = intent.extras?.getInt(EXTRA_TYPE)
            val title = findViewById<TextView>(R.id.title)
            title.text = sampleEntity?.name
            val squareRed = findViewById<ImageView>(R.id.square_red)
            sampleEntity?.color?.let { DrawableCompat.setTint(squareRed.drawable, it) }
            //初始化
            setUpWindowAnimations()
            setupLayout()
            setupToolbar()
        } else {
            finish()
        }
    }

    private fun setupLayout() {
        findViewById<Button>(R.id.exit_button).setOnClickListener {
            finishAfterTransition()
        }
    }

    // 进入动画
    private fun setUpWindowAnimations() {
        val transition = if (type == TYPE_PROGRAMMATICALLY) {
            buildEnterTransition()
        } else {
            TransitionInflater.from(this).inflateTransition(R.transition.transition_explode)
        }
        window.enterTransition = transition
    }

    private fun buildEnterTransition(): Transition {
        val enterTransition = Explode()
        enterTransition.setDuration(500)
        return enterTransition
    }

}