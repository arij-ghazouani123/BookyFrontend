package com.example.booky.ui.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.booky.R
import com.example.booky.data.api.ChatAPIService
import com.example.booky.data.api.ChatService
import com.example.booky.data.models.Conversation
import com.example.booky.data.models.MessageWithoutPopulate
import com.example.booky.data.models.User
import com.example.booky.ui.adapter.MessageAdapter
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatActivity : AppCompatActivity() {

    var messagesList: MutableList<MessageWithoutPopulate> = mutableListOf()
    private var chatRV: RecyclerView? = null
    var addButton: Button? = null
    var messageTIET: TextInputEditText? = null

    private var currentConversation: Conversation? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        chatRV = findViewById(R.id.chatRV)
        addButton = findViewById(R.id.addButton)
        messageTIET = findViewById(R.id.messageTIET)

        val sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val  us =  sharedPreferences.getString(myuser, "")

      val  nowuser = gson.fromJson(us, User::class.java)
        val userId =nowuser.id
        val senderId =nowuser.id

        val  us2 =  sharedPreferences.getString("conversation", "")
          currentConversation = gson.fromJson(us2, Conversation::class.java)
        if(senderId == currentConversation!!.sender!!.id)
        {
            senderId == currentConversation!!.receiver!!.id
        }else{
            senderId == currentConversation!!.sender!!.id
        }
        val currentUser = currentConversation!!.receiver

        val linearLayoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        chatRV!!.layoutManager = linearLayoutManager
        chatRV!!.adapter = MessageAdapter(
            messagesList,
            currentConversation!!
        )

        getData()
        startUpdates()

        addButton!!.setOnClickListener {
            ChatAPIService.chatService.envoyerMessage(
                ChatService.MessageBody(
                    messageTIET!!.text.toString(),
                    userId,
                    //"645832fbfe46bf1dd1036bfd"
                    currentUser!!.id
                )
            ).enqueue(object : Callback<ChatService.MessageResponse> {
                override fun onResponse(
                    call: Call<ChatService.MessageResponse>,
                    response: Response<ChatService.MessageResponse>
                ) {
                    if (response.code() == 200) {
                        messageTIET!!.setText("")
                    }
                }

                override fun onFailure(
                    call: Call<ChatService.MessageResponse>,
                    t: Throwable
                ) {
                    t.printStackTrace()
                }
            })
        }
    }

    private fun getData() {
        ChatAPIService.chatService.getMyMessages(
            ChatService.OneConversationBody(
                currentConversation!!._id,

                )
        ).enqueue(
            object : Callback<ChatService.MessagesResponse> {
                override fun onResponse(
                    call: Call<ChatService.MessagesResponse>,
                    response: Response<ChatService.MessagesResponse>
                ) {
                    if (response.code() == 200) {
                        messagesList = response.body()?.messages!! as MutableList<MessageWithoutPopulate>

                        chatRV!!.adapter = MessageAdapter(messagesList, currentConversation!!)
                        chatRV!!.scrollToPosition(messagesList.size - 1);

                    } else {
                        println("status code is " + response.code())
                    }
                }

                override fun onFailure(
                    call: Call<ChatService.MessagesResponse>,
                    t: Throwable
                ) {
                    println("HTTP ERROR")
                    t.printStackTrace()
                }
            }
        )
    }

    private val scope = MainScope() // could also use an other scope such as viewModelScope if available
    var job: Job? = null

    private fun startUpdates() {
        job = scope.launch {
            while (true) {
                getData() // the function that should be ran every second
                delay(2500)
            }
        }
    }

    private fun stopUpdates() {
        job?.cancel()
        job = null
    }

    override fun finish() {
        super.finish()
        stopUpdates()
    }

}