package com.shahin.redesign.extensions

import android.animation.ValueAnimator
import android.view.View
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.hide() {
    visibility = View.INVISIBLE
}

fun View.setVisibleOrGone(visible: Boolean) {
    if (visible) visible() else gone()
}

fun View.setVisibleOrHide(visible: Boolean) {
    if (visible) visible() else hide()
}

fun View.setRootLayoutBottomMargin(bottomMargin: Int) {
    val params = layoutParams as FrameLayout.LayoutParams
    params.bottomMargin = bottomMargin
    layoutParams = params
}
