package com.example.material.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityOptionsCompat
import com.example.animation.R
import com.example.material.utils.TransitionHelper
import com.example.material.utils.TransitionHelper.toAndroidXPairs

@SuppressWarnings("unchecked")
abstract class BaseActivity : AppCompatActivity() {
    protected val EXTRA_SAMPLE = "sample"
    protected val EXTRA_TYPE = "type"
    protected val TYPE_PROGRAMMATICALLY = 0
    protected val TYPE_XML = 1

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

}