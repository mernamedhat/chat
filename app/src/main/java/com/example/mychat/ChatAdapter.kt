package com.example.mychat

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide

class ChatAdapter(private var messages: List<Message>, private val currentUserId: String) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.messageText)
        val messageContainer: LinearLayout = itemView.findViewById(R.id.messageContainer)
        val messageImage: ImageView = itemView.findViewById(R.id.messageImage)
        val voiceIcon: ImageView = itemView.findViewById(R.id.voiceIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return ChatViewHolder(view)
    }

    @SuppressLint("PrivateResource")
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]

        // Reset visibility
        holder.messageText.visibility = View.GONE
        holder.messageImage.visibility = View.GONE
        holder.voiceIcon.visibility = View.GONE

        // Handle message types
        when (message.messageType) {
            "TEXT" -> {
                holder.messageText.text = message.message
                holder.messageText.visibility = View.VISIBLE
            }
            "IMAGE" -> {
                holder.messageImage.visibility = View.VISIBLE
//                Glide.with(holder.itemView.context)
//                    .load(message.mediaUrl)
//                    .into(holder.messageImage)
            }
            "VOICE" -> {
                holder.voiceIcon.visibility = View.VISIBLE
                holder.voiceIcon.setOnClickListener {
                    // Play audio (implement MediaPlayer logic)
                }
            }
        }

        // Align messages
//        val params = holder.messageContainer.layoutParams as FrameLayout.LayoutParams
        if (message.senderId == currentUserId) {
//            holder.messageContainer.setBackgroundResource(com.google.android.material.R.drawable.abc_ic_star_black_16dp)
//            params.gravity = Gravity.END
        } else {
//            holder.messageContainer.setBackgroundResource(com.google.android.material.R.drawable.abc_ic_star_half_black_16dp)
//            params.gravity = Gravity.START
        }
//        holder.messageContainer.layoutParams = params
    }

    override fun getItemCount(): Int = messages.size

    fun updateMessages(newMessages: List<Message>) {
        this.messages = newMessages
        notifyDataSetChanged()
    }
}


