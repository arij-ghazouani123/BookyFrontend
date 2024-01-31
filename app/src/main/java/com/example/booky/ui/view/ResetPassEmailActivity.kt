package com.example.booky.ui.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.ArrayMap
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.booky.R
import com.example.booky.data.api.RestApiService
import com.example.booky.data.api.RetrofitInstance
import com.example.booky.data.models.User
import com.example.booky.data.models.sendmail
import com.example.booky.utils.LoadingDialog
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ResetPassEmailActivity : AppCompatActivity() {


    lateinit var userEmailLayout: TextInputLayout;
    lateinit var userEmailEditText: EditText;
    lateinit var loadingDialog: LoadingDialog
    lateinit var submitBtn : Button

    var gson = Gson()
    lateinit var mainIntent : Intent
    lateinit var mSharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_pass_email)
        mSharedPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        loadingDialog = LoadingDialog(this)
        userEmailLayout = findViewById(R.id.userEmail_tfLayout)
        userEmailEditText = findViewById(R.id.userEmailEditText)
        submitBtn = findViewById<Button>(R.id.submit_email_btn)



        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()


        val random = rand(1000,9999).toString()

        mainIntent = Intent(this, ConfirmAfterResetActivity::class.java)

        submitBtn.setOnClickListener{

            if(validateInput(userEmailEditText)){
                findmail(userEmailEditText.text.toString(),random)


            }

        }
    }
    fun rand(start: Int, end: Int): Int {
        require(start <= end) { "Illegal Argument" }
        return (start..end).random()
    }
    private fun doLogin(email :String ,code :String){


        val apiInterface = RetrofitInstance.getRetrofitInstance().create(RestApiService::class.java)


        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )




        val jsonParams: MutableMap<String?, Any?> = ArrayMap()
//put something inside the map, could be null
//put something inside the map, could be null
        jsonParams["email"] = email
        jsonParams["activationCode"] = code

        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            JSONObject(jsonParams).toString()
        )

        apiInterface.sendmail(body).enqueue(object : Callback<sendmail> {

            override fun onResponse(call: Call<sendmail>, response: Response<sendmail>) {

                val user = response.body()

                if (user != null){




                    startActivity(mainIntent)
                    finish()
                }else{
                    Toast.makeText(this@ResetPassEmailActivity, "mail didn't send ", Toast.LENGTH_SHORT).show()
                }


                window.clearFlags( WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }

            override fun onFailure(call: Call<sendmail>, t: Throwable) {
                Toast.makeText(this@ResetPassEmailActivity, t.message, Toast.LENGTH_SHORT).show()


                window.clearFlags( WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }

        })


    }
    private fun findmail(email:String,code :String){


        val apiInterface = RetrofitInstance.getRetrofitInstance().create(RestApiService::class.java)


        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        val jsonParams: MutableMap<String?, Any?> = ArrayMap()
//put something inside the map, could be null
//put something inside the map, could be null
        jsonParams["email"] = email


        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            JSONObject(jsonParams).toString()
        )

        apiInterface.getuserbyemail(body).enqueue(object : Callback<MutableList<User>>{

            override fun onResponse(call: Call<MutableList<User>>, response: Response<MutableList<User>>) {

                val user = response.body()

                if (user != null){
                    if(user.size != 1){
                        Toast.makeText(this@ResetPassEmailActivity, "User not found", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this@ResetPassEmailActivity, "User Found", Toast.LENGTH_SHORT).show()

                        for (name in user) {
                            Log.d("user",name.toString())

                            val json = gson.toJson(name)
                            print("////////////////////////////////////////////////")
                            Log.d("json",json.toString())
                            mSharedPref.edit().apply {
                                putString(myuser, json)
                                putString("code", code)
                            }.apply()
                            doLogin(email,code)
                        }

                    }

                }else{
                    Toast.makeText(this@ResetPassEmailActivity, "User not found", Toast.LENGTH_SHORT).show()
                }


                window.clearFlags( WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }

            override fun onFailure(call: Call<MutableList<User>>, t: Throwable) {
                Toast.makeText(this@ResetPassEmailActivity, t.message, Toast.LENGTH_SHORT).show()


                window.clearFlags( WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }

        })


    }

    private fun validateInput( email: EditText): Boolean {
        if ( email.text.trim().isEmpty()) {


            if (email.text.isEmpty()) {
                email.error = "Email is required"
                email.requestFocus()

            }

        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.text).matches()){
            email.error = "Email unvalid"
            email.requestFocus()
            return false
        }

        return true
    }
}

