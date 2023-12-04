package com.dicoding.habitapp.utils

import android.content.Context
import com.dicoding.habitapp.R

fun setPriorityImage(context: Context, priorityLevel: String): Int {
    return when (priorityLevel) {
        context.getString(R.string.high) -> R.drawable.ic_priority_high
        context.getString(R.string.medium) -> R.drawable.ic_priority_medium
        else -> R.drawable.ic_priority_low
    }
}