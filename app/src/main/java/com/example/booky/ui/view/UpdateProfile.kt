package com.example.booky.ui.view

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.ArrayMap
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.booky.GoogleLogin
import com.example.booky.MainActivity
import com.example.booky.R
import com.example.booky.data.api.RestApiService
import com.example.booky.data.api.RetrofitInstance
import com.example.booky.data.models.User
import com.example.booky.utils.LoadingDialog
//import com.google.android.gms.auth.api.signin.GoogleSignIn
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount
//import com.google.android.gms.auth.api.signin.GoogleSignInClient
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions
//import com.google.android.gms.common.SignInButton
//import com.google.android.gms.common.api.ApiException
//import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateProfile : AppCompatActivity() {
    lateinit var btnLogin: Button
    lateinit var txtPassword: TextInputEditText
    lateinit var txtLayoutPassword: TextInputLayout
    lateinit var mainIntent : Intent
    private lateinit var mSharedPref: SharedPreferences
    lateinit var nowuser: User
    lateinit var txtPasswordConfirmed: TextInputEditText
    lateinit var txtLayoutPasswordConfirmed: TextInputLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_updateprofile)

        val toolbar: Toolbar = findViewById(R.id.toolbarback)
        setSupportActionBar(toolbar)


        toolbar.setNavigationOnClickListener {


            finish()
        }
        btnLogin = findViewById(R.id.findAccount)

        mSharedPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        val gson = Gson()
        val  us =  mSharedPref.getString(myuser, "")

        nowuser = gson.fromJson(us, User::class.java)
        txtPassword = findViewById(R.id.txtEmail)
        txtLayoutPassword = findViewById(R.id.txtLayoutEmailConfirmation)



        txtPasswordConfirmed = findViewById(R.id.txtEmailm)
        txtLayoutPasswordConfirmed = findViewById(R.id.txtLayoutEmailConfirmationc)
        btnLogin.setOnClickListener{

            txtLayoutPassword!!.error = null

            txtLayoutPasswordConfirmed!!.error = null

            if (txtPassword?.text!!.isEmpty()) {
                txtLayoutPassword!!.error = "must not be empty"
                return@setOnClickListener
            }

            if (txtPasswordConfirmed?.text!!.isEmpty()) {
                txtLayoutPasswordConfirmed!!.error = "must not be empty"
                return@setOnClickListener
            }


            mainIntent = Intent(this, MainActivity::class.java)

            doLogin()
        }


    }
    private fun doLogin(){

        val apiInterface = RetrofitInstance.getRetrofitInstance().create(RestApiService::class.java)



        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        val jsonParams: MutableMap<String?, Any?> = ArrayMap()
//put something inside the map, could be null
//put something inside the map, could be null
        jsonParams["_id"] = nowuser.id
        jsonParams["firstName"] = txtPassword!!.text.toString()
        jsonParams["lastName"] = txtPasswordConfirmed!!.text.toString()



        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            JSONObject(jsonParams).toString()
        )

        apiInterface.updateusernotpass(body).enqueue(object : Callback<User> {

            override fun onResponse(call: Call<User>, response: Response<User>) {

                val user = response.body()

                if (user != null){
                    Toast.makeText(this@UpdateProfile, "User Updated", Toast.LENGTH_SHORT).show()
                    Log.d("user",user.toString())
                    val gson = Gson()
                    val json = gson.toJson(user)
                    print("////////////////////////////////////////////////")
                    Log.d("json",json.toString())
                    mSharedPref.edit().apply {
                        putString(myuser, json)

                    }.apply()
                    startActivity(mainIntent)
                    finish()
                }else{
                    Toast.makeText(this@UpdateProfile, "User can not Update", Toast.LENGTH_SHORT).show()
                }


                window.clearFlags( WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@UpdateProfile, t.message, Toast.LENGTH_SHORT).show()


                window.clearFlags( WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }

        })


    }
}