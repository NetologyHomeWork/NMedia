package ru.netology.nmedia.data.utils

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import androidx.annotation.StringRes
import androidx.navigation.NavController
import ru.netology.nmedia.R
import ru.netology.nmedia.data.auth.AppAuth

fun authDialog(
    context: Context,
    navController: NavController,
    @StringRes message: Int
): Dialog {
    val listener = DialogInterface.OnClickListener { dialog, which ->
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                navController.navigate(R.id.action_sign_up_fragment)
                dialog.dismiss()
            }
            DialogInterface.BUTTON_NEGATIVE -> {
                navController.navigate(R.id.action_sign_in_fragment)
                dialog.dismiss()
            }
            DialogInterface.BUTTON_NEUTRAL -> {
                dialog.dismiss()
            }
        }
    }
    return AlertDialog.Builder(context)
        .setCancelable(true)
        .setIcon(R.mipmap.ic_launcher_round)
        .setTitle(R.string.nmedia)
        .setMessage(message)
        .setNegativeButton(R.string.sign_in, listener)
        .setPositiveButton(R.string.sign_up, listener)
        .setNeutralButton(R.string.cancel, listener)
        .create()
}

fun logoutDialog(context: Context, @StringRes message: Int, appAuth: AppAuth): Dialog {
    val listener = DialogInterface.OnClickListener { dialog, which ->
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                appAuth.removeAuth()
                dialog.dismiss()
            }
            DialogInterface.BUTTON_NEGATIVE -> {
                dialog.dismiss()
            }
        }
    }
    return AlertDialog.Builder(context)
        .setCancelable(true)
        .setIcon(R.mipmap.ic_launcher_round)
        .setTitle(R.string.nmedia)
        .setMessage(message)
        .setNegativeButton(R.string.cancel, listener)
        .setPositiveButton(R.string.confirm, listener)
        .create()
}