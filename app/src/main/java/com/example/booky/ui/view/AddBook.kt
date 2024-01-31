package com.example.booky.ui.view

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.ArrayMap
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.booky.MainActivity
import com.example.booky.R
import com.example.booky.data.api.RestApiService
import com.example.booky.data.api.RetrofitInstance
import com.example.booky.data.models.Book
import com.example.booky.data.models.User

import com.example.booky.ui.view.PREF_NAME
import com.example.booky.ui.view.USER_ID
import com.example.booky.ui.view.myuser

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AddBook : AppCompatActivity() {
    private lateinit var txtEdit: EditText

    private lateinit var txtEditSub: EditText
    lateinit var nowuser: User
    lateinit var btnADDQuestion: Button
    private lateinit var mSharedPref: SharedPreferences
    lateinit var amainIntent : Intent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book)
        //toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        mSharedPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);


        toolbar.setNavigationOnClickListener {

            val  mainIntent = Intent(this, MainActivity::class.java)

            startActivity(mainIntent)
            finish()
        }

        txtEdit = findViewById(R.id.Edittext)
        txtEditSub = findViewById(R.id.EdittextSubject)
        btnADDQuestion= findViewById(R.id.btnAddQuestion)

        amainIntent = Intent(this, MainActivity::class.java)
        btnADDQuestion.setOnClickListener{
            if (txtEditSub?.text!!.isEmpty()) {
                Toast.makeText(this@AddBook, " Book Title must not be empty", Toast.LENGTH_SHORT).show()

            return@setOnClickListener
        }
        if (txtEdit?.text!!.isEmpty()) {
            Toast.makeText(this@AddBook, "Description must not be empty", Toast.LENGTH_SHORT).show()

            return@setOnClickListener
        }
            val  mainIntent = Intent(this, UploadImage::class.java)

            mSharedPref.edit().apply {
                putString("description",txtEdit.text.toString())
                putString("subject",txtEditSub.text.toString())

            }.apply()
            startActivity(mainIntent)


        }
    }

}