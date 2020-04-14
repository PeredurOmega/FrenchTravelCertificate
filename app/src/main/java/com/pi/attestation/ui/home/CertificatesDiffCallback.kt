package com.pi.attestation.ui.home

import androidx.recyclerview.widget.DiffUtil
import com.pi.attestation.objects.Certificate

/**
 * Utility class used to detect the changes made and animate them.
 * @see DiffUtil.Callback
 */
class CertificatesDiffCallback(private val oldList: List<Certificate>,
                               private val newList: List<Certificate>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size + 1
    }

    override fun getNewListSize(): Int {
        return newList.size + 1
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        if(oldItemPosition == 0 || newItemPosition == 0) return (oldItemPosition == newItemPosition)
        return oldList[oldItemPosition - 1] == newList[newItemPosition - 1]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        if(oldItemPosition == 0 || newItemPosition == 0){
            return (oldItemPosition == newItemPosition && oldListSize == newListSize)
        }
        return oldList[oldItemPosition - 1] == newList[newItemPosition - 1]
    }
}