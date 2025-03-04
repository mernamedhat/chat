package com.example.mychat

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class MainActivity : AppCompatActivity() {
    //    private lateinit var binding: ActivityChatBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var chatAdapter: ChatAdapter
    private var receiverId: String = ""
    lateinit var chatRecyclerView:RecyclerView
    //    private lateinit var prefManager: OnBoardingPrefManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
         chatRecyclerView=findViewById<RecyclerView>(R.id.chatRecyclerView)
        var sendButton=findViewById<ImageView>(R.id.sendMessageButton)
       var imageButton=findViewById<ImageView>(R.id.attachFileButton)
       var voiceButton=findViewById<ImageView>(R.id.attachFileButton)
        var messageInput=findViewById<EditText>(R.id.messageInput)
//        prefManager = OnBoardingPrefManager(this)
        receiverId = intent.getIntExtra("userId", -1).toString()
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("chats/room1")

        chatAdapter = ChatAdapter(emptyList(), "prefManager.UesrId.toString()")
       chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = chatAdapter

        loadMessages()

        sendButton.setOnClickListener {
            val messageText = messageInput.text.toString()
            if (messageText.isNotBlank()) {
                sendMessage(messageText, "TEXT")
                messageInput.text.clear()
            }
        }

       imageButton.setOnClickListener {
            pickImage()
        }

        voiceButton.setOnClickListener {
            recordVoiceMessage()
        }
    }




    private fun loadMessages() {
        database.child("messages").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = mutableListOf<Message>()
                snapshot.children.forEach { data ->
                    val message = data.getValue(Message::class.java)
                    if (message != null) messages.add(message)
                }
                chatAdapter.updateMessages(messages)
                if (messages.isNotEmpty()) {
                    chatRecyclerView.scrollToPosition(messages.size - 1)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Database", "Failed to load messages", error.toException())
            }
        })
    }

    private fun sendMessage(content: String, type: String, mediaUrl: String? = null) {
        val messageId = database.child("messages").push().key
        val message = Message(
//            senderId = prefManager.UesrId.toString(),
            receiverId = receiverId,
            message = if (type == "TEXT") content else null,
            mediaUrl = mediaUrl,
            messageType = type,
            timestamp = System.currentTimeMillis()
        )

        messageId?.let {
            database.child("messages").child(it).setValue(message)
        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_PICK_REQUEST)
    }

    private fun recordVoiceMessage() {
        // Implement voice recording and upload logic
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_REQUEST && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            imageUri?.let { uri ->
                uploadMedia(uri) { imageUrl ->
                    sendMessage("", "IMAGE", imageUrl)
                }
            }
        }
    }

    private fun uploadMedia(uri: Uri, callback: (String) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().getReference("chat_media/${UUID.randomUUID()}")
        storageRef.putFile(uri)
            .addOnSuccessListener { task ->
                task.metadata?.reference?.downloadUrl?.addOnSuccessListener { url ->
                    callback(url.toString())
                }
            }
            .addOnFailureListener {
                Log.e("Upload", "Failed to upload media", it)
            }
    }

    companion object {
        private const val IMAGE_PICK_REQUEST = 1001
    }
}
