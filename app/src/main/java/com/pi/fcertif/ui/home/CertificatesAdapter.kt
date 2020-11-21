package com.pi.fcertif.ui.home

import android.content.res.Resources
import android.view.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.pi.fcertif.R
import com.pi.fcertif.objects.Certificate
import kotlin.collections.ArrayList

/**
 * [RecyclerView.Adapter] used to populate the [RecyclerView] containing certificates.
 * @param profileFilled [Boolean] whether or not the profile is properly filled. According to this,
 * different info will be displayed.
 * @param actionModeListener [ActionModeListener] used to interact with the [ActionMode].
 */
class CertificatesAdapter(
    private val profileFilled: Boolean,
    private val actionModeListener: ActionModeListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ItemClickListener {

    companion object {

        /**
         * Key for a [RecyclerView.ViewHolder] which contains explanations and a "fill profile"
         * button.
         */
        const val FILL_PROFILE_CARD_TYPE = 12

        /**
         * Key for a [RecyclerView.ViewHolder] which contains explanations and a "create
         * certificate" button.
         */
        const val CREATE_CERTIFICATE_WHEN_NONE_CARD_TYPE = 13

        /**
         * Key for a [RecyclerView.ViewHolder] which only contains a "create certificate" button.
         */
        const val CREATE_CERTIFICATE_CARD_TYPE = 14
    }

    /**
     * [ArrayList] of the certificates used to populate the bound [RecyclerView].
     */
    private val certificates = ArrayList<Certificate>()

    /**
     * [ActionMode] currently in use. Null means there is currently no [ActionMode].
     */
    private var actionMode: ActionMode? = null

    /**
     * [ArrayList] of [Int] positions the selected items in this adapter .
     */
    private val selectedItemList = ArrayList<Int>()

    /**
     * [ActionMode.Callback] in use when selecting an item in this adapter.
     */
    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            val inflater: MenuInflater = mode.menuInflater
            inflater.inflate(R.menu.certificates_action_mode, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.action_delete -> {
                    selectedItemList.sort()
                    actionModeListener.deleteAction(selectedItemList)
                    mode.finish()
                    true
                }
                R.id.action_share -> {
                    selectedItemList.sort()
                    actionModeListener.shareAction(selectedItemList)
                    mode.finish()
                    true
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            actionMode = null
            val changedPositions = ArrayList(selectedItemList)
            selectedItemList.clear()
            for (position in changedPositions) {
                notifyItemChanged(position)
            }
        }
    }

    override fun selectedForActionMode(
        position: Int, selectedListener: SelectedListener,
        resources: Resources
    ): Boolean {
        if (actionMode == null) return false
        onLongClick(position, selectedListener, resources)
        return true
    }

    override fun onLongClick(
        position: Int, selectedListener: SelectedListener,
        resources: Resources
    ) {
        if (selectedItemList.contains(position)) {
            selectedItemList.remove(position)
            selectedListener.unselected(true)
        } else {
            selectedItemList.add(position)
            selectedListener.selected(true)
        }

        val currentSize = selectedItemList.size
        if (currentSize > 0 && actionMode == null) {
            actionMode = actionModeListener.startActionMode(actionModeCallback)
        } else if (currentSize == 0 && actionMode != null) {
            actionMode!!.finish()
        }
        if (actionMode != null) {
            actionMode!!.title = resources.getQuantityString(
                R.plurals.selected_count, currentSize,
                currentSize
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            FILL_PROFILE_CARD_TYPE -> {
                FillProfileCardViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.fill_profile_card, parent, false)
                )
            }
            CREATE_CERTIFICATE_WHEN_NONE_CARD_TYPE -> {
                CreateWhenNoneCardViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.create_when_none_card, parent, false)
                )
            }
            CREATE_CERTIFICATE_CARD_TYPE -> {
                BasicCreateCardViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.basic_create_card, parent, false)
                )
            }
            else -> {
                CertificateViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.certificate_card, parent, false), this
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return certificates.size + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == 0) return
        else (holder as CertificateViewHolder).bindToCertificate(
            certificates[position - 1],
            position, selectedItemList.contains(position)
        )
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            if (profileFilled) {
                if (itemCount == 1) CREATE_CERTIFICATE_WHEN_NONE_CARD_TYPE
                else CREATE_CERTIFICATE_CARD_TYPE
            } else FILL_PROFILE_CARD_TYPE
        } else 0
    }

    /**
     * Displays the provided certificates into the [RecyclerView].
     * @param certificates Full list of the [Certificate] to display.
     */
    fun setItems(certificates: ArrayList<Certificate>?) {
        actionMode?.finish()
        val diffResult = DiffUtil.calculateDiff(
            CertificatesDiffCallback(
                this.certificates,
                certificates ?: ArrayList()
            )
        )
        this.certificates.clear()
        this.certificates.addAll(certificates ?: ArrayList())
        diffResult.dispatchUpdatesTo(this)
    }
}