package com.example.booky.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.booky.R
import com.example.booky.data.api.RestApiService
import com.example.booky.data.api.RetrofitInstance
import com.example.booky.data.models.Book
import com.example.booky.data.models.BookList
import com.example.booky.data.models.User
import com.example.booky.ui.adapter.BookAdapter
import com.example.booky.ui.adapter.BookAdapterMyList
import com.example.booky.ui.view.PREF_NAME
import com.example.booky.ui.view.myuser
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 * Use the [MyBookFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyBookFragment : Fragment() {

    lateinit var recylcerChampion: RecyclerView
    lateinit var recylcerChampionAdapter: BookAdapterMyList
    var champList : MutableList<BookList> = ArrayList()
    var champList2 : MutableList<BookList> = ArrayList()
    lateinit  var recherche : TextView
    lateinit var nowuser: User
    private lateinit var mSharedPref: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)



        recylcerChampion = rootView.findViewById(R.id.listView)
        recherche = rootView.findViewById(R.id.recherche)
         mSharedPref  = this.requireActivity()?.getSharedPreferences(PREF_NAME, AppCompatActivity.MODE_PRIVATE)!!

        val gson = Gson()
        val  us =  mSharedPref.getString(myuser, "")

        nowuser = gson.fromJson(us,User::class.java)

        doLogin()
        var text=""

        recherche.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) { }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


                champList2 =     champList.filter { book ->  book.title.contains(s.toString())  } as MutableList<BookList>
             if(champList2.size == 0){
                 champList2 = champList.filter { book ->  book.description.contains(s.toString())  } as MutableList<BookList>
             }
                recylcerChampionAdapter = BookAdapterMyList(ArrayList(champList2.asReversed()))
                recylcerChampion.adapter = recylcerChampionAdapter
            }
        })

        recylcerChampionAdapter = BookAdapterMyList(champList)
        recylcerChampion.adapter = recylcerChampionAdapter
        recylcerChampion.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)


        return rootView
    }
    private fun doLogin(){

        val apiInterface = RetrofitInstance.getRetrofitInstance().create(RestApiService::class.java)



        apiInterface.AllBooks().enqueue(object : Callback<MutableList<BookList>> {

            override fun onResponse(call: Call<MutableList<BookList>>, response: Response<MutableList<BookList>>) {

                val user = response.body()

                if (user != null) {
                    champList=ArrayList(user.asReversed().filter { book -> book.userId.id==nowuser.id })

                    recylcerChampionAdapter =
                        BookAdapterMyList(champList)
                    recylcerChampion.adapter = recylcerChampionAdapter

                }



            }

            override fun onFailure(call: Call<MutableList<BookList>>, t: Throwable) {

            }

        })


    }
}