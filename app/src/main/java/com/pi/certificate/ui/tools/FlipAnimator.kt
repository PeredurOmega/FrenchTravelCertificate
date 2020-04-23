package com.pi.certificate.ui.tools

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.view.View
import com.pi.certificate.R

/**
 * Helper class used to animate a flipping effect on two views.
 */
class FlipAnimator {

    private var leftIn: AnimatorSet? = null
    private var rightOut:AnimatorSet? = null
    private var leftOut:AnimatorSet? = null
    private var rightIn:AnimatorSet? = null

    /**
     * Performs flip animation on two views.
     * @param back [View] at the back.
     * @param front [View] at the front.
     * @param showFront [Boolean] True if we want to show the front, false otherwise.
     */
    fun flipView(back: View, front: View, showFront: Boolean) {
        val context = back.context
        leftIn =
            AnimatorInflater.loadAnimator(context, R.animator.card_flip_left_in) as AnimatorSet
        rightOut =
            AnimatorInflater.loadAnimator(context, R.animator.card_flip_right_out) as AnimatorSet
        leftOut =
            AnimatorInflater.loadAnimator(context, R.animator.card_flip_left_out) as AnimatorSet
        rightIn =
            AnimatorInflater.loadAnimator(context, R.animator.card_flip_right_in) as AnimatorSet
        val showFrontAnim = AnimatorSet()
        val showBackAnim = AnimatorSet()
        leftIn!!.setTarget(back)
        rightOut!!.setTarget(front)
        showFrontAnim.playTogether(leftIn, rightOut)
        leftOut!!.setTarget(back)
        rightIn!!.setTarget(front)
        showBackAnim.playTogether(rightIn, leftOut)
        if (showFront) {
            showFrontAnim.start()
        } else {
            showBackAnim.start()
        }
    }
}