package com.example.booky.ui.adapter

import android.content.Context
import android.content.SharedPreferences
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.booky.R
import com.example.booky.data.models.Conversation
import com.example.booky.data.models.MessageWithoutPopulate
import com.example.booky.data.models.User
import com.example.booky.ui.view.PREF_NAME
import com.example.booky.ui.view.myuser
import com.google.gson.Gson


class MessageAdapter(var items: MutableList<MessageWithoutPopulate>, var conversation: Conversation) :
    RecyclerView.Adapter<MessageAdapter.ConversationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_message, parent, false)
        return ConversationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.bindView(items[position], conversation)
    }

    override fun getItemCount(): Int = items.size

    class ConversationViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        private val lastMessageTV: TextView = itemView.findViewById(R.id.descriptionTV)
        // private val profilePictureIV: ImageView = itemView.findViewById(R.id.profilePictureIV)
        private val userNameTV: TextView = itemView.findViewById(R.id.userNameTV)

        fun bindView(message: MessageWithoutPopulate, conversation: Conversation) {

            val sharedPreferences =
                itemView.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val gson = Gson()
            val  us =  sharedPreferences.getString(myuser, "")

            val  nowuser = gson.fromJson(us, User::class.java)
            val userId =nowuser.id




            if (message.senderConversation!!.sender == userId) {
                (itemView as LinearLayout).gravity = Gravity.START

                userNameTV.text = conversation.sender!!.firstName + " " + conversation.sender!!.lastName

            } else {
                userNameTV.text = conversation.receiver!!.firstName + " " + conversation.receiver!!.lastName

            }

            itemView.setOnClickListener {

            }

            lastMessageTV.text = message.description

        }
    }
}