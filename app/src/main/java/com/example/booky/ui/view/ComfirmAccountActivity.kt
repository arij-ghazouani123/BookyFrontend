package com.example.booky.ui.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.ArrayMap
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.booky.MainActivity
import com.example.booky.R
import com.example.booky.data.api.RestApiService
import com.example.booky.data.api.RetrofitInstance
import com.example.booky.data.models.User
import com.example.booky.utils.LoadingDialog
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ComfirmAccountActivity : AppCompatActivity() {
    lateinit var userConfirmationCodeLayout: TextInputLayout;
    lateinit var userConfirmationCodeEditText: EditText;
    lateinit var loadingDialog: LoadingDialog
    lateinit var mSharedPref: SharedPreferences
    lateinit var submitBtn : Button
    lateinit var nowuser: User
    lateinit var mainIntent : Intent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comfirm_account)
        mSharedPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        loadingDialog = LoadingDialog(this)
        userConfirmationCodeLayout = findViewById(R.id.confirmationCode_tfLayout)
        userConfirmationCodeEditText = findViewById(R.id.ConfirmationCodeEditText)
        submitBtn = findViewById<Button>(R.id.submit_btn)
        mainIntent = Intent(this, MainActivity::class.java)
        submitBtn.setOnClickListener{

            if(validateInput(userConfirmationCodeEditText)){
                if (userConfirmationCodeEditText.text.toString()!=mSharedPref.getString("code","")) {
                    userConfirmationCodeEditText!!.error = "code doesn't match"
                    return@setOnClickListener
                }
                login()

            }

        }
    }


    private fun  login(){
        val gson = Gson()
        val  us =  mSharedPref.getString(myuser, "")

        nowuser = gson.fromJson(us, User::class.java)
        val apiInterface = RetrofitInstance.getRetrofitInstance().create(RestApiService::class.java)


        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        val jsonParams: MutableMap<String?, Any?> = ArrayMap()
//put something inside the map, could be null
//put something inside the map, could be null
        jsonParams["_id"] = nowuser.id
        jsonParams["verified"] = "true"

        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            JSONObject(jsonParams).toString()
        )

        apiInterface.updateVerified(body).enqueue(object : Callback<User> {

            override fun onResponse(call: Call<User>, response: Response<User>) {

                val user = response.body()



                if (user != null){
                    Toast.makeText(this@ComfirmAccountActivity, "Verified Success", Toast.LENGTH_SHORT).show()


                    Log.d("user",user.toString())

                    val json = gson.toJson(user)
                    print("////////////////////////////////////////////////")
                    Log.d("json",json.toString())
                    mSharedPref.edit().apply {
                        putString(myuser, json)
                        putString(USER_ID,user.id)
                        putString("code", user.activationCode)
                    }.apply()




                    startActivity(mainIntent)
                    finish()

                }else{
                    Toast.makeText(this@ComfirmAccountActivity, "Can not Verifie", Toast.LENGTH_SHORT).show()
                }


                window.clearFlags( WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@ComfirmAccountActivity, t.message, Toast.LENGTH_SHORT).show()


                window.clearFlags( WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }

        })


    }




    private fun validateInput(code: EditText): Boolean {
        if(code.text.trim().isEmpty()){

            if (code.text.isEmpty()) {
                code.error = "This field is required"
                code.requestFocus()

            }

            return false

        }

        return true
    }
}