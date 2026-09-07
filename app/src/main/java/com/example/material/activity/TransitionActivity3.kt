package com.example.material.activity

import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionInflater
import android.transition.Visibility
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.example.animation.R
import com.example.material.bean.SampleBean

class TransitionActivity3 : BaseActivity() {
    private var type: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transition3)
        initSystemBar(true, true)
        val bundle = intent.extras
        // 以下为判断bundle是否为空，以及bundle是否包含关键词“bundle”
        if (bundle != null && bundle.containsKey(EXTRA_SAMPLE)) {
            // 如果传递有值，则获取赋值
            val sampleEntity = bundle.getParcelable<SampleBean>(EXTRA_SAMPLE)
            type = intent.extras?.getInt(EXTRA_TYPE)
            val title = findViewById<TextView>(R.id.title)
            title.text = sampleEntity?.name
            title.setTextColor(ContextCompat.getColor(this, android.R.color.black))
            val squareRed: ImageView = findViewById(R.id.square_red)
            sampleEntity?.color?.let {
                DrawableCompat.setTint(squareRed.drawable, it)
            }
            // 初始化
            setUpWindowAnimations()
            setupLayout()
            setupToolbar()
        } else {
            finish()
        }
    }

    // 进入动画
    private fun setUpWindowAnimations() {
        val transition = if (type == TYPE_PROGRAMMATICALLY) {
            buildEnterTransition()
        } else {
            TransitionInflater.from(this).inflateTransition(R.transition.transition_slide_from_bottom)
        }
        window.enterTransition = transition
    }

    private fun setupLayout() {
        findViewById<Button>(R.id.exit_button).setOnClickListener {
            finishAfterTransition()
        }
    }

    private fun buildEnterTransition(): Visibility {
        val enterTransition = Slide()
        enterTransition.setDuration(500)
        enterTransition.slideEdge = Gravity.END
        return enterTransition
    }

}