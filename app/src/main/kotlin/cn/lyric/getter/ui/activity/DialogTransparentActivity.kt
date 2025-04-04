package cn.lyric.getter.ui.activity

import android.content.Context
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import cn.lyric.getter.R
import cn.lyric.getter.tool.ConfigTools.config
import cn.lyric.getter.ui.dialog.EditTextDialog

class DialogTransparentActivity : AppCompatActivity() {

    private fun openRegexReplaceDialog(context: Context) {
        val title = context.getString(R.string.regex_replace)
        val a = EditTextDialog(context)
            .setTitle(title)
            .setText(config.regexReplace)
            .show {
                config.regexReplace = it
            }
        a.setOnDismissListener {
            finish()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        finish()
        return true
    }

    override fun onStart() {
        super.onStart()
        openRegexReplaceDialog(this)

    }
}