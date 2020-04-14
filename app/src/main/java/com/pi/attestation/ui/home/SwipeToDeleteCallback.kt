package com.pi.attestation.ui.home

import android.content.Context
import android.graphics.*
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.pi.attestation.R
import com.pi.attestation.ui.home.CertificatesAdapter.Companion.CREATE_CERTIFICATE_CARD_TYPE
import com.pi.attestation.ui.home.CertificatesAdapter.Companion.CREATE_CERTIFICATE_WHEN_NONE_CARD_TYPE
import com.pi.attestation.ui.home.CertificatesAdapter.Companion.FILL_PROFILE_CARD_TYPE

/**
 * [ItemTouchHelper.Callback] that provides a "swipe to delete" feature.
 * @param context [Context] used to retrieve resources : [SwipeToDeleteCallback#background] and
 * [SwipeToDeleteCallback#deleteDrawable]
 */
abstract class SwipeToDeleteCallback internal constructor(context: Context) :
    ItemTouchHelper.Callback() {

    /**
     * [Paint] holding the style and color information about how to draw geometries, text and bitmaps.
     */
    private val clearPaint = Paint()

    /**
     * Background to draw when swiping to delete. This drawable should have exactly the size of the
     * item which is being deleted and will be drawn at the exact same place of the original position
     * of the item view which is being deleted.
     */
    private val background = ContextCompat.getDrawable(context, R.drawable.delete_background)

    /**
     * Drawable to draw when swiping to delete. This drawable will be drawn at the center right of
     * the original item position.
     */
    private val deleteDrawable = ContextCompat.getDrawable(context, R.drawable.delete_bin_96px)

    /**
     * Original width of the [SwipeToDeleteCallback#deleteDrawable].
     */
    private val intrinsicWidth: Int

    /**
     * Original height of the [SwipeToDeleteCallback#deleteDrawable].
     */
    private val intrinsicHeight: Int

    init {
        clearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        intrinsicWidth = deleteDrawable!!.intrinsicWidth
        intrinsicHeight = deleteDrawable.intrinsicHeight
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder)
            : Int {
        if(viewHolder.itemViewType == FILL_PROFILE_CARD_TYPE
            || viewHolder.itemViewType == CREATE_CERTIFICATE_WHEN_NONE_CARD_TYPE
            || viewHolder.itemViewType == CREATE_CERTIFICATE_CARD_TYPE) return 0
        return makeMovementFlags(0, ItemTouchHelper.LEFT)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                        viewHolder1: RecyclerView.ViewHolder): Boolean {
        return false
    }


    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                             dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val itemView: View = viewHolder.itemView
        val itemHeight: Int = itemView.height
        val isCancelled = dX == 0f && !isCurrentlyActive
        if (isCancelled) {
            clearCanvas(c, itemView.right + dX, itemView.top.toFloat(),
                itemView.right.toFloat(), itemView.bottom.toFloat())
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }
        background?.setBounds(itemView.left, itemView.top, itemView.right,
            itemView.bottom)
        background?.draw(c)

        val drawableHeight = (itemHeight.toDouble() / 2).toInt()
        val drawableWidth = (intrinsicWidth * (itemHeight / intrinsicHeight.toDouble()) / 2).toInt()

        val deleteIconTop: Int = itemView.top + (itemHeight - drawableHeight) / 2
        val deleteIconMargin = (itemHeight - drawableHeight) / 2
        val deleteIconLeft: Int = itemView.right - deleteIconMargin - drawableWidth
        val deleteIconRight: Int = itemView.right - deleteIconMargin
        val deleteIconBottom = deleteIconTop + drawableHeight
        deleteDrawable?.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight,
            deleteIconBottom)
        deleteDrawable?.draw(c)
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    /**
     * Clears the [Canvas].
     * @param c [Canvas] to clear.
     * @param left [Float] The left side of the rectangle to clear.
     * @param top [Float] The top side of the rectangle to clear.
     * @param right [Float] The right side of the rectangle to clear.
     * @param bottom [Float] The bottom side of the rectangle to clear.
     */
    private fun clearCanvas(c: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
        c.drawRect(left, top, right, bottom, clearPaint)
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.7f
    }
}
