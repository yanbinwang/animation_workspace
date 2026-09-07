package com.example.material.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SampleBean(
    var color: Int? = null, // 颜色
    var name: String? = null // 名称
) : Parcelable