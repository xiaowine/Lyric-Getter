package cn.lyric.getter.ui.dialog


import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import cn.lyric.getter.R
import cn.lyric.getter.databinding.DialogProgressBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class MdProgressDialog(context: Context) {
    private val layout by lazy { DialogProgressBinding.inflate(LayoutInflater.from(context)) }
    private val dialogBuilder: MaterialAlertDialogBuilder
    private lateinit var dialog: Dialog

    init {
        dialogBuilder = MaterialAlertDialogBuilder(context).apply {
            setView(layout.root)
            setCancelable(false)
        }
    }

    fun setMessage(message: String): MdProgressDialog {
        layout.message.text = message
        return this
    }

    fun setTitle(title: String): MdProgressDialog {
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
