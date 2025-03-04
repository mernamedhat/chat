package com.example.mychat

data class Message(
    val senderId: String = "",
    val receiverId: String = "",
    val message: String? = null,
    val mediaUrl: String? = null, // URL for image or voice message
    val messageType: String = "TEXT", // TEXT, IMAGE, VOICE
    val timestamp: Long = 0
)


