package com.example.booky.ui.view

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.ArrayMap
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.booky.R
import com.example.booky.data.api.RestApiService
import com.example.booky.data.api.RetrofitInstance
import com.example.booky.data.models.User
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


const val PREF_NAME = "DATA_CV_PREF"
const val emailfull = "email"
const val fullname = "fullname"
const val password = "password"
const val myuser = "myuser"
const val USER_ID = "USER_ID"
const val Facebookk = "FALSE"
const val IS_REMEMBRED = "remembred"
class RegisterActivity : AppCompatActivity() {
    lateinit var loginbtn : Button
    lateinit var registerbtn : Button
    var gson = Gson()
    lateinit var mainIntent : Intent
    lateinit var mSharedPref: SharedPreferences
   // lateinit var loadingDialog: LoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
       mSharedPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
       val random = rand(1000,9999).toString()
        registerbtn = findViewById<Button>(R.id.signup_btn)
       mainIntent = Intent(this, ComfirmAccountActivity::class.java)
        registerbtn.setOnClickListener {
            val firstname = findViewById<EditText>(R.id.firstNameEditText)
            val lastname = findViewById<EditText>(R.id.lastNameEditText)
            val email = findViewById<EditText>(R.id.userEmailEditText)
            val password = findViewById<EditText>(R.id.passwordEditText)
            val verifPass = findViewById<EditText>(R.id.repeatPassEditText)

            if (validateRegister(firstname,lastname, email, password,verifPass)) {
                register(
                    firstname.text.toString().trim(),
                    lastname.text.toString().trim(),
                    email.text.toString().trim(),
                    password.text.toString().trim(),
                    random
                )}
        }

       // Redirect To Login Screen

       val loginrBtn = findViewById<TextView>(R.id.login_link)
       loginrBtn.setOnClickListener {
           val intent = Intent(this, LoginActivity::class.java)
           startActivity(intent)
       }

    }

    private fun validateRegister(firstname: EditText,lastname: EditText, email: EditText, password: EditText, verifPass: EditText): Boolean {
        if (firstname.text.trim().isEmpty() || lastname.text.trim().isEmpty() || email.text.trim().isEmpty() ||  password.text.trim().isEmpty() || verifPass.text.trim().isEmpty()) {


            if (email.text.isEmpty()) {
                email.error = "Email is required"
                email.requestFocus()

            }


            if (verifPass.text.isEmpty()) {
                verifPass.error = "Password does not match"
                verifPass.requestFocus()

            }

            if (password.text.isEmpty()) {
                password.error = "Password is required"
                password.requestFocus()

            }

            if (firstname.text.isEmpty()) {
                firstname.error = "first name is required"
                firstname.requestFocus()

            }
            if (lastname.text.isEmpty()) {
                lastname.error = "last name is required"
                lastname.requestFocus()

            }

            return false
        }

        //Patterns // Regex // Length
        if (password.text.length < 6){
            password.error = "Password must be at least 6 characters"
            password.requestFocus()
            return false
        }

        if (password.text.toString() != verifPass.text.toString()){
            verifPass.error = "Password does not match"
            verifPass.requestFocus()
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.text).matches()){
            email.error = "Email unvalid"
            email.requestFocus()
            return false
        }
        if (firstname.text.length < 3){
            firstname.error = "firstname must be at least 3 characters"
            firstname.requestFocus()
            return false
        }
        if (lastname.text.length < 3){
            lastname.error = "lastname must be at least 3 characters"
            lastname.requestFocus()
            return false
        }

       /* if (Pattern.compile("^[a-zA-Z ]+$").matcher(firstname.text).matches()){
            firstname.error = "Invalid First Name"
            firstname.requestFocus()
            return false
        }
        if (Pattern.compile("^[a-zA-Z ]+$").matcher(lastname.text).matches()){
            lastname.error = "Invalid Last Name"
            lastname.requestFocus()
            return false
        }
*/

        return true
    }
    fun rand(start: Int, end: Int): Int {
        require(start <= end) { "Illegal Argument" }
        return (start..end).random()
    }


    private fun register(firstName: String, lastName: String, email: String, password: String,random:String) {
        val apiInterface = RetrofitInstance.getRetrofitInstance().create(RestApiService::class.java)



        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        val jsonParams: MutableMap<String?, Any?> = ArrayMap()
//put something inside the map, could be null
//put something inside the map, could be null

        jsonParams["firstName"] = firstName
        jsonParams["lastName"] = lastName
        jsonParams["password"] = password

        jsonParams["email"] = email
        jsonParams["activationCode"] =random


        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            JSONObject(jsonParams).toString()
        )

        apiInterface.SignIn(body).enqueue(object : Callback<User> {

            override fun onResponse(call: Call<User>, response: Response<User>) {

                val user = response.body()

                if (user != null){
                    Toast.makeText(this@RegisterActivity, "Sign Up Success", Toast.LENGTH_SHORT).show()
                    Log.d("user",user.toString())

                    val json = gson.toJson(user)
                    print("////////////////////////////////////////////////")
                    Log.d("json",json.toString())
                    mSharedPref.edit().apply {
                        putString(myuser, json)
                        putString("code", random)
                    }.apply()

                    startActivity(mainIntent)
                    finish()
                }else{
                    Toast.makeText(this@RegisterActivity, "can not sign up", Toast.LENGTH_SHORT).show()
                }


                window.clearFlags( WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, t.message, Toast.LENGTH_SHORT).show()


                window.clearFlags( WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }

        })


    }


}