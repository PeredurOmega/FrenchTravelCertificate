package com.pi.fcertif.ui.documents

import android.content.res.Resources
import android.view.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.pi.fcertif.R
import com.pi.fcertif.objects.Document
import com.pi.fcertif.ui.home.ItemClickListener
import com.pi.fcertif.ui.home.SelectedListener
import kotlin.collections.ArrayList

/**
 * [RecyclerView.Adapter] used to populate the [RecyclerView] containing custom documents.
 * @param actionModeListener [ActionModeListener] used to interact with the [ActionMode].
 */
class DocumentsAdapter(private val actionModeListener: ActionModeListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), ItemClickListener {

    companion object {

        /**
         * Key for a [RecyclerView.ViewHolder] which only contains an "import custom document"
         * button.
         */
        const val IMPORT_DOCUMENT_CARD_TYPE = 17
    }

    /**
     * [ArrayList] of the documents used to populate the bound [RecyclerView].
     */
    private val documents = ArrayList<Document>()

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
            inflater.inflate(R.menu.profiles_action_mode, menu)
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
        position: Int,
        selectedListener: SelectedListener,
        resources: Resources
    ): Boolean {
        if (actionMode == null) return false
        onLongClick(position, selectedListener, resources)
        return true
    }

    override fun onLongClick(
        position: Int,
        selectedListener: SelectedListener,
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
            IMPORT_DOCUMENT_CARD_TYPE -> {
                ImportCustomDocumentViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.basic_create_card, parent, false)
                )
            }
            else -> DocumentViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.profile_card, parent, false),
                this
            )
        }
    }

    override fun getItemCount(): Int {
        return documents.size + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == 0) return
        else (holder as DocumentViewHolder).bindToDocument(
            documents[position - 1],
            position,
            selectedItemList.contains(position)
        )
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) IMPORT_DOCUMENT_CARD_TYPE else 0
    }

    /**
     * Displays the provided documents into the [RecyclerView].
     * @param documents Full list of the [Document] to display.
     */
    fun setItems(documents: ArrayList<Document>?) {
        actionMode?.finish()
        val diffResult = DiffUtil.calculateDiff(
            DocumentsDiffCallback(this.documents, documents ?: ArrayList())
        )
        this.documents.clear()
        this.documents.addAll(documents ?: ArrayList())
        diffResult.dispatchUpdatesTo(this)
    }
}