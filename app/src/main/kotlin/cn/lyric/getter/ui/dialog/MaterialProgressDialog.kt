package cn.lyric.getter.ui.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import cn.lyric.getter.databinding.DialogProgressBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MaterialProgressDialog(context: Context) {
    private val layout by lazy { DialogProgressBinding.inflate(LayoutInflater.from(context)) }
    private val dialogBuilder: MaterialAlertDialogBuilder =
        MaterialAlertDialogBuilder(context).apply {
            setView(layout.root)
            setCancelable(false)
        }
    private lateinit var dialog: Dialog

    fun setMessage(message: String): MaterialProgressDialog {
        layout.message.text = message
        return this
    }

    fun setTitle(title: String): MaterialProgressDialog {
        dialogBuilder.setTitle(title)
        return this
    }

    fun show() {
        dialog = dialogBuilder.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }
}
