package com.example.booky.ui.view


import android.Manifest
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.util.ArrayMap
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.booky.MainActivity
import com.example.booky.R
import com.example.booky.data.api.RestApiService
import com.example.booky.data.api.RetrofitInstance
import com.example.booky.data.models.Book
import com.example.booky.data.models.User
import com.example.booky.data.models.fileutil

import com.example.booky.ui.view.PREF_NAME
import com.example.booky.ui.view.myuser

import com.google.gson.Gson
import com.koushikdutta.ion.Ion

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import java.io.File
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Locale


class UploadImageUpdate : AppCompatActivity() {
    lateinit var imgsel: Button
    lateinit var upload:Button
    lateinit var img: ImageView
    lateinit  var path: String
    lateinit var uri: Uri
    lateinit var nowuser: User
    private lateinit var radioGroup: RadioGroup
     lateinit var selectedRadioButton: RadioButton
    lateinit var amainIntent : Intent
    var button_date: Button? = null
    lateinit var textview_date: TextView
    var cal = Calendar.getInstance()
    private lateinit var mSharedPref: SharedPreferences
    var f: fileutil = fileutil()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_image)

        mSharedPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        val toolbar: Toolbar = findViewById(R.id.toolbarback)
        setSupportActionBar(toolbar)

        // get the references from layout file
        textview_date =  findViewById(R.id.text_view_date_1)
        button_date = findViewById(R.id.button_date_1)

        textview_date!!.text = "--/--/----"

        // create an OnDateSetListener
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        }

        // when you click on the button, show DatePickerDialog that is set with OnDateSetListener
        button_date!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(this@UploadImageUpdate,
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }

        })
        toolbar.setNavigationOnClickListener {


            finish()
        }
        radioGroup = findViewById(R.id.radioGroup)
      val  description =  mSharedPref.getString("description", "")

     val   subject = mSharedPref.getString("subject", "")

        Ion.getDefault(this).configure().setLogging("ion-sample", Log.DEBUG);
        img = findViewById(R.id.img);
        imgsel = findViewById(R.id.selimg);
        upload =findViewById(R.id.uploadimg);
        upload.setVisibility(View.INVISIBLE);
        upload.setOnClickListener {

            amainIntent=Intent(this, MainActivity::class.java)
            if (description != null) {
                if (subject != null) {
                    val selectedRadioButtonId: Int = radioGroup.checkedRadioButtonId
                    if (selectedRadioButtonId != -1) {

                        selectedRadioButton = findViewById(selectedRadioButtonId)
                        val string: String = selectedRadioButton.text.toString()
                         doLogin(description,subject,string,textview_date.text.trim().toString())

                    } else {
                        Toast.makeText(this@UploadImageUpdate, "Nothing selected from the radio group", Toast.LENGTH_SHORT).show()

                    }

                }
            }
        }


        imgsel.setOnClickListener {
            val fintent = Intent(Intent.ACTION_GET_CONTENT)
            fintent.type = "image/jpeg"
            try {
                startActivityForResult(fintent, 100)
            } catch (e: ActivityNotFoundException) {
            }
        }

    }
    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        textview_date!!.text = sdf.format(cal.getTime())
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) return
        when (requestCode) {
            100 -> if (resultCode == RESULT_OK) {
                uri = data.data!!
                img.setImageURI(data.data)
                upload.visibility = View.VISIBLE
            }
        }
    }
    private val apppermissions = arrayOf<String>(

        Manifest.permission.INTERNET,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    private fun checkAndRequestPermission(): Boolean {
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        for (perm in apppermissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    perm
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                listPermissionsNeeded.add(perm)
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                this, listPermissionsNeeded.toTypedArray(),
                200
            )
            return false
        }
        return true
    }

    private fun doUpdateImage(idBook:String){
        checkAndRequestPermission()
        val apiInterface = RetrofitInstance.getRetrofitInstance().create(RestApiService::class.java)
        val file = File(f.getPath(uri,this))




        val  reqFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val image = MultipartBody.Part.createFormData("upload",
            file.getName(), reqFile)

        apiInterface.uploadimagebook(image,idBook).enqueue(object : Callback<Book> {

            override fun onResponse(call: Call<Book>, response: retrofit2.Response<Book>) {

                val user = response.body()

                if (user != null){
                    Toast.makeText(this@UploadImageUpdate, "Image Updated", Toast.LENGTH_SHORT).show()

                    startActivity(amainIntent)
                    finish()



                }else{
                    Toast.makeText(this@UploadImageUpdate, "can not Update Image", Toast.LENGTH_SHORT).show()
                }


                window.clearFlags( WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }

            override fun onFailure(call: Call<Book>, t: Throwable) {
                Toast.makeText(this@UploadImageUpdate, t.message, Toast.LENGTH_SHORT).show()


                window.clearFlags( WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }

        })


    }
    private fun doLogin(description:String,Subject:String,offre:String,Year:String){
        val gson = Gson()
        val  us =  mSharedPref.getString(myuser, "")

        nowuser = gson.fromJson(us,User::class.java)
        val apiInterface = RetrofitInstance.getRetrofitInstance().create(RestApiService::class.java)

val idbook =mSharedPref.getString("idbook", "")
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        val jsonParams: MutableMap<String?, Any?> = ArrayMap()
//put something inside the map, could be null
//put something inside the map, could be null
        jsonParams["description"] = description
        jsonParams["userId"] = nowuser.id
        jsonParams["title"] = Subject
        jsonParams["year"] = Year
        jsonParams["offre"] = offre
        jsonParams["image"] = "image"


        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            JSONObject(jsonParams).toString()
        )

        apiInterface.Updatbook(body,idbook.toString()).enqueue(object : Callback<Book> {

            override fun onResponse(call: Call<Book>, response: Response<Book>) {

                val user = response.body()

                if (user != null){
                    Toast.makeText(this@UploadImageUpdate, "Book Updated", Toast.LENGTH_SHORT).show()
                    doUpdateImage(user.id)


                }else{
                    Toast.makeText(this@UploadImageUpdate, "can not Update Book", Toast.LENGTH_SHORT).show()
                }


                window.clearFlags( WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }

            override fun onFailure(call: Call<Book>, t: Throwable) {
                Toast.makeText(this@UploadImageUpdate, t.message, Toast.LENGTH_SHORT).show()


                window.clearFlags( WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }

        })


    }


}