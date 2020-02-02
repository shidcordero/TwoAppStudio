package com.sample.testapplication.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.text.InputType
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.callbacks.onCancel
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.sample.testapplication.R
import com.sample.testapplication.model.DialogParams
import java.util.concurrent.Callable

object DialogUtils {

    private var loadingDialog: MaterialDialog? = null

    fun showLoadingDialog(context: Context) {
        if (loadingDialog != null)
            loadingDialog!!.dismiss()


        if (loadingDialog == null || loadingDialog!!.context !== context) {
            loadingDialog = MaterialDialog(context)
                .customView(viewRes = R.layout.tpl_progress, dialogWrapContent = true)
                .cancelable(true)
                .cancelOnTouchOutside(false)
                .onCancel{ (context as Activity).onBackPressed() }
        }

        if (loadingDialog != null && !loadingDialog!!.isShowing) {
            loadingDialog!!.show()
        }

    }

    fun dismissLoadingDialog() {
        if (loadingDialog != null && loadingDialog!!.isShowing) {
            loadingDialog!!.dismiss()
            loadingDialog = null
        }
    }

    fun showInternetError(context: Context?) {
        MaterialDialog(context!!)
            .show {
                title(R.string.cm_connection_error)
                message(R.string.cm_connection_error_desc)
                positiveButton(R.string.lm_ok)
            }
    }

    fun showMessage(
        context: Context,
        title: Any,
        message: Any,
        positiveLabel: Any,
        positiveCallback: Callable<Any>? = null,
        negativeLabel: Any? = null,
        negativeCallback: Callable<Any>? = null
    ) {
        val dialogBuilder = MaterialDialog(context)

        if (title is String)
            dialogBuilder.title(text = title)
        else
            dialogBuilder.title(text = context.getString(title as Int))

        if (message is String)
            dialogBuilder.message(text = message)
        else
            dialogBuilder.message(text = context.getString(message as Int))

        if (positiveLabel is String) {
            dialogBuilder.positiveButton(text = positiveLabel)
        } else {
            dialogBuilder.positiveButton(text = context.getString(positiveLabel as Int))
        }

        if (negativeLabel != null) {
            if (negativeLabel is String) {
                dialogBuilder.negativeButton(text = negativeLabel)
            } else {
                dialogBuilder.negativeButton(text = context.getString(negativeLabel as Int))
            }
        }

        if (positiveCallback != null){
            dialogBuilder.positiveButton {
                positiveCallback.call()
            }
        }
        if (negativeCallback != null) {
            dialogBuilder.negativeButton {
                negativeCallback.call()
            }
        }

        dialogBuilder.show()
    }


    @SuppressLint("CheckResult")
    fun showInputDialog(
        context: Context,
        title: Any? = null,
        positiveLabel: Any? = R.string.cm_submit,
        positiveCallback: CallableWithParams,
        hintLabel: Int? = null,
        onSubmit: Boolean = true,
        allowEmpty: Boolean = false,
        inputType: Int = InputType.TYPE_CLASS_TEXT,
        preFill: String? = null
    ){
        val dialogBuilder = MaterialDialog(context)
            .input(
                waitForPositiveButton = onSubmit,
                allowEmpty = allowEmpty,
                hintRes = hintLabel,
                inputType = inputType,
                prefill = preFill
            ){ dialog, text ->
                if (inputType == InputType.TYPE_TEXT_VARIATION_URI){
                    val inputField = dialog.getInputField()
                    val isValid = AndroidUtils.isUrlValid((inputField.text).toString())

                    inputField.error = if (isValid) null else context.getString(R.string.em_invalid_not_url)
                    dialog.setActionButtonEnabled(WhichButton.POSITIVE, isValid)

                    if (isValid && onSubmit) {
                        positiveCallback.params = DialogParams(text = inputField.text.toString(), dialog = dialog)
                        positiveCallback.call()
                    }
                } else {
                    if (!onSubmit)
                        positiveCallback.call()
                }
            }

        if (title is String)
            dialogBuilder.title(text = title)
        else
            dialogBuilder.title(text = context.getString(title as Int))

        if (positiveLabel is String) {
            dialogBuilder.positiveButton(text = positiveLabel)
        } else {
            dialogBuilder.positiveButton(text = context.getString(positiveLabel as Int))
        }

        dialogBuilder.negativeButton(res = R.string.lm_cancel) {
            dialog -> dialog.dismiss()
        }

        dialogBuilder.noAutoDismiss()

        dialogBuilder.show()
    }
}