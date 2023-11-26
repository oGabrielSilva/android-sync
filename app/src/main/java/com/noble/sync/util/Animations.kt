package com.noble.sync.util

import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation

class Animations {
    companion object {
        fun getShake(time: Long, toX: Float, cycles: Float): TranslateAnimation {
            val shake = TranslateAnimation(0f, toX, 0f, 0f)
            shake.duration = time
            shake.interpolator = CycleInterpolator(cycles)
            return shake
        }
    }
}