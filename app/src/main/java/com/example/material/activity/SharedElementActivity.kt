package com.example.material.activity

import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.Slide
import android.view.Gravity
import android.widget.TextView
import com.example.animation.R
import com.example.material.bean.SampleBean
import com.example.material.fragment.SharedElementFragment

class SharedElementActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sharedelement)
        val bundle = intent.extras
        //以下为判断bundle是否为空，以及bundle是否包含关键词“bundle”
        if (bundle != null && bundle.containsKey(EXTRA_SAMPLE)) {
            //如果传递有值，则获取赋值
            val sampleEntity = bundle.getParcelable<SampleBean>(EXTRA_SAMPLE)
            val title = findViewById<TextView>(R.id.title)
            title.text = sampleEntity?.name
            //初始化
            setUpWindowAnimations()
            setupLayout(sampleEntity)
            setupToolbar()
        } else {
            finish()
        }
    }

    private fun setUpWindowAnimations() {
        // We are not interested in defining a new Enter Transition. Instead we change default transition duration
        window.enterTransition.setDuration(500)
    }

    private fun setupLayout(sampleEntity: SampleBean?) {
        // Transition for fragment1
        val slideTransition = Slide(Gravity.START)
        slideTransition.setDuration(500)
        // Create fragment and define some of it transitions
        val sharedElementFragment = SharedElementFragment.newInstance(sampleEntity)
        sharedElementFragment.reenterTransition = slideTransition
        sharedElementFragment.exitTransition = slideTransition
        sharedElementFragment.sharedElementEnterTransition = ChangeBounds()
        supportFragmentManager.beginTransaction()
            .replace(R.id.sample2_content, sharedElementFragment)
            .commit()
    }

}