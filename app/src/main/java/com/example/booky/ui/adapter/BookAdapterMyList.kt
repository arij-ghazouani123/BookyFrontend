package com.example.booky.ui.adapter

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.booky.MainActivity
import com.example.booky.R
import com.example.booky.data.api.RestApiService
import com.example.booky.data.api.RetrofitInstance
import com.example.booky.data.models.BookList
import com.example.booky.data.models.deletebook
import com.example.booky.ui.view.PREF_NAME
import com.example.booky.ui.view.UpdateBook
import com.google.android.material.imageview.ShapeableImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookAdapterMyList(private val bookList: MutableList<BookList>): RecyclerView.Adapter<BookAdapterMyList.BookViewHolder>(){

    lateinit var itemView2:View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_row_user,parent,false)
        itemView2 = LayoutInflater.from(parent.context).inflate(R.layout.list_row_click,parent,false)
        return BookViewHolder(itemView)
    }

    override fun getItemCount(): Int {
       return bookList.size
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val currentItem = bookList[position]
        holder.usernme.text = currentItem.userId.firstName + " " +currentItem.userId.lastName
        holder.bookTitle.text = currentItem.title
        holder.bookDescription.text = currentItem.description
        holder.bookOffer.text = currentItem.offre
        var imagee= bookList[position].userId.profilPic
        var imagebook= bookList[position].image
        if( imagebook!=null && imagebook.length>14){
            imagebook = "img/"+ imagebook.subSequence(14,imagebook.length)

        }
        Glide.with(holder.bookImage).load("http://10.0.2.2:9090/" +imagebook).placeholder(R.drawable.user)
            .error(R.drawable.user).into(holder.bookImage)
        if( imagee!=null && imagee.length>14){
            imagee = "img/"+ imagee.subSequence(14,imagee.length)
Log.d("ima",imagee)
            Log.d("ima",bookList[position].image)
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

            val textView = itemView2.findViewById<TextView>(R.id.idfullname)
            textView.setText(currentItem.userId.firstName + " " +currentItem.userId.lastName)
            val imageuser = itemView2.findViewById<ShapeableImageView>(R.id.idUrlImg)
            Glide.with(imageuser).load("http://10.0.2.2:9090/" +imagee).placeholder(R.drawable.user).circleCrop()
                .error(R.drawable.user).into(imageuser)

            builder.setView(itemView2);
            builder.show();
        }


        Glide.with(holder.UserImage).load("http://10.0.2.2:9090/" + imagee).placeholder(R.drawable.user).circleCrop()
            .error(R.drawable.user).into(holder.UserImage)
        holder.bookDelete.setOnClickListener{v->
doLogin(bookList[position].id,v)


        }
        holder.bookUpdate.setOnClickListener{v->
            lateinit var mSharedPref: SharedPreferences
            mSharedPref = v.context.getSharedPreferences(PREF_NAME, AppCompatActivity.MODE_PRIVATE);
            mSharedPref.edit().apply {
                putString("idbook",currentItem.id)
                putString("description",currentItem.description)
                putString("subject",currentItem.title)

            }.apply()
           val intent = Intent(v.context, UpdateBook::class.java)

            v.context.startActivity(intent)


        }
    }
    private fun doLogin(id:String,v:View){

        val apiInterface = RetrofitInstance.getRetrofitInstance().create(RestApiService::class.java)



        apiInterface.deletebook(id).enqueue(object : Callback<deletebook> {

            override fun onResponse(call: Call<deletebook>, response: Response<deletebook>) {

                val user = response.body()

                if (user != null) {
                    val intent = Intent(v.context, MainActivity::class.java)

                    v.context.startActivity(intent)



                }



            }

            override fun onFailure(call: Call<deletebook>, t: Throwable) {

            }

        })


    }

    class BookViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val bookImage: ImageView =itemView.findViewById(R.id.imagebook)
        val UserImage: ShapeableImageView =itemView.findViewById(R.id.title_image)
        val bookTitle: TextView = itemView.findViewById(R.id.book_title)
        val bookDescription: TextView = itemView.findViewById(R.id.book_description)
        val bookOffer: TextView = itemView.findViewById(R.id.book_offer)
        val bookDelete: TextView = itemView.findViewById(R.id.book_delete)
        val bookUpdate: TextView = itemView.findViewById(R.id.book_update)
        val usernme: TextView = itemView.findViewById(R.id.username)

    }
}
