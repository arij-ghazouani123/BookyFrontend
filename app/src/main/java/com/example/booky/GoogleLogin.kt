package com.example.booky


import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
//import com.google.android.gms.auth.api.signin.GoogleSignIn
//import com.google.android.gms.auth.api.signin.GoogleSignInClient
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class GoogleLogin : AppCompatActivity() {

   /* lateinit var mGoogleSignInClient: GoogleSignInClient

    private lateinit var name : TextView
    private lateinit var email : TextView
    private lateinit var pic : ImageView
    private lateinit var signout : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_login)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        signout = findViewById(R.id.signout)

        signout.setOnClickListener {
            signOut()
        }

        val acct = GoogleSignIn.getLastSignedInAccount(this)
        if (acct != null) {
            val personName = acct.displayName
            val personEmail = acct.email
            val personPhoto = acct.photoUrl

            name.text = personName
            email.text = personEmail
            Glide.with(this).load(personPhoto).into(pic)


        }

        name = findViewById(R.id.name)
        email = findViewById(R.id.email)
        pic = findViewById(R.id.pic)

    }
    private fun signOut() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this) { task ->
                // ...
                Toast.makeText(this@GoogleLogin, "signout", Toast.LENGTH_SHORT).show()
                finish()
            }
    }
*/
}