package com.pi.attestation.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider.getUriForFile
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.pi.attestation.R
import com.pi.attestation.objects.Certificate
import com.pi.attestation.tools.CertificatesGenerator
import com.pi.attestation.tools.CertificatesManager
import com.pi.attestation.tools.GeneratorListener
import com.pi.attestation.ui.profile.InfoManager
import com.pi.attestation.ui.tools.ViewModelFactory
import java.io.File


/**
 * [Fragment] displayed when we open the app (as "home" [Fragment]). This [Fragment] displays all
 * the previous certificates or a help box in case there is no certificate.
 */
class HomeFragment : Fragment(), ActionModeListener {

    private lateinit var homeViewModel : HomeViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentActivity = activity ?: return

        homeViewModel = ViewModelProvider(this,
            ViewModelFactory(fragmentActivity.filesDir))
            .get(HomeViewModel::class.java)

        val recyclerView: RecyclerView = view.findViewById(R.id.certificatesRV)
        val infoManager = InfoManager(fragmentActivity)
        val adapter = CertificatesAdapter(infoManager.hasBeenFilled(infoManager.retrieveUserInfo()),
            this)
        recyclerView.adapter = adapter
        enableSwipeToDelete(view, recyclerView)

        homeViewModel.certificates.observe(viewLifecycleOwner, Observer {
            adapter.setItems(it)
        })
    }

    /**
     * Enables the swipe to delete feature. With this feature enabled the user can delete a
     * [Certificate] just by swiping to the left. When deleting a [Certificate] a [Snackbar] will be
     * shown to provide a "undo" feature to the user in case it was accidental.
     * @param view [View] where to create the [Snackbar] displayed in case of [Certificate]
     * deletion.
     * @param recyclerView [RecyclerView] where the feature should be enabled.
     * @see SwipeToDeleteCallback
     */
    private fun enableSwipeToDelete(view: View, recyclerView: RecyclerView){
        val swipeToDeleteCallback: SwipeToDeleteCallback =
            object : SwipeToDeleteCallback(view.context) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
                    val position = viewHolder.adapterPosition - 1
                    if(position < 0) return
                    val certificate = homeViewModel.getCertificate(position) ?: return
                    val fragmentActivity = activity
                    if (fragmentActivity != null) {
                        val dirFile = fragmentActivity.filesDir
                        homeViewModel.removeItem(position)
                        CertificatesManager(dirFile).removeCertificate(certificate)
                        Snackbar.make(view, R.string.certificate_deleted, Snackbar.LENGTH_LONG)
                            .setAction(R.string.undo) {
                                homeViewModel.addItem(certificate, position)
                                recyclerView.scrollToPosition(position)
                                CertificatesManager(dirFile).addCertificate(certificate, position)
                            }.addCallback(object : Snackbar.Callback() {
                                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                    if (event != DISMISS_EVENT_ACTION) {
                                        CertificatesManager(dirFile).deletePdf(certificate,
                                            fragmentActivity.cacheDir)
                                    }
                                }
                            }).show()
                    }else Toast.makeText(view.context, R.string.unknown_error,
                        Toast.LENGTH_SHORT).show()
                }
            }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun startActionMode(actionModeCallback: ActionMode.Callback) : ActionMode? {
        return activity?.startActionMode(actionModeCallback)
    }

    override fun deleteAction(toBeRemovedList: ArrayList<Int>) {
        val adapterPositions = ArrayList(toBeRemovedList)
        val certificates = homeViewModel.getCertificates(toBeRemovedList)
        val fragmentActivity = activity
        if (fragmentActivity != null && certificates != null) {
            val dirFile = fragmentActivity.filesDir
            homeViewModel.removeItems(adapterPositions)
            CertificatesManager(dirFile).removeCertificates(certificates)

            val view = view
            if(view != null){
                Snackbar.make(view, if(toBeRemovedList.size == 1) R.string.certificate_deleted
                                    else R.string.certificates_deleted,
                    Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo) {
                        homeViewModel.addItems(certificates, adapterPositions)
                        CertificatesManager(dirFile).addCertificates(certificates, adapterPositions)
                    }.addCallback(object : Snackbar.Callback() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            if (event != DISMISS_EVENT_ACTION) {
                                CertificatesManager(dirFile).deletePdfFiles(certificates,
                                    fragmentActivity.cacheDir)
                            }
                        }
                    }).show()
            }
        }else Toast.makeText(fragmentActivity, R.string.unknown_error, Toast.LENGTH_SHORT)
            .show()
    }

    override fun shareAction(selectedItemsPositions: ArrayList<Int>) {
        val certificates = homeViewModel.getCertificates(selectedItemsPositions)
        val fragmentActivity = activity
        if (fragmentActivity != null && certificates != null) {

            var generated = true

            val pdfPaths = ArrayList<Uri>()
            for(certificate in certificates){
                val pdfFile = File(fragmentActivity.cacheDir, certificate.pdfFileName)
                if(!pdfFile.exists()) generated = false
                val pdfUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    getUriForFile(fragmentActivity, fragmentActivity.packageName, pdfFile)
                } else Uri.fromFile(pdfFile)
                pdfPaths.add(pdfUri)
            }

            val generatorListener = object : GeneratorListener {
                override fun onGenerated() {
                    when (pdfPaths.size) {
                        0 -> return
                        1 -> {
                            val share = Intent().apply {
                                action = Intent.ACTION_SEND
                                type = "application/pdf"
                                putExtra(Intent.EXTRA_STREAM, pdfPaths[0])
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            fragmentActivity.startActivity(share)
                        }
                        else -> {
                            val share = Intent().apply {
                                action = Intent.ACTION_SEND_MULTIPLE
                                type = "application/pdf"
                                putParcelableArrayListExtra(Intent.EXTRA_STREAM, pdfPaths)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            fragmentActivity.startActivity(share)
                        }
                    }
                }
            }

            if(generated) generatorListener.onGenerated()
            else CertificatesGenerator(fragmentActivity, certificates, generatorListener).execute()
        }else Toast.makeText(fragmentActivity, R.string.unknown_error, Toast.LENGTH_SHORT)
            .show()
    }
}
