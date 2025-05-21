package com.example.material.activity

import android.content.Intent
import android.os.Bundle
import android.transition.Fade
import android.transition.Slide
import android.transition.Visibility
import android.widget.Button
import android.widget.TextView
import com.example.animation.R
import com.example.material.bean.SampleBean

class TransitionActivity : BaseActivity() {
    private var sampleEntity: SampleBean? = null
    private var intent: Intent? = null
    private var bundle: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transition)
        val bundle = getIntent().extras
        //以下为判断bundle是否为空，以及bundle是否包含关键词“bundle”
        if (bundle != null && bundle.containsKey(EXTRA_SAMPLE)) {
            //如果传递有值，则获取赋值
            sampleEntity = bundle.getParcelable(EXTRA_SAMPLE)
            val title = findViewById<TextView>(R.id.title)
            title.text = sampleEntity?.name
            //初始化
            setUpWindowAnimations()
            setupLayout()
            setupToolbar()
        } else {
            finish()
        }
    }

    //进入动画
    private fun setUpWindowAnimations() {
        val enterTransition = Fade()
        enterTransition.setDuration(500)
        // This view will not be affected by enter transition animation
        enterTransition.excludeTarget(R.id.square_red, true)
        window.enterTransition = enterTransition
    }

    private fun setupLayout() {
        findViewById<Button>(R.id.sample1_button1).setOnClickListener {
            intent = Intent(this, TransitionActivity2::class.java)
            bundle = Bundle()
            bundle?.putParcelable(EXTRA_SAMPLE, sampleEntity)
            intent?.putExtras(bundle ?: return@setOnClickListener)
            intent?.putExtra(EXTRA_TYPE, TYPE_PROGRAMMATICALLY)
            transitionTo(intent)
        }

        findViewById<Button>(R.id.sample1_button2).setOnClickListener {
            intent = Intent(this, TransitionActivity2::class.java)
            bundle = Bundle()
            bundle?.putParcelable(EXTRA_SAMPLE, sampleEntity)
            intent?.putExtras(bundle ?: return@setOnClickListener)
            intent?.putExtra(EXTRA_TYPE, TYPE_XML)
            transitionTo(intent)
        }

        findViewById<Button>(R.id.sample1_button3).setOnClickListener {
            intent = Intent(this, TransitionActivity3::class.java)
            bundle = Bundle()
            bundle?.putParcelable(EXTRA_SAMPLE, sampleEntity)
            intent?.putExtras(bundle ?: return@setOnClickListener)
            intent?.putExtra(EXTRA_TYPE, TYPE_PROGRAMMATICALLY)
            transitionTo(intent)
        }

        findViewById<Button>(R.id.sample1_button4).setOnClickListener {
            intent = Intent(this, TransitionActivity3::class.java)
            bundle = Bundle()
            bundle?.putParcelable(EXTRA_SAMPLE, sampleEntity)
            intent?.putExtras(bundle ?: return@setOnClickListener)
            intent?.putExtra(EXTRA_TYPE, TYPE_XML)
            transitionTo(intent)
        }

        findViewById<Button>(R.id.sample1_button5).setOnClickListener {
            val returnTransition = buildReturnTransition()
            window.returnTransition = returnTransition
            finishAfterTransition()
        }
        findViewById<Button>(R.id.sample1_button6).setOnClickListener {
            finishAfterTransition()
        }
    }

    private fun buildReturnTransition(): Visibility {
        val enterTransition: Visibility = Slide()
        enterTransition.setDuration(500)
        return enterTransition
    }

}