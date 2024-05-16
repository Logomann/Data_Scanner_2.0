package com.logomann.datascanner20.ui.snackbar


import android.content.Context
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.logomann.datascanner20.R
import com.google.android.material.snackbar.Snackbar

class SnackbarMessage {
    companion object {
        fun showMessageError(cl: CoordinatorLayout, message: String, context: Context) {
            Snackbar.make(cl, message, Snackbar.LENGTH_LONG)
                .setTextColor(context.getColor(R.color.white))
                .setBackgroundTint(context.getColor(R.color.red))
                .setTextMaxLines(5)
                .show()
        }

        fun showMessageAction(cl: CoordinatorLayout, message: String, context: Context) {
            Snackbar.make(cl, message, Snackbar.LENGTH_INDEFINITE)
                .setActionTextColor(context.getColor(R.color.white))
                .setTextColor(context.getColor(R.color.white))
                .setBackgroundTint(context.getColor(R.color.green))
                .setAction(context.getString(R.string.ok)) {
                }
                .setTextMaxLines(5)
                .show()
        }

        fun showMessageOk(cl: CoordinatorLayout, message: String, context: Context) {
            Snackbar.make(cl, message, Snackbar.LENGTH_SHORT)
                .setTextColor(context.getColor(R.color.white))
                .setBackgroundTint(context.getColor(R.color.green))
                .setTextMaxLines(5)
                .show()


        }
    }
}




