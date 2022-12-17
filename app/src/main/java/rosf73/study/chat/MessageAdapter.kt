package rosf73.study.chat

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import rosf73.study.chat.MainActivity.Companion.ANONYMOUS
import rosf73.study.chat.data.Message
import rosf73.study.chat.databinding.ItemImageMessageBinding
import rosf73.study.chat.databinding.ItemMessageBinding

class MessageAdapter(
    private val options: FirebaseRecyclerOptions<Message>,
    private val currentUserName: String?
) : FirebaseRecyclerAdapter<Message, ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_TEXT) {
            val view = inflater.inflate(R.layout.item_message, parent, false)
            val binding = ItemMessageBinding.bind(view)
            MessageViewHolder(binding)
        } else {
            val view = inflater.inflate(R.layout.item_image_message, parent, false)
            val binding = ItemImageMessageBinding.bind(view)
            ImageMessageViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Message) {
        if (options.snapshots[position].text != null) {
            (holder as MessageViewHolder).bind(model)
        } else {
            (holder as ImageMessageViewHolder).bind(model)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (options.snapshots[position].text != null) VIEW_TYPE_TEXT else VIEW_TYPE_IMAGE
    }

    inner class MessageViewHolder(
        private val binding: ItemMessageBinding
    ) : ViewHolder(binding.root) {
        fun bind(item: Message) {
            binding.tvMessage.text = item.text
            setTextColor(item.name, binding.tvMessage)

            binding.tvMessenger.text = item.name ?: ANONYMOUS
            if (item.photoUrl != null) {
                loadImageIntoView(binding.ivMessenger, item.photoUrl)
            } else {
                binding.ivMessenger.setImageResource(R.drawable.ic_account_circle)
            }
        }

        private fun setTextColor(userName: String?, textView: TextView) {
            if (userName != ANONYMOUS && currentUserName == userName && userName != null) {
                textView.setBackgroundResource(R.drawable.tv_rounded_blue)
                textView.setTextColor(Color.WHITE)
            } else {
                textView.setBackgroundResource(R.drawable.tv_rounded_gray)
                textView.setTextColor(Color.BLACK)
            }
        }
    }

    inner class ImageMessageViewHolder(
        private val binding: ItemImageMessageBinding
    ) : ViewHolder(binding.root) {
        fun bind(item: Message) {
            loadImageIntoView(binding.ivImageMessage, item.imageUrl!!)

            binding.tvMessenger.text = item.name ?: ANONYMOUS
            if (item.photoUrl != null) {
                loadImageIntoView(binding.ivMessenger, item.photoUrl)
            } else {
                binding.ivMessenger.setImageResource(R.drawable.ic_account_circle)
            }
        }
    }

    private fun loadImageIntoView(view: ImageView, url: String) {
        if (url.startsWith("gs://")) {
            val storageReference = Firebase.storage.getReferenceFromUrl(url)
            storageReference.downloadUrl
                .addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    Glide.with(view.context)
                        .load(downloadUrl)
                        .into(view)
                }
                .addOnFailureListener { e -> }
        } else {
            Glide.with(view.context).load(url).into(view)
        }
    }

    companion object {
        const val VIEW_TYPE_TEXT = 1
        const val VIEW_TYPE_IMAGE = 2
    }
}
