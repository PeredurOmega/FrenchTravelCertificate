package com.pi.attestation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.pi.attestation.ui.creator.CertificateCreatorActivity
import com.pi.attestation.ui.profile.InfoManager
import com.pi.attestation.ui.tools.Leaver
import com.pi.attestation.ui.tools.SaverFragment

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        appBarConfiguration = AppBarConfiguration(setOf(R.id.nav_home, R.id.nav_profile,
            R.id.nav_eula, R.id.nav_settings, R.id.nav_contribute), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            val infoManager = InfoManager(this)
            if(infoManager.hasBeenFilled(infoManager.retrieveUserInfo())){
                startActivity(Intent(this, CertificateCreatorActivity::class.java))
            }else{
                Snackbar.make(view, R.string.no_profile_info, Snackbar.LENGTH_LONG)
                    .setAction(R.string.fill_profile) {
                        navController.navigate(R.id.nav_profile)
                    }.show()
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            run {
                if ((destination.label)?.equals(getString(R.string.menu_profile)) == true) hideFab()
                else showFab()
            }
        }

        checkForUpdate(this)
    }

    private fun checkForUpdate(activity: Activity){
        Thread(Runnable {
            val appUpdateManager = AppUpdateManagerFactory.create(activity)
            val appUpdateInfo = appUpdateManager.appUpdateInfo
            appUpdateInfo.addOnSuccessListener {
                if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && it.clientVersionStalenessDays() != null
                    && it.clientVersionStalenessDays() >= 1
                    && it.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    activity.runOnUiThread {
                        appUpdateManager.startUpdateFlowForResult(it, AppUpdateType.IMMEDIATE,
                            activity, 123)
                    }
                }
            }
        }).start()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun hideFab() {
        findViewById<FloatingActionButton?>(R.id.fab)?.hide()
    }

    private fun showFab() {
        findViewById<FloatingActionButton?>(R.id.fab)?.show()
    }

    override fun onBackPressed() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        if (navHostFragment != null) {
            val fragmentList = navHostFragment.childFragmentManager.fragments
            if (fragmentList.size > 0 && fragmentList[0] is SaverFragment) {
                if ((fragmentList[0] as SaverFragment).shouldNotExit(object : Leaver {
                        override fun onQuit() {
                            onBackPressed()
                        }
                    })) return
            }
        }
        super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_settings -> {
                val navController = findNavController(R.id.nav_host_fragment)
                navController.navigate(R.id.nav_settings)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
