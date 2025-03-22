package cn.lyric.getter.ui.dialog

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import cn.lyric.getter.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class EditTextDialogHelper(private val context: Context) {
    private val dialogView: View = LayoutInflater.from(context).inflate(R.layout.dialog_edit_text, null)
    private val editText: EditText = dialogView.findViewById(R.id.edit_text_input)
    private var hint: String = ""

    // 设置Hint文本
    fun setHint(hint: String): EditTextDialogHelper {
        this.hint = hint
        return this
    }

    // 设置默认文本
    fun setText(text: String): EditTextDialogHelper {
        editText.setText(text)
        editText.setSelection(text.length)
        return this
    }

    // 显示弹窗
    fun show(onConfirm: (String) -> Unit): AlertDialog {
        val dialog = MaterialAlertDialogBuilder(context)
            .setView(dialogView)
            .setTitle(hint)
            .setPositiveButton(R.string.ok) { _, _ ->
                onConfirm(editText.text.toString())
            }
            .setNegativeButton(R.string.cancel, null)
            .create() // 创建dialog，但不立即显示

        dialog.window?.apply {
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            setGravity(Gravity.TOP)
        }

        dialog.setOnShowListener {
            editText.requestFocus()
            dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
        return dialog
    }
}
