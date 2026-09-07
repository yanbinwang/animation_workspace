package com.example.material.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import androidx.activity.enableEdgeToEdge
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityOptionsCompat
import androidx.core.graphics.ColorUtils.calculateLuminance
import com.example.animation.R
import com.example.material.utils.TransitionHelper
import com.example.material.utils.TransitionHelper.toAndroidXPairs

@SuppressWarnings("unchecked")
abstract class BaseActivity : AppCompatActivity() {

    companion object {
        protected const val EXTRA_SAMPLE = "sample"
        protected const val EXTRA_TYPE = "type"
        protected const val TYPE_PROGRAMMATICALLY = 0
        protected const val TYPE_XML = 1

        /**
         * 根据颜色值(@ColorInt)的亮度判断是否需要使用白色系统状态栏/导航栏图标
         */
        fun shouldUseWhiteSystemBarsForColor(@ColorInt backgroundColor: Int): Boolean {
            // 使用系统API获取相对亮度（0.0-1.0之间）
            val luminance = calculateLuminance(backgroundColor)
            // 亮度阈值，低于0.5认为是暗色背景，需要白色图标
            return luminance < 0.5
        }

        /**
         * 状态栏图标亮/暗
         */
        fun Window.setStatusBarLightMode(isLight: Boolean, force: Boolean = false) {
            // 先判断当前模式是否已符合，符合则直接返回
            val currentIsLight = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                insetsController?.systemBarsAppearance?.and(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS) != 0
            } else {
                (decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) != 0
            }
            // 相同模式且不强制，直接跳过（避免重复触发重绘）
            if (!force && currentIsLight == isLight) return
            // 开始执行状态栏图标切换
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Android 11+（API 30+）推荐使用 InsetsController
                insetsController?.apply {
                    if (isLight) {
                        // 浅色模式（黑色图标）
                        setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
                    } else {
                        // 深色模式（白色图标）
                        setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
                    }
                }
            } else {
                // Android 6.0+（API 23-29）使用系统UI标志
                decorView.systemUiVisibility = if (isLight) {
                    // 浅色模式（黑色图标）：添加 LIGHT_STATUS_BAR 标志
                    decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    // 深色模式（白色图标）：移除 LIGHT_STATUS_BAR 标志
                    decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                }
            }
        }

        /**
         * 导航栏图标亮/暗
         */
        fun Window.setNavigationBarLightMode(isLight: Boolean, force: Boolean = false) {
            // 先判断当前模式是否已符合，符合则直接返回
            val currentIsLight = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                insetsController?.systemBarsAppearance?.and(WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS) != 0
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                (decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR) != 0
            } else {
                // 低版本不支持，直接返回
                false
            }
            // 相同模式且不强制，直接跳过（避免重复触发重绘）
            if (!force && currentIsLight == isLight) return
            // 低版本固定白色
            val mIsLight = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) isLight else false
            // 开始执行导航栏图标切换
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Android 11+ 推荐接口
                insetsController?.apply {
                    if (mIsLight) {
                        // 浅色模式（黑色图标）
                        setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS, WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS)
                    } else {
                        // 深色模式（白色图标）
                        setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS)
                    }
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10 兼容接口
                decorView.systemUiVisibility = if (mIsLight) {
                    decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                } else {
                    decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    protected fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    protected fun transitionTo(i: Intent?) {
        val pairs = TransitionHelper.createSafeTransitionParticipants(this, true)
        val transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this,  *pairs.toAndroidXPairs())
        startActivity(i, transitionActivityOptions.toBundle())
    }

    protected fun initSystemBar(statusBarDark: Boolean, navigationBarDark: Boolean) {
        window?.apply {
            setStatusBarLightMode(statusBarDark)
            setNavigationBarLightMode(navigationBarDark)
        }
    }

}