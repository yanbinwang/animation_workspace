package com.example.material.activity

import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.animation.R
import com.example.material.adapter.SampleAdapter
import com.example.material.bean.SampleBean

/**
 * 安卓5.0+版本新增动画,minSdkVersion>=21
 * 如果用户不对toolbar做屏蔽
 * 在mainfest中的label属性就是toolbar的标题
 */
class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initSystemBar(true, true)
        setUpWindowAnimations()
        initView()
    }

    /**
     * 设置当前activity的进入动画（左侧进入，半秒完成）
     */
    private fun setUpWindowAnimations() {
        val slideTransition = Slide()
        slideTransition.slideEdge = Gravity.START
        slideTransition.setDuration(500)
        window.reenterTransition = slideTransition
        window.exitTransition = slideTransition
    }

    /**
     * 初始化当前activity的控件以及基础数据等(页面跳转做在适配器内)
     */
    private fun initView() {
        val sampleList = listOf(
            SampleBean(ContextCompat.getColor(this, R.color.sample_red), "Transitions 动画"),
            SampleBean(ContextCompat.getColor(this, R.color.sample_blue), "Shared Elements 动画"),
            SampleBean(ContextCompat.getColor(this, R.color.sample_green), "View animations 动画"),
            SampleBean(ContextCompat.getColor(this, R.color.sample_yellow), "Circular Reveal 动画"))
        val mainRecyclerView = findViewById<RecyclerView>(R.id.mainRecyclerView)
        mainRecyclerView.setHasFixedSize(true)
        mainRecyclerView.setLayoutManager(LinearLayoutManager(this))
        val sampleAdapter = SampleAdapter(this, sampleList)
        mainRecyclerView.setAdapter(sampleAdapter)
    }

}