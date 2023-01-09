package ru.netology.nmedia.presentation.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.presentation.fragments.PhotoFragment

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding ?: throw NullPointerException("ActivityMainBinding is null")

    private val destinationListener =
        NavController.OnDestinationChangedListener { _, destination, _ ->
            supportActionBar?.title =
                if (destination.label.isNullOrBlank()) "" else destination.label
            supportActionBar?.setDisplayHomeAsUpEnabled(!isStartDestination(destination))
        }

    private val fragmentListener = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewCreated(
            fm: FragmentManager,
            f: Fragment,
            v: View,
            savedInstanceState: Bundle?
        ) {
            super.onFragmentViewCreated(fm, f, v, savedInstanceState)
            if (f is PhotoFragment) {
                binding.toolbar.setBackgroundColor(
                    ContextCompat.getColor(
                        this@MainActivity,
                        R.color.black
                    )
                )
            } else {
                binding.toolbar.setBackgroundColor(
                    ContextCompat.getColor(
                        this@MainActivity,
                        R.color.purple_700
                    )
                )
            }
            onNavControllerActivated(f.findNavController())
        }
    }

    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }


        checkGoogleApiAvailability()

        setSupportActionBar(binding.toolbar)

        val navController = getRootNavController()
        onNavControllerActivated(navController)

        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentListener, true)
    }

    override fun onDestroy() {
        _binding = null
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentListener)
        super.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController!!.navigateUp()
    }

    private fun checkGoogleApiAvailability() {
        with(GoogleApiAvailability.getInstance()) {
            val code = isGooglePlayServicesAvailable(this@MainActivity)
            if (code == ConnectionResult.SUCCESS) return@with
            if (isUserResolvableError(code)) {
                getErrorDialog(
                    this@MainActivity,
                    code,
                    REQUEST_CODE_ERROR_DIALOG
                )?.show()
                return
            }
            Toast.makeText(
                this@MainActivity,
                getString(R.string.google_api_unavailable),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun isStartDestination(destination: NavDestination?): Boolean {
        if (destination == null) return false
        val graph = destination.parent ?: return false
        return destination.id == graph.startDestinationId
    }

    private fun onNavControllerActivated(navController: NavController) {
        if (this.navController == navController) return
        this.navController?.removeOnDestinationChangedListener(destinationListener)
        navController.addOnDestinationChangedListener(destinationListener)
        this.navController = navController
    }

    private fun getRootNavController(): NavController {
        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        return navHost.navController
    }

    private companion object {
        private const val REQUEST_CODE_ERROR_DIALOG = 900
    }
}