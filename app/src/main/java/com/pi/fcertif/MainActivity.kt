package com.pi.fcertif

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
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
import com.pi.fcertif.ui.creator.CertificateCreatorActivity
import com.pi.fcertif.ui.profile.InfoManager
import com.pi.fcertif.ui.tools.Leaver
import com.pi.fcertif.ui.tools.SaverFragment

/**
 * Main [AppCompatActivity] oF the App. This is the first [AppCompatActivity] to be launched.
 */
class MainActivity : AppCompatActivity() {

    /**
     * @see AppBarConfiguration
     */
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme_NoActionBar)
        setContentView(R.layout.activity_main)

        setUpNavigation()

        checkForUpdate(this)
    }

    /**
     * Sets up the the navigation.
     */
    private fun setUpNavigation() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navController = setUpNavController()
        setUpFab(navController)
    }

    /**
     * Sets up the [NavController] and binding it with the [Toolbar] and the [DrawerLayout]. In case
     * of changed destination the [FloatingActionButton] will be hidden if the destination is the
     * ProfileFragment and will be shown for other destinations.
     * @return [NavController] fully configured.
     */
    private fun setUpNavController(): NavController {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_profile,
                R.id.nav_eula, R.id.nav_settings, R.id.nav_contribute
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            run {
                if ((destination.label)?.equals(getString(R.string.menu_profile)) == true) hideFab()
                else showFab()
            }
        }
        return navController
    }

    /**
     * Sets up the [FloatingActionButton]. When the user clicks on the [FloatingActionButton] we
     * check that his profile is complete. If it is we launch the [CertificateCreatorActivity]. If
     * it isn't we prompt the user with a [Snackbar] containing a "Navigate to profile" action.
     * @param navController [NavController] to use to navigate to the
     * [com.pi.fcertif.ui.profile.ProfileFragment] in case of click on the
     * [FloatingActionButton] while the profile is incomplete.
     */
    private fun setUpFab(navController: NavController) {
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            val infoManager = InfoManager(this)
            if (infoManager.hasBeenFilled(infoManager.retrieveUserInfo())) {
                startActivity(Intent(this, CertificateCreatorActivity::class.java))
            } else {
                Snackbar.make(view, R.string.no_profile_info, Snackbar.LENGTH_LONG)
                    .setAction(R.string.fill_profile) {
                        navController.navigate(R.id.nav_profile)
                    }.show()
            }
        }
    }

    /**
     * Checks if there is any update available. If any the user will be prompted to update the app
     * (if he has not been prompted to update since at least one day). This method is useful because
     * of the change brought regularly by the Ministry of Home Affairs.
     * @param activity [Activity] to use.
     */
    private fun checkForUpdate(activity: Activity) {
        Thread {
            val appUpdateManager = AppUpdateManagerFactory.create(activity)
            val appUpdateInfo = appUpdateManager.appUpdateInfo
            appUpdateInfo.addOnSuccessListener {
                if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && it.clientVersionStalenessDays() != null
                    && it.clientVersionStalenessDays() >= 1
                    && it.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
                ) {
                    activity.runOnUiThread {
                        appUpdateManager.startUpdateFlowForResult(
                            it, AppUpdateType.IMMEDIATE,
                            activity, 123
                        )
                    }
                }
            }
        }.start()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    /**
     * Hides the [FloatingActionButton] of this [Activity].
     */
    private fun hideFab() {
        findViewById<FloatingActionButton?>(R.id.fab)?.hide()
    }

    /**
     * Shows the [FloatingActionButton] of this [Activity].
     */
    private fun showFab() {
        findViewById<FloatingActionButton?>(R.id.fab)?.show()
    }

    /**
     * This method overrides [AppCompatActivity.onBackPressed] to intercept back clicks. When a back
     * click is intercepted this method checks if the displayed fragment inherits [SaverFragment].
     * If the fragment inherits [SaverFragment] this [SaverFragment] will be notified that the user
     * clicked on the back button, if it doesn't the method will call its super method
     * [AppCompatActivity.onBackPressed]. When the [SaverFragment] is notified it should prompt the
     * user for asking to save or discard any change (if any else the back click will behave as
     * usual).
     */
    override fun onBackPressed() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        if (navHostFragment != null) {
            val fragmentList = navHostFragment.childFragmentManager.fragments
            if (fragmentList.size > 0 && fragmentList[0] is SaverFragment) {
                if ((fragmentList[0] as SaverFragment).shouldNotExit(object : Leaver {
                        override fun onQuit() {
                            backPress()
                            setUpNavigation()
                        }
                    })) return
            }
        }
        backPress()
    }

    /**
     * Has the same effect as when the user press the back button.
     */
    private fun backPress() {
        super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                val navController = findNavController(R.id.nav_host_fragment)
                navController.navigate(R.id.nav_settings)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
