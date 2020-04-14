package com.pi.attestation.ui.tools

import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

/**
 * [LinearLayoutManager] which enables smooth scrolling to a specific position.
 */
@Suppress("unused")
class TopLayoutManager : LinearLayoutManager {

    constructor(context: Context) : super(context)

    constructor(context: Context, orientation: Int, reverseLayout: Boolean) :
            super(context, orientation, reverseLayout)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) :
            super(context, attrs, defStyleAttr, defStyleRes)

    override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State,
                                        position: Int) {
        val topSmoothScroller = TopSmoothScroller(recyclerView.context)
        topSmoothScroller.targetPosition = position
        startSmoothScroll(topSmoothScroller)
    }

    /**
     * [LinearSmoothScroller] which is a bit slower than usual for a better rendering.
     * Usually the speed is calculated with 25f/densityDpi. Here we use 150f/densityDpi.
     */
    private class TopSmoothScroller(context: Context) : LinearSmoothScroller(context) {

        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
            return 150f / displayMetrics.densityDpi
        }
    }
}