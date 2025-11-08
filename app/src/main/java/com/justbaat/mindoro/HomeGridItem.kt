package com.justbaat.mindoro

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes

data class HomeGridItem(
    val title: String,
    val subtitle: String,
    @DrawableRes val iconResId: Int,
    @ColorRes val backgroundColorResId: Int,
    @IdRes val actionId: Int,
    val tag: String? = null
)