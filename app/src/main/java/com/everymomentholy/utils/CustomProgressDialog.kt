package com.everymomentholy.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.everymomentholy.R
import com.everymomentholy.ui.activity.LoginActivity

class CustomProgressDialog{

    lateinit var dialog: CustomDialog

    fun show(context: Context): Dialog {
        return show(context, null)
    }

    fun show(context: Context, title: CharSequence?): Dialog {
        val inflater = (context as Activity).layoutInflater
        val view = inflater.inflate(R.layout.custom_progressdialog, null)


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT_WATCH) {
            dialog = CustomDialog(context)
        }
        dialog.setContentView(view)
        dialog.show()
        return dialog
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT_WATCH)
    class CustomDialog(context: Context) : Dialog(context, R.style.CustomDialogTheme) {
        init {
            // Set Semi-Transparent Color for Dialog Background
            window?.decorView?.rootView?.setBackgroundResource(R.color.dialogBackground)
            window?.decorView?.setOnApplyWindowInsetsListener { _, insets ->
                insets.consumeSystemWindowInsets()
            }
        }
    }

    /*lateinit var activity: Activity
    lateinit var dialog: AlertDialog

    fun LoadingDialog(myActivity: Activity) {
        activity = myActivity
    }

    fun startLoadingDialog() {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        builder.setView(inflater.inflate(R.layout.custom_progressdialog, null))
        builder.setCancelable(false)
        dialog = builder.create()
        dialog.show()
    }

    fun diamissDialog() {
        dialog.dismiss()
    }*/

}