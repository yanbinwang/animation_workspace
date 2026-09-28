package com.example.material.utils

import android.app.Activity
import android.view.View

object TransitionHelper {

    /**
     * 创建安全的共享元素过渡参与者数组
     * 规避系统 UI 过渡 bug，参考：https://plus.google.com/+AlexLockwood/posts/RPtwZ5nNebb
     * 共享元素默认会把 DecorView 里的状态栏、导航栏背景一起纳入过渡，不加处理会闪屏
     * @param activity 当前页面Activity
     * @param includeStatusBar 是否把状态栏加入共享元素动画
     * @param otherParticipants 其他自定义共享元素，可变参数：View 和 transitionName 的配对
     * @return 共享元素 Pair 数组，传给 ActivityOptions.makeSceneTransitionAnimation
     */
    fun createSafeTransitionParticipants(activity: Activity, includeStatusBar: Boolean = false, vararg otherParticipants: Pair<View, String>): Array<Pair<View, String>> {
        // 获取 Activity 根 DecorView（包含状态栏、导航栏+页面内容）
        val decor = activity.window.decorView
        var statusBar: View? = null
        if (includeStatusBar) {
            // 找到系统状态栏背景 View，android.R.id.statusBarBackground 是系统内置id
            statusBar = decor.findViewById(android.R.id.statusBarBackground)
        }
        // 找到系统导航栏（底部虚拟按键栏）背景 View
        val navBar = decor.findViewById<View>(android.R.id.navigationBarBackground)
        // 创建共享元素参与者列表，预分配容量3：状态栏、导航栏 + 自定义元素
        val participants = ArrayList<Pair<View,String>>(3)
        // 把状态栏 View 添加进列表（内部会判断view不为null才add）
        addNonNullViewToTransitionParticipants(statusBar, participants)
        // 把导航栏 View 添加进列表（同样非空判断）
        addNonNullViewToTransitionParticipants(navBar, participants)
//        // 如果传入了额外的共享元素，并且不是【只有一个null元素】这种无效情况
//        if (otherParticipants != null && !(otherParticipants.size == 1 && otherParticipants[0] == null)) {
            // 将外部传入的共享元素全部追加到参与者列表
            participants.addAll(listOf(*otherParticipants))
//        }
        // ArrayList转数组，作为最终共享元素数组返回
        return participants.toTypedArray<Pair<View, String>>()
    }

    /**
     * 如果view不为空，就把View和它的transitionName组成Pair，添加进共享参与者列表
     * @param view 待加入的View（状态栏/导航栏/自定义共享View）
     * @param participants 共享元素列表
     */
    private fun addNonNullViewToTransitionParticipants(view: View?, participants: ArrayList<Pair<View, String>>) {
        // View为空直接return，不添加
        if (view == null) {
            return
        }
        // 组装Kotlin Pair：View + view自身的transitionName，添加到列表
        participants.add(Pair(view, view.transitionName))
    }

    /**
     * 将 Kotlin Pair 数组 转换为 AndroidX 的 Pair 数组
     * 因为老版本 ActivityOptions.makeSceneTransitionAnimation 需要 androidx.core.util.Pair
     */
    fun <A, B> Array<Pair<A, B>>.toAndroidXPairs(): Array<androidx.core.util.Pair<A, B>> {
        // 遍历每一个Kotlin Pair，转成androidx.core.util.Pair，再转成数组返回
        return map { (first, second) ->
            androidx.core.util.Pair(first, second)
        }.toTypedArray()
    }

}