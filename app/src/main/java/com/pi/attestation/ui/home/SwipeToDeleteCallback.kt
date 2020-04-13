package com.pi.attestation.ui.home

import android.content.Context
import android.graphics.*
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.pi.attestation.R


abstract class SwipeToDeleteCallback internal constructor(val context: Context) :
    ItemTouchHelper.Callback() {

    private val clearPaint = Paint()
    private val background = ContextCompat.getDrawable(context, R.drawable.delete_background)
    private val deleteDrawable = ContextCompat.getDrawable(context, R.drawable.delete_bin_96px)
    private val intrinsicWidth: Int
    private val intrinsicHeight: Int

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder)
            : Int {
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

    private fun clearCanvas(c: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
        c.drawRect(left, top, right, bottom, clearPaint)
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.7f
    }

    init {
        clearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        intrinsicWidth = deleteDrawable!!.intrinsicWidth
        intrinsicHeight = deleteDrawable.intrinsicHeight
    }
}
