package rosf73.study.chat.util

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts

class OpenDocumentContract : ActivityResultContracts.OpenDocument() {

    override fun createIntent(context: Context, input: Array<String>): Intent {
        val intent = super.createIntent(context, input)
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        return intent
    }
}