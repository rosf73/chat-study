package rosf73.study.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import rosf73.study.chat.data.Message
import rosf73.study.chat.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var manager: LinearLayoutManager

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase

    private lateinit var adapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        db = Firebase.database
        initChat()
        setListener()
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
    }

    public override fun onPause() {
        adapter.stopListening()
        super.onPause()
    }

    public override fun onResume() {
        super.onResume()
        adapter.startListening()
    }

    private fun initChat() {
        val messagesRef = db.reference.child(MESSAGES_CHILD)

        val options = FirebaseRecyclerOptions.Builder<Message>()
            .setQuery(messagesRef, Message::class.java)
            .build()

        adapter = MessageAdapter(options, getUserName())
        manager = LinearLayoutManager(this)
        manager.stackFromEnd = true

        binding.pbMain.visibility = ProgressBar.INVISIBLE
        binding.rvMessages.layoutManager = manager
        binding.rvMessages.adapter = adapter

        adapter.registerAdapterDataObserver(
            ScrollToBottomObserver(binding.rvMessages, adapter, manager)
        )
    }

    private fun getPhotoUrl(): String? {
        val user = auth.currentUser
        return user?.photoUrl?.toString()
    }

    private fun getUserName(): String? {
        val user = auth.currentUser
        return if (user != null) {
            user.displayName
        } else {
            ANONYMOUS
        }
    }

    private fun setListener() {
        binding.etMessage.addTextChangedListener(ButtonObserver(binding.btnSend))

        binding.btnSend.setOnClickListener {
            val msg = Message(
                binding.etMessage.text.toString(),
                getUserName(),
                getPhotoUrl(),
                null
            )
            db.reference.child(MESSAGES_CHILD).push().setValue(msg) // send message (no image)
            binding.etMessage.setText("")
        }
    }

    companion object {
        const val MESSAGES_CHILD = "messages"
        const val ANONYMOUS = "anonymous"
    }
}