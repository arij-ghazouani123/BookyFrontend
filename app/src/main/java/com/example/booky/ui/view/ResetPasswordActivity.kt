package com.example.booky.ui.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.ArrayMap
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.booky.R
import com.example.booky.data.api.RestApiService
import com.example.booky.data.api.RetrofitInstance
import com.example.booky.data.models.User
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import android.util.Log
import android.view.WindowManager
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response
import kotlin.math.log

class ResetPasswordActivity : AppCompatActivity() {
    lateinit var submitBtn : Button
    lateinit var idUser : String
    lateinit var mainIntent : Intent
    private lateinit var mSharedPref: SharedPreferences
    lateinit var nowuser: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        submitBtn = findViewById<Button>(R.id.submit_btn)
        mSharedPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        val gson = Gson()
        val  us =  mSharedPref.getString(myuser, "")

        nowuser = gson.fromJson(us, User::class.java)
        submitBtn.setOnClickListener {
            val newPass = findViewById<EditText>(R.id.newpasswordEditText)
            val confNewPass = findViewById<EditText>(R.id.confnewpasswordEditText)
            val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val email = sharedPreferences.getString("email_reset_pass", null).toString()
            print(email)

            if (validateNewPass(newPass, confNewPass)) {
                mainIntent = Intent(this, LoginActivity::class.java)
                doLogin(

                    newPass.text.toString().trim(),

                    )
            }
        }
    }
    private fun validateNewPass(newPass: EditText,confNewPass: EditText): Boolean {
        if (newPass.text.trim().isEmpty() || confNewPass.text.trim().isEmpty() ) {


            if (newPass.text.isEmpty()) {
                newPass.error = "Password is required"
                newPass.requestFocus()

            }


            if (confNewPass.text.isEmpty()) {
                confNewPass.error = "Password does not match"
                confNewPass.requestFocus()

            }

            return false
        }

        //Patterns // Regex // Length
        if (newPass.text.length < 6){
            newPass.error = "Password must be at least 6 characters"
            newPass.requestFocus()
            return false
        }

        if (newPass.text.toString() != confNewPass.text.toString()){
            confNewPass.error = "Password does not match"
            confNewPass.requestFocus()
            return false
        }


        return true
    }

    private fun doLogin(pass:String){

        val apiInterface = RetrofitInstance.getRetrofitInstance().create(RestApiService::class.java)


        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        val jsonParams: MutableMap<String?, Any?> = ArrayMap()
//put something inside the map, could be null
//put something inside the map, could be null
        jsonParams["id"] = nowuser.id
        jsonParams["password"] = pass



        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            JSONObject(jsonParams).toString()
        )

        apiInterface.UpdateUser(body).enqueue(object : Callback<User> {

            override fun onResponse(call: Call<User>, response: Response<User>) {

                val user = response.body()

                if (user != null){
                    Toast.makeText(this@ResetPasswordActivity, "Password changed", Toast.LENGTH_SHORT).show()
                    Log.d("user",user.toString())


                    startActivity(mainIntent)
                    finish()
                }else{
                    Toast.makeText(this@ResetPasswordActivity, "Password can not change", Toast.LENGTH_SHORT).show()
                }


                window.clearFlags( WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@ResetPasswordActivity, t.message, Toast.LENGTH_SHORT).show()


                window.clearFlags( WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }

        })


    }
}