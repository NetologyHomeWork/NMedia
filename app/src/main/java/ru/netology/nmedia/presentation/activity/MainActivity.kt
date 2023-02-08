package ru.netology.nmedia.presentation.activity

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.netology.nmedia.R
import ru.netology.nmedia.data.utils.logoutDialog
import ru.netology.nmedia.data.utils.observeStateFlow
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.presentation.fragments.PhotoFragment
import ru.netology.nmedia.presentation.viewmodel.AuthViewModel

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding ?: throw NullPointerException("ActivityMainBinding is null")

    private val authViewModel by viewModel<AuthViewModel>()

    var menuProvider: MenuProvider? = null

    private val destinationListener =
        NavController.OnDestinationChangedListener { _, destination, _ ->
            supportActionBar?.title =
                if (destination.label.isNullOrBlank()) "" else destination.label
            if (destination.id == R.id.postListFragment || destination.id == R.id.postDetailFragment) {
                provideAuthMenu()
            } else {
                menuProvider?.let { removeMenuProvider(it) }
            }
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

    val isAuth get() = authViewModel.authorized

    private val permissionPostNotificationLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
        ::requestPermission
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionPostNotificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

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

    private fun requestPermission(granted: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (granted) return
            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
       }
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

    private fun provideAuthMenu() {
        authViewModel.state.observeStateFlow(this) {
            menuProvider?.let(::removeMenuProvider)
            addMenuProvider(object : MenuProvider {

                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.auth_menu, menu)
                    menu.setGroupVisible(R.id.authorized, isAuth)
                    menu.setGroupVisible(R.id.unauthorized, isAuth.not())
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return when (menuItem.itemId) {
                        R.id.logout -> {
                            logoutDialog(this@MainActivity, R.string.confirm_logout).show()
                            true
                        }
                        R.id.sigh_in -> {
                            findNavController(R.id.fragment_container)
                                .navigate(R.id.action_sign_in_fragment)
                            true
                        }
                        R.id.sigh_up -> {
                            findNavController(R.id.fragment_container)
                                .navigate(R.id.action_sign_up_fragment)
                            true
                        }
                        else -> false
                    }
                }
            }.apply {
                menuProvider = this
            }, this@MainActivity, Lifecycle.State.RESUMED)

        }
    }

    private companion object {
        private const val REQUEST_CODE_ERROR_DIALOG = 900
    }
}