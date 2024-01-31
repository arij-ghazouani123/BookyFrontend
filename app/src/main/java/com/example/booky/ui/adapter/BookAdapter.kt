package com.example.booky.ui.adapter

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.booky.MainActivity
import com.example.booky.R
import com.example.booky.data.api.ChatAPIService
import com.example.booky.data.api.ChatService
import com.example.booky.data.models.BookList
import com.example.booky.data.models.Conversation
import com.example.booky.data.models.User
import com.example.booky.ui.view.ChatActivity
import com.example.booky.ui.view.PREF_NAME
import com.example.booky.ui.view.myuser
import com.google.android.material.imageview.ShapeableImageView
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date


class BookAdapter(private val bookList: MutableList<BookList>): RecyclerView.Adapter<BookAdapter.BookViewHolder>(){
    lateinit var itemView2:View


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_row,parent,false)
        itemView2 = LayoutInflater.from(parent.context).inflate(R.layout.list_row_click,parent,false)
        return BookViewHolder(itemView)
    }

    override fun getItemCount(): Int {
       return bookList.size
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val currentItem = bookList[position]

        holder.bookTitle.text = currentItem.title
        holder.bookDescription.text = currentItem.description
        holder.bookOffer.text = currentItem.offre
        holder.usernme.text = currentItem.userId.firstName + " " +currentItem.userId.lastName
        var imagee= bookList[position].userId.profilPic
        var imagebook= bookList[position].image
        if( imagee!=null && imagee.length>14){
            imagee = "img/"+ imagee.subSequence(14,imagee.length)

        }

        if( imagebook!=null && imagebook.length>14){
            imagebook = "img/"+ imagebook.subSequence(14,imagebook.length)

        }

        holder.UserImage.setOnClickListener{v->

            val builder: AlertDialog.Builder = AlertDialog.Builder(v.context)
            builder.setPositiveButton(
                "Close"
            ) { dialog, which ->

                if (itemView2.getParent() != null) {
                    (itemView2.getParent() as ViewGroup).removeView(itemView2) // <- fix
                }
                dialog.dismiss()
            }
            lateinit var mSharedPref: SharedPreferences
            mSharedPref = v.context.getSharedPreferences(PREF_NAME, AppCompatActivity.MODE_PRIVATE);
            val gson = Gson()
            val  us =  mSharedPref.getString(myuser, "")

          val  nowuser = gson.fromJson(us, User::class.java)
            val textView = itemView2.findViewById<TextView>(R.id.idfullname)
            textView.setText(currentItem.userId.firstName + " " +currentItem.userId.lastName)
            val imageuser = itemView2.findViewById<ShapeableImageView>(R.id.idUrlImg)
            val button = itemView2.findViewById<Button>(R.id.logout_btn_uploadImage)
            button!!.setOnClickListener {




                    ChatAPIService.chatService.envoyerMessage(
                        ChatService.MessageBody(
                           "Hey",
                            nowuser.id,
                            //"645832fbfe46bf1dd1036bfd"
                            currentItem.userId.id

                        )
                    ).enqueue(object : Callback<ChatService.MessageResponse> {
                        override fun onResponse(
                            call: Call<ChatService.MessageResponse>,
                            response: Response<ChatService.MessageResponse>
                        ) {
                            if (response.code() == 200) {

                            }
                        }

                        override fun onFailure(
                            call: Call<ChatService.MessageResponse>,
                            t: Throwable
                        ) {
                            t.printStackTrace()
                        }
                    })

                Toast.makeText(v.context, "Hey send !!", Toast.LENGTH_SHORT).show()
            }

            Glide.with(imageuser).load("http://10.0.2.2:9090/" +imagee).placeholder(R.drawable.user).circleCrop()
                .error(R.drawable.user).into(imageuser)
            builder.setView(itemView2);
            builder.show();
        }
        Glide.with(holder.UserImage).load("http://10.0.2.2:9090/" +imagee).placeholder(R.drawable.user).circleCrop()
            .error(R.drawable.user).into(holder.UserImage)
        Glide.with(holder.bookImage).load("http://10.0.2.2:9090/" +imagebook).placeholder(R.drawable.user)
            .error(R.drawable.user).into(holder.bookImage)

    }


    class BookViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val bookImage: ImageView =itemView.findViewById(R.id.imagebook)
        val UserImage: ShapeableImageView =itemView.findViewById(R.id.title_image)
        val bookTitle: TextView = itemView.findViewById(R.id.book_title)
        val bookDescription: TextView = itemView.findViewById(R.id.book_description)
        val bookOffer: TextView = itemView.findViewById(R.id.book_offer)
        val usernme: TextView = itemView.findViewById(R.id.username)
    }
}
