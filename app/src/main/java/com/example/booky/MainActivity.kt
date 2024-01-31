package com.example.booky

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.booky.databinding.ActivityComfirmAccountBinding
import com.example.booky.databinding.ActivityMainBinding
import com.example.booky.ui.fragments.*
import com.example.booky.ui.view.AddBook
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {


    var binding: ActivityMainBinding? = null


    lateinit var btnadd: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(
            layoutInflater
        )






        setContentView(binding!!.root)
        replaceFragment(HomeFragment())
        binding!!.bottomNavigationView.background = null
        binding!!.bottomNavigationView.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.home -> replaceFragment(HomeFragment())
                R.id.map -> replaceFragment(MessagesFragment())
                R.id.chat -> replaceFragment(MyBookFragment())
                R.id.profil -> replaceFragment(SettingsFragment())
            }
            true
        }
        binding!!.fab.setOnClickListener { showBottomDialog() }
        btnadd = findViewById(R.id.fab)
        btnadd.setOnClickListener{
            val mainIntent = Intent(this, AddBook::class.java)
            startActivity(mainIntent)

        }

    }


    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    private fun showBottomDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottomsheetlayout)

        val cancelButton = dialog.findViewById<ImageView>(R.id.cancelButton)

        cancelButton.setOnClickListener { dialog.dismiss() }
        dialog.show()
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setGravity(Gravity.BOTTOM)
    }


}

