package com.pi.fcertif.ui.tools

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pi.fcertif.ui.documents.DocumentsManager
import java.lang.reflect.InvocationTargetException

/**
 * Factory used to create [ViewModel] with specific constructors.
 */
class ViewModelDocumentsFactory(private val documentsManager: DocumentsManager) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (ViewModel::class.java.isAssignableFrom(modelClass)) {
            try {
                modelClass.getConstructor(DocumentsManager::class.java).newInstance(documentsManager)
            } catch (e: NoSuchMethodException) {
                throw RuntimeException("Cannot create an instance of $modelClass", e)
            } catch (e: IllegalAccessException) {
                throw RuntimeException("Cannot create an instance of $modelClass", e)
            } catch (e: InstantiationException) {
                throw RuntimeException("Cannot create an instance of $modelClass", e)
            } catch (e: InvocationTargetException) {
                throw RuntimeException("Cannot create an instance of $modelClass", e)
            }
        } else super.create(modelClass)
    }
}