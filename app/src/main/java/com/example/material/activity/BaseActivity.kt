package com.example.material.activity

import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityOptionsCompat
import androidx.core.graphics.ColorUtils.calculateLuminance
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.animation.R
import com.example.material.utils.TransitionHelper
import com.example.material.utils.TransitionHelper.toAndroidXPairs
import com.example.material.utils.function.doOnceAfterLayout
import com.example.material.utils.function.padding
import com.example.material.utils.function.size

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
        toolbar.doOnceAfterLayout { tb ->
            // 取当前页面状态栏高度
            val statusBarHeight = getStatusBarHeight()
            // 设置高度
            tb.size(height = tb.measuredHeight + statusBarHeight)
            // 设置左、右内边距全为0
            tb.padding(top = statusBarHeight, start = 0, end = 0)
            // 取出系统按钮
            val systemNavBtn = getNavButtonView(tb)
            // 去除水波纹
            systemNavBtn?.background = null
            // 去除长按文字
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                systemNavBtn?.tooltipText = null
            }
            systemNavBtn?.contentDescription = null
            systemNavBtn?.setOnLongClickListener { _ -> true }
        }
    }

    /**
     * 反射获取 Toolbar 中的私有字段 mNavButtonView（返回按钮）
     */
    private fun getNavButtonView(toolbar: Toolbar?): ImageButton? {
        try {
            // 获取 Toolbar 类中的 mNavButtonView 字段
            val field = Toolbar::class.java.getDeclaredField("mNavButtonView")
            // 设置字段可访问（私有字段需要开启）
            field.isAccessible = true
            // 获取字段值（即返回按钮的 ImageButton 实例）
            return field.get(toolbar) as? ImageButton
        } catch (e: Exception) {
            // 转换异常
            e.printStackTrace()
        }
        return null
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

    /**
     * 获取顶栏高度(静态默认值)
     * 设备出厂时定义的固定值（如大多数手机为 24dp~32dp），写死在系统资源文件中；
     * 不考虑当前窗口的状态（如是否全屏、是否隐藏状态栏、是否启用边缘到边缘模式）；
     * 不包含刘海屏（display cutout）等额外区域的高度（部分高版本手机可能优化，但本质仍是静态值）
     */
    protected fun getStatusBarHeight(): Int {
        val baseStatusBarHeight = getInternalDimensionSize("status_bar_height")
        val insets = ViewCompat.getRootWindowInsets(window.decorView)
        return insets?.getInsets(WindowInsetsCompat.Type.statusBars())?.top ?: baseStatusBarHeight
    }

    /**
     * 获取底栏高度(静态默认值)
     */
    protected fun getNavigationBarHeight(): Int {
        val baseNavigationBarHeight = getInternalDimensionSize("navigation_bar_height")
        val currentActivity = this
        val insets = ViewCompat.getRootWindowInsets(currentActivity.window.decorView)
        return insets?.getInsets(WindowInsetsCompat.Type.navigationBars())?.bottom ?: baseNavigationBarHeight
    }

    /**
     * 拿系统内置 dimen 尺寸 (直接取系统层配置的像素值作为保底措施)
     * "status_bar_height" → 状态栏高度
     * "navigation_bar_height" → 竖屏导航栏高度
     * "navigation_bar_height_landscape" → 横屏导航栏高度
     */
    private fun getInternalDimensionSize(key: String): Int {
        val resourceId = Resources.getSystem().getIdentifier(key, "dimen", "android")
        if (resourceId <= 0) return 0
        return try {
            val systemSize = Resources.getSystem().getDimensionPixelSize(resourceId)
            val appSize = resources.getDimensionPixelSize(resourceId)
            // 优先取较大值；若系统值更小，则按密度比补偿后四舍五入
            if (systemSize >= appSize) {
                systemSize
            } else {
                val densityCompensatedSize = appSize * Resources.getSystem().displayMetrics.density / resources.displayMetrics.density
                // 刻意保留 ±0.5f 手写取整，不使用 roundToInt() 因为 roundToInt() 在 -0.5f 时结果为 0，而原版逻辑结果为 -1，必须保持逐 bit 一致以避免兼容性问题
                (if (densityCompensatedSize >= 0) {
                    densityCompensatedSize + 0.5f
                } else {
                    densityCompensatedSize - 0.5f
                }).toInt()
            }
        } catch (_: Resources.NotFoundException) {
            0
        }
    }




}