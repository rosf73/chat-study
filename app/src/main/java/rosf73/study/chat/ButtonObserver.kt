package rosf73.study.chat

import android.text.Editable
import android.text.TextWatcher
import android.widget.ImageView

class ButtonObserver(
    private val button: ImageView
) : TextWatcher {

    override fun beforeTextChanged(charSequence: CharSequence, start: Int, count: Int, end: Int) {}
    override fun onTextChanged(charSequence: CharSequence, start: Int, count: Int, end: Int) {
        if (charSequence.toString().trim().isNotEmpty()) {
            button.isEnabled = true
            button.setImageResource(R.drawable.ic_send)
        } else {
            button.isEnabled = false
            button.setImageResource(R.drawable.ic_send_gray)
        }
    }
    override fun afterTextChanged(editable: Editable?) {}
}