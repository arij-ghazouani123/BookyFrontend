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
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

  /*  private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 100*/


    lateinit var userEmailLayout: TextInputLayout;
    lateinit var passwordLayout: TextInputLayout;
    lateinit var userEmailEditText: EditText;
    lateinit var passwordEditText: EditText;
    lateinit var remember: CheckBox;
    lateinit var loadingDialog: LoadingDialog
    var gson = Gson()
    lateinit var mainIntent: Intent
    lateinit var mainIntent2: Intent
    lateinit var mSharedPref: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mSharedPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);




    /*    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        // Set the dimensions of the sign-in button.
        val signInButton = findViewById<SignInButton>(R.id.sign_in_button)
        signInButton.setSize(SignInButton.SIZE_STANDARD)

        signInButton.setOnClickListener {
            signIn()
        }
*/




        if (mSharedPref.getBoolean(IS_REMEMBRED, false)) {
            val mainIntent = Intent(this, MainActivity::class.java)

            startActivity(mainIntent)
            finish()
        }




        loadingDialog = LoadingDialog(this)
        userEmailLayout = findViewById(R.id.userEmail_tfLayout)
        passwordLayout = findViewById(R.id.password_tfLayout)
        userEmailEditText = findViewById(R.id.userEmailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        remember = findViewById(R.id.rememberMe_ckbx)


        // Redirect To Register Screen

        val registerBtn = findViewById<TextView>(R.id.register_link)
        registerBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }


        // Redirect To New Activation Code Screen
        val forgetpassBtn = findViewById<TextView>(R.id.forgetpass_link)
        forgetpassBtn.setOnClickListener {
            val intent2 = Intent(this, ResetPassEmailActivity::class.java)
            startActivity(intent2)
        }


        // Login Button Handler

        val loginBtn = findViewById<Button>(R.id.login_btn)
        loginBtn.setOnClickListener() {
            if (validateLogin(userEmailEditText, passwordEditText, passwordLayout)) {
                if (remember.isChecked) {
                    mSharedPref.edit().apply {
                        putBoolean(IS_REMEMBRED, remember.isChecked)
                    }.apply()
                }
                mainIntent = Intent(this, MainActivity::class.java)
                mainIntent2 = Intent(this, ComfirmAccountActivity::class.java)
                login(
                    userEmailEditText.text.trim().toString(),
                    passwordEditText.text.trim().toString()
                )
                //remember.setOnCheckedChangeListener(new


            } else {
                val toast = Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT)
                toast.show()
            }

        }


    }

    // Validate User Input
    private fun validateLogin(
        username: EditText,
        password: EditText,
        passwordlayout: TextInputLayout
    ): Boolean {
        if (username.text.trim().isEmpty() || password.text.trim().isEmpty()) {

            if (password.text.isEmpty()) {
                password.error = "Password is required"
                password.requestFocus()

            }
            // made it revesed so it desplays correctly you ll see it in the app
            if (username.text.isEmpty()) {
                username.error = "Username is required"
                username.requestFocus()

            }


            return false

        }

        return true
    }


    private fun login(email: String, password: String) {

        val apiInterface = RetrofitInstance.getRetrofitInstance().create(RestApiService::class.java)


        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        val jsonParams: MutableMap<String?, Any?> = ArrayMap()
//put something inside the map, could be null
//put something inside the map, could be null
        jsonParams["email"] = email
        jsonParams["password"] = password

        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            JSONObject(jsonParams).toString()
        )

        apiInterface.seConnecter(body).enqueue(object : Callback<User> {

            override fun onResponse(call: Call<User>, response: Response<User>) {

                val user = response.body()



                if (user != null) {
                    Toast.makeText(this@LoginActivity, "Login Success", Toast.LENGTH_SHORT).show()



                    val json = gson.toJson(user)

                    mSharedPref.edit().apply {
                        putString(myuser, json)
                        putString(USER_ID, user.id)
                        putString("code", user.activationCode)
                    }.apply()

                    if (user.verified == "false") {
                        startActivity(mainIntent2)
                        finish()
                    } else {
                        startActivity(mainIntent)
                        finish()
                    }

                } else {
                    Toast.makeText(this@LoginActivity, "User not found", Toast.LENGTH_SHORT).show()
                }


                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_SHORT).show()


                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }

        })


    }







   /* private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }


    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val acct = GoogleSignIn.getLastSignedInAccount(this)
            if (acct != null) {
                val personName = acct.displayName
                val personGivenName = acct.givenName
                val personFamilyName = acct.familyName
                val personEmail = acct.email
                val personId = acct.id
                val personPhoto = acct.photoUrl
            }

            startActivity(Intent(this@LoginActivity, GoogleLogin::class.java))


        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d("message", e.toString())
        }
    }*/

}