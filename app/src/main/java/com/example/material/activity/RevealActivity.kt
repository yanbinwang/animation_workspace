package com.example.material.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.transition.Fade
import android.transition.Transition
import android.transition.TransitionInflater
import android.transition.TransitionManager
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.example.animation.R
import com.example.material.bean.SampleBean
import com.example.material.utils.function.orZero
import kotlin.math.hypot
import kotlin.math.max

@SuppressLint("ClickableViewAccessibility", "SetTextI18n")
class RevealActivity : BaseActivity(), OnTouchListener {
    private var llRoot: LinearLayout? = null
    private var bgViewGroup: RelativeLayout? = null
    private var toolbar: Toolbar? = null
    private var interpolator: Interpolator? = null
    private var body: TextView? = null
    private var btnRed: View? = null

    companion object {
        private const val DELAY = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reveal)
        val bundle = intent.extras
        // 以下为判断bundle是否为空，以及bundle是否包含关键词“bundle”
        if (bundle != null && bundle.containsKey(EXTRA_SAMPLE)) {
            // 如果传递有值，则获取赋值
            val sampleEntity = bundle.getParcelable<SampleBean>(EXTRA_SAMPLE)
            val sharedTarget = findViewById<ImageView>(R.id.shared_target)
            sampleEntity?.color?.let {
                DrawableCompat.setTint(sharedTarget.drawable, it)
                val isLight = shouldUseWhiteSystemBarsForColor(it)
                initSystemBar(isLight, isLight)
            }
            val title = findViewById<TextView>(R.id.title)
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
        interpolator = AnimationUtils.loadInterpolator(this, android.R.interpolator.linear_out_slow_in)
        setupEnterAnimations()
        setupExitAnimations()
    }

    private fun setupEnterAnimations() {
        val transition = TransitionInflater.from(this).inflateTransition(R.transition.transition_changebounds_with_arcmotion)
        window.sharedElementEnterTransition = transition
        transition.addListener(object : Transition.TransitionListener {
            override fun onTransitionStart(transition: Transition) {
            }

            override fun onTransitionEnd(transition: Transition) {
                // Removing listener here is very important because shared element transition is executed again backwards on exit. If we don't remove the listener this code will be triggered again.
                transition.removeListener(this)
                hideTarget()
                animateRevealShow(toolbar)
                animateButtonsIn()
            }

            override fun onTransitionCancel(transition: Transition) {
            }

            override fun onTransitionPause(transition: Transition) {
            }

            override fun onTransitionResume(transition: Transition) {
            }
        })
    }

    private fun setupExitAnimations() {
        val returnTransition = Fade()
        window.returnTransition = returnTransition
        returnTransition.setDuration(500)
        returnTransition.setStartDelay(500)
        returnTransition.addListener(object : Transition.TransitionListener {
            override fun onTransitionStart(transition: Transition) {
                transition.removeListener(this)
                animateButtonsOut()
                animateRevealHide(bgViewGroup)
            }

            override fun onTransitionEnd(transition: Transition) {
            }

            override fun onTransitionCancel(transition: Transition) {
            }

            override fun onTransitionPause(transition: Transition) {
            }

            override fun onTransitionResume(transition: Transition) {
            }
        })
    }

    private fun setupLayout() {
        llRoot = findViewById(R.id.ll_root)
        bgViewGroup = findViewById(R.id.reveal_root)
        toolbar = findViewById(R.id.toolbar)
        body = findViewById(R.id.sample_body)
        val btnGreen = findViewById<ImageView>(R.id.square_green)
        btnGreen.setOnClickListener {
            revealGreen()
        }
        btnRed = findViewById(R.id.square_red)
        btnRed?.setOnClickListener {
            revealRed()
        }
        val btnBlue = findViewById<ImageView>(R.id.square_blue)
        btnBlue.setOnClickListener {
            revealBlue()
        }
        findViewById<ImageView>(R.id.square_yellow).setOnTouchListener(this)
    }

    private fun revealBlue() {
        animateButtonsOut()
        val anim = animateRevealColorFromCoordinates(bgViewGroup, R.color.sample_blue, bgViewGroup?.width.orZero / 2, 0)
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                animateButtonsIn()
            }
        })
        body?.text = "Circular Reveal Animation from top with nested animations on end"
        body?.setTextColor(ContextCompat.getColor(this, R.color.theme_blue_background))
        toolbar?.setBackgroundResource(R.color.sample_blue)
        llRoot?.setBackgroundResource(R.color.theme_blue_background)
    }

    private fun revealRed() {
        val originalParams = btnRed?.layoutParams
        val transition = TransitionInflater.from(this).inflateTransition(R.transition.transition_changebounds_with_arcmotion)
        transition.addListener(object : Transition.TransitionListener {
            override fun onTransitionStart(transition: Transition) {
            }

            override fun onTransitionEnd(transition: Transition) {
                animateRevealColor(bgViewGroup, R.color.sample_red)
                body?.text = "View layout change animation with Circular Reveal Animation on finish"
                body?.setTextColor(ContextCompat.getColor(this@RevealActivity, R.color.theme_red_background))
                btnRed?.layoutParams = originalParams
            }

            override fun onTransitionCancel(transition: Transition) {
            }

            override fun onTransitionPause(transition: Transition) {
            }

            override fun onTransitionResume(transition: Transition) {
            }
        })
        TransitionManager.beginDelayedTransition(bgViewGroup, transition)
        val layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
        btnRed?.layoutParams = layoutParams
        toolbar?.setBackgroundResource(R.color.sample_red)
        llRoot?.setBackgroundResource(R.color.theme_red_background)
    }

    private fun revealYellow(x: Float, y: Float) {
        animateRevealColorFromCoordinates(bgViewGroup, R.color.sample_yellow, x.toInt(), y.toInt())
        body?.text = "Circular Reveal Animation starting from the center of target view"
        body?.setTextColor(ContextCompat.getColor(this, R.color.theme_yellow_background))
        toolbar?.setBackgroundResource(R.color.sample_yellow)
        llRoot?.setBackgroundResource(R.color.theme_yellow_background)
    }

    private fun revealGreen() {
        animateRevealColor(bgViewGroup, R.color.sample_green)
        body?.text = "Circular Reveal Animation starting from touch coordinates"
        body?.setTextColor(ContextCompat.getColor(this, R.color.theme_green_background))
        toolbar?.setBackgroundResource(R.color.sample_green)
        llRoot?.setBackgroundResource(R.color.theme_green_background)
    }

    private fun hideTarget() {
        findViewById<ImageView>(R.id.shared_target).setVisibility(View.GONE)
    }

    private fun animateButtonsIn() {
        for (i in 0..<bgViewGroup?.childCount.orZero) {
            val child = bgViewGroup?.getChildAt(i)
            child?.animate()
                ?.setStartDelay((100 + i * DELAY).toLong())
                ?.setInterpolator(interpolator)
                ?.alpha(1f)
                ?.scaleX(1f)
                ?.scaleY(1f)
        }
    }

    private fun animateButtonsOut() {
        for (i in 0..<bgViewGroup?.childCount.orZero) {
            val child = bgViewGroup?.getChildAt(i)
            child?.animate()
                ?.setStartDelay(i.toLong())
                ?.setInterpolator(interpolator)
                ?.alpha(0f)
                ?.scaleX(0f)
                ?.scaleY(0f)
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            if (v?.id == R.id.square_yellow) {
                revealYellow(event.rawX, event.rawY)
            }
        }
        return false
    }

    private fun animateRevealShow(viewRoot: View?) {
        val cx = (viewRoot?.left.orZero + viewRoot?.right.orZero) / 2
        val cy = (viewRoot?.top.orZero + viewRoot?.bottom.orZero) / 2
        val finalRadius = max(viewRoot?.width?.toDouble().orZero, viewRoot?.height?.toDouble().orZero).toInt()
        val anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, 0f, finalRadius.toFloat())
        viewRoot?.visibility = View.VISIBLE
        anim.duration = 500
        anim.interpolator = AccelerateInterpolator()
        anim.start()
    }

    private fun animateRevealColor(viewRoot: ViewGroup?, @ColorRes color: Int) {
        val cx = (viewRoot?.left.orZero + viewRoot?.right.orZero) / 2
        val cy = (viewRoot?.top.orZero + viewRoot?.bottom.orZero) / 2
        animateRevealColorFromCoordinates(viewRoot, color, cx, cy)
    }

    private fun animateRevealColorFromCoordinates(viewRoot: ViewGroup?, @ColorRes color: Int, x: Int, y: Int): Animator {
        val finalRadius = hypot(viewRoot?.width?.toDouble().orZero, viewRoot?.height?.toDouble().orZero).toFloat()
        val anim = ViewAnimationUtils.createCircularReveal(viewRoot, x, y, 0f, finalRadius)
        viewRoot?.setBackgroundColor(ContextCompat.getColor(this, color))
        anim.duration = 500
        anim.interpolator = AccelerateDecelerateInterpolator()
        anim.start()
        return anim
    }

    private fun animateRevealHide(viewRoot: View?) {
        val cx = (viewRoot?.left.orZero + viewRoot?.right.orZero) / 2
        val cy = (viewRoot?.top.orZero + viewRoot?.bottom.orZero) / 2
        val initialRadius = viewRoot?.width.orZero
        val anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, initialRadius.toFloat(), 0f)
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                viewRoot?.visibility = View.INVISIBLE
            }
        })
        anim.duration = 500
        anim.start()
    }

}