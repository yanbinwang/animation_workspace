package com.example.material.utils

import android.app.Activity
import android.view.View

object TransitionHelper {

    fun createSafeTransitionParticipants(activity: Activity, includeStatusBar: Boolean, vararg otherParticipants: Pair<View, String>): Array<Pair<View, String>> {
        // Avoid system UI glitches as described here:
        // https://plus.google.com/+AlexLockwood/posts/RPtwZ5nNebb
        val decor = activity.window.decorView
        var statusBar: View? = null
        if (includeStatusBar) {
            statusBar = decor.findViewById(android.R.id.statusBarBackground)
        }
        val navBar = decor.findViewById<View>(android.R.id.navigationBarBackground)
        // Create pair of transition participants.
        val participants = ArrayList<Pair<View,String>>(3)
        addNonNullViewToTransitionParticipants(statusBar, participants)
        addNonNullViewToTransitionParticipants(navBar, participants)
        // only add transition participants if there's at least one none-null element
        if (otherParticipants != null && !(otherParticipants.size == 1 && otherParticipants[0] == null)) {
            participants.addAll(listOf(*otherParticipants))
        }
        return participants.toTypedArray<Pair<View, String>>()
    }

    private fun addNonNullViewToTransitionParticipants(view: View?, participants: ArrayList<Pair<View, String>>) {
        if (view == null) {
            return
        }
        participants.add(Pair(view, view.transitionName))
    }

    /**
     * 将 Kotlin Pair 数组转换为 AndroidX Pair 数组
     */
    fun <A, B> Array<Pair<A, B>>.toAndroidXPairs(): Array<androidx.core.util.Pair<A, B>> {
        return map { (first, second) ->
            androidx.core.util.Pair(first, second)
        }.toTypedArray()
    }

}