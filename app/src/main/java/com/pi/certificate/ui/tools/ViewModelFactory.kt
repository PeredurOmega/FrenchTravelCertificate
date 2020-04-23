package com.pi.certificate.ui.tools

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.io.File
import java.lang.reflect.InvocationTargetException

/**
 * Factory used to create [ViewModel] with specific constructors.
 */
class ViewModelFactory(private val cacheFile: File): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (ViewModel::class.java.isAssignableFrom(modelClass)){
                try {
                    modelClass.getConstructor(File::class.java).newInstance(cacheFile)
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