package com.example.booky.ui.adapter

import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.booky.R
import com.example.booky.data.api.ChatAPIService
import com.example.booky.data.api.ChatService
import com.example.booky.data.models.Conversation
import com.example.booky.ui.view.ChatActivity
import com.example.booky.ui.view.PREF_NAME
import com.example.booky.ui.view.USER_ID
import com.example.booky.ui.view.myuser
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ConversationAdapter(var items: MutableList<Conversation>) :
    RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_conversation, parent, false)
        return ConversationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        Log.d("conver",items[position].toString())
        holder.bindView(items[position])
    }

    override fun getItemCount(): Int = items.size

    class ConversationViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        private val profilePictureIV: ImageView = itemView.findViewById(R.id.profilePictureIV)
        private val conversationNameTV: TextView = itemView.findViewById(R.id.titleTV)
        private val lastMessageTV: TextView = itemView.findViewById(R.id.descriptionTV)
        private val deleteConversationButton: TextView =
            itemView.findViewById(R.id.deleteConversationButton)

        fun bindView(conversation: Conversation) {

            itemView.setOnClickListener {  v->
                lateinit var mSharedPref: SharedPreferences
                mSharedPref = v.context.getSharedPreferences(PREF_NAME, AppCompatActivity.MODE_PRIVATE);
                val intent = Intent(itemView.context, ChatActivity::class.java)
                var gson = Gson()
                val json = gson.toJson(conversation)
                mSharedPref.edit().apply {
                    putString("conversation", json)

                }.apply()

                itemView.context.startActivity(intent)
            }


            conversationNameTV.text = conversation.receiver?.firstName + " " + conversation.receiver?.lastName
            lastMessageTV.text = conversation.lastMessage

            deleteConversationButton.setOnClickListener {
                ChatAPIService.chatService.deleteConversation(  ChatService.DeleteBody(
                    conversation._id
                ))?.enqueue(
                    object : Callback<ChatService.MessageResponse?> {
                        override fun onResponse(
                            call: Call<ChatService.MessageResponse?>,
                            response: Response<ChatService.MessageResponse?>
                        ) {
                            if (response.code() == 200) {
                                Snackbar.make(
                                    itemView,
                                    "Conversation Deleted",
                                    Snackbar.LENGTH_SHORT
                                )
                                    .show()
                            } else {
                                Log.d("BODY", "id code is " + conversation._id)
                                println("status code is " + response.code())
                            }
                        }

                        override fun onFailure(
                            call: Call<ChatService.MessageResponse?>,
                            t: Throwable
                        ) {
                            println("HTTP ERROR")
                            t.printStackTrace()
                        }
                    }
                )
            }
        }
    }
}