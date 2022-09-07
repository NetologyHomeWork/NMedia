package ru.netology.nmedia.presentation.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import ru.netology.nmedia.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkGoogleApiAvailability()
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

    private companion object {
        private const val REQUEST_CODE_ERROR_DIALOG = 900
    }
}