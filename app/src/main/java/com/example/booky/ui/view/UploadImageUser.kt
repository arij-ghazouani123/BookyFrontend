package com.example.booky.ui.view


import android.Manifest
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
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.booky.R
import com.example.booky.data.api.RestApiService
import com.example.booky.data.api.RetrofitInstance
import com.example.booky.data.models.User
import com.example.booky.data.models.fileutil
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
import java.util.ArrayList


class UploadImageUser : AppCompatActivity() {
    lateinit var imgsel: Button
    lateinit var upload:Button
    lateinit var img: ImageView
    lateinit  var path: String
    lateinit var uri: Uri
    lateinit var nowuser: User
    private lateinit var mSharedPref: SharedPreferences
    var f: fileutil = fileutil()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_image_user)

        mSharedPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        val toolbar: Toolbar = findViewById(R.id.toolbarback)
        setSupportActionBar(toolbar)


        toolbar.setNavigationOnClickListener {


            finish()
        }
        Ion.getDefault(this).configure().setLogging("ion-sample", Log.DEBUG);
        img = findViewById(R.id.img);
        imgsel = findViewById(R.id.selimg);
        upload =findViewById(R.id.uploadimg);
        upload.setVisibility(View.INVISIBLE);
        upload.setOnClickListener {
            doLogin()
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

    private fun doLogin(){
        checkAndRequestPermission()
        val apiInterface = RetrofitInstance.getRetrofitInstance().create(RestApiService::class.java)

        val file = File(f.getPath(uri,this))
        val gson = Gson()
        val  us =  mSharedPref.getString(myuser, "")

        nowuser = gson.fromJson(us,User::class.java)



        val  reqFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val image = MultipartBody.Part.createFormData("upload",
            file.getName(), reqFile)

        apiInterface.upload(image,nowuser.id).enqueue(object : Callback<User> {

            override fun onResponse(call: Call<User>, response: retrofit2.Response<User>) {

                val user = response.body()

                if (user != null){
                    Toast.makeText(this@UploadImageUser, "Image Updated", Toast.LENGTH_SHORT).show()
                    Log.d("user",user.toString())
                    nowuser.profilPic = user.profilPic
                    val json = gson.toJson(nowuser)
                    print("////////////////////////////////////////////////")
                    Log.d("json",json.toString())
                    mSharedPref.edit().apply {
                        putString(myuser, json)

                    }.apply()

                }else{
                    Toast.makeText(this@UploadImageUser, "can not Update Image", Toast.LENGTH_SHORT).show()
                }


                window.clearFlags( WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@UploadImageUser, t.message, Toast.LENGTH_SHORT).show()


                window.clearFlags( WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }

        })


    }

}