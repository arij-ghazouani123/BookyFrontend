package com.example.booky.ui.view

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.booky.R
import com.example.booky.data.api.RestApiService
import com.example.booky.data.api.RetrofitInstance
import com.example.booky.data.models.User
import com.example.booky.utils.LoadingDialog
import com.google.android.material.textfield.TextInputLayout
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback

class ConfirmAfterResetActivity : AppCompatActivity() {
    lateinit var userNewConfirmationCodeLayout: TextInputLayout;
    lateinit var userNewConfirmationCodeEditText: EditText;
    lateinit var loadingDialog: LoadingDialog
    lateinit var submitBtn : Button
    lateinit var mSharedPref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_after_reset)
        mSharedPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        loadingDialog = LoadingDialog(this)
        userNewConfirmationCodeLayout = findViewById(R.id.Code_tfLayout)
        userNewConfirmationCodeEditText = findViewById(R.id.CodeEditText)
        submitBtn = findViewById<Button>(R.id.Nsubmit_btn)
        submitBtn.setOnClickListener{

            if(validateInput(userNewConfirmationCodeEditText)){
                if (userNewConfirmationCodeEditText.text.toString()!=mSharedPref.getString("code","")) {
                    userNewConfirmationCodeEditText!!.error = "code doesn't match"
                    return@setOnClickListener
                }
                val mainIntent = Intent(this, ResetPasswordActivity::class.java)
                startActivity(mainIntent)
                finish()
            }

        }
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