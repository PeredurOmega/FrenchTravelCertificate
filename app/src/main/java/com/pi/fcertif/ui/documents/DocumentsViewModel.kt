package com.pi.fcertif.ui.documents

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pi.fcertif.objects.Document
import java.lang.IndexOutOfBoundsException
import java.util.*
import kotlin.collections.ArrayList

/**
 * [ViewModel] used to provide content for the [DocumentsFragment] (retains data even in case of
 * screen rotation).
 */
class DocumentsViewModel(private val documentsManager: DocumentsManager) : ViewModel() {

    /**
     * [MutableLiveData] with an [ArrayList] of all the currently available [Document].
     */
    private val _documents = MutableLiveData<ArrayList<Document>>().apply {
        val documents = documentsManager.retrieveDocuments()
        value = documents
    }

    /**
     * Returns [DocumentsViewModel#_documents]. To be used by an observer.
     */
    val documents: LiveData<ArrayList<Document>> = _documents

    /**
     * Adds a [Document] to the [DocumentsViewModel#_documents]. Any observer will be notified of
     * this change.
     * @param document [Document] to add.
     * @param position [Int] Position where to add the provided [Document] in the [ArrayList].
     */
    fun addItem(document: Document, position: Int) {
        val documents = _documents.value
        if (documents != null) {
            documents.add(position, document)
            _documents.postValue(documents)
        } else _documents.postValue(ArrayList(Collections.singleton(document)))
        documentsManager.saveDocument(document)
    }

    /**
     * Removes a [Document] to the [DocumentsViewModel#_documents]. Any observer will be notified of
     * this change.
     * @param position [Int] Position of the [Document] to remove.
     */
    fun removeItem(position: Int) {
        val documents = _documents.value
        if (documents != null) {
            documentsManager.removeDocument(documents[position])
            documents.removeAt(position)
            _documents.postValue(documents)
        }
    }

    /**
     * Returns a [Document] according its position [Int].
     * @param position [Int] Position of the [Document] to retrieve.
     * @return [Document] at the given position.
     */
    fun getDocument(position: Int): Document? {
        return _documents.value?.get(position)
    }

    /**
     * Returns an [ArrayList] of [Document] according their positions in the adapter.
     * @param adapterPositions [ArrayList] containing each position in the adapter of the elements
     * of the [ArrayList] of [Document] to retrieve.
     * @return [ArrayList] of [Document] of the elements found at the given positions.
     */
    fun getProfiles(adapterPositions: ArrayList<Int>): ArrayList<Document>? {
        val documents = _documents.value ?: return null
        val documentsToRemove = ArrayList<Document>()
        try {
            for (adapterPosition in adapterPositions) {
                documentsToRemove.add(documents[adapterPosition - 1])
            }
        } catch (e: IndexOutOfBoundsException) {
            return null
        }
        return documentsToRemove
    }

    /**
     * Removes an [ArrayList] of [Document] to the [DocumentsVieModel#_documents]. Any observer
     * will be notified of this change.
     * @param adapterPositions [Document] of [Int] containing positions in the adapter of each
     * [Document] to remove.
     */
    fun removeItems(adapterPositions: ArrayList<Int>) {
        val documents = _documents.value ?: return
        var corrector = 1
        for (adapterPosition in adapterPositions) {
            documentsManager.removeDocument(documents[adapterPosition - corrector])
            documents.removeAt(adapterPosition - corrector)
            corrector++
        }
        _documents.postValue(documents)
    }

    /**
     * Adds an [ArrayList] of [Document] to the [DocumentsViewModel#_profiles]. Any observer will
     * be notified of this change.
     * @param documentsToAdd [ArrayList] of [Document] to add.
     * @param adapterPositions [ArrayList] of [Int] containing the positions where to add each
     * provided [Document] in the [ArrayList].
     */
    fun addItems(documentsToAdd: ArrayList<Document>, adapterPositions: ArrayList<Int>) {
        val documents = _documents.value
        if (documents != null && documentsToAdd.size == adapterPositions.size) {
            for (i in 0 until documentsToAdd.size) {
                documents.add(adapterPositions[i] - 1, documentsToAdd[i])
                documentsManager.saveDocument(documentsToAdd[i])
            }
            _documents.postValue(documents)
        } else {
            for (i in 0 until documentsToAdd.size) documentsManager.saveDocument(documentsToAdd[i])
            _documents.postValue(ArrayList(documentsToAdd))
        }
    }
}