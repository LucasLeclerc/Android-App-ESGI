package com.example.pour_comprendre.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.pour_comprendre.DetailsActivity
import com.example.pour_comprendre.ResultSearchActivity
import com.example.pour_comprendre.R
import com.example.pour_comprendre.databinding.FragmentHomeBinding
import com.example.pour_comprendre.retrofit.helpers.GamesAdapter
import com.example.pour_comprendre.retrofit.models.Game
import com.google.android.material.button.MaterialButton
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private lateinit var rvGames: RecyclerView
    private val url = "http://141.94.245.122:3000/games/top?lang="+ Locale.getDefault().language
    private lateinit var searchBar: EditText
    private lateinit var id_game_best_sells: Number

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        rvGames = view.findViewById(R.id.linear_game)
        searchBar = view.findViewById(R.id.search_bar)
        apiCallTopGames()

        searchBar.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH
                || actionId == EditorInfo.IME_ACTION_DONE
                || event.action == KeyEvent.ACTION_DOWN
                && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                val intent = Intent(activity, ResultSearchActivity::class.java)
                intent.putExtra("search", searchBar.text.toString())
                activity?.startActivity(intent)
                activity?.finish()
                return@OnEditorActionListener true
            }
            false
        })

        view.findViewById<MaterialButton>(R.id.know_more_best_sell).setOnClickListener {
            val intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra("game_id", id_game_best_sells.toString())
            startActivity(intent)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val ioScope = CoroutineScope(Dispatchers.IO + Job() )
    private fun apiCallTopGames() {
        ioScope.launch {
            val requestQueue = Volley.newRequestQueue(context)
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response -> // Do something with response string
                    val manager = LinearLayoutManager(context)
                    rvGames.layoutManager = manager
                    rvGames.setHasFixedSize(true)
                    val game = JSONObject(response)
                    val games_array = game.getJSONArray("games")

                    var allGames = arrayOf<Game>()

                    id_game_best_sells = games_array.getJSONObject(0).getString("game_id").toInt()
                    val best_sells_pic = view?.findViewById<ImageView>(R.id.best_sells_pic)
                    val cur_game_sells = games_array.getJSONObject(0).getString("game_picture").toString()
                    Picasso.get().load(cur_game_sells).into(best_sells_pic);

                    for (i in 1 until games_array.length()) {
                        val gameFor = games_array.getJSONObject(i)
                        val game_id = gameFor.get("game_id").toString().toInt()
                        val game_name = gameFor.get("game_name").toString()
                        val game_publishers = gameFor.get("publishers").toString()
                        val game_picture = gameFor.get("game_picture").toString()
                        val game_background = gameFor.get("game_background").toString()
                        val game_price = gameFor.get("game_price").toString()
                        val game_promotion = gameFor.get("game_promotion").toString().toInt()

                        allGames += Game(game_id, game_name, game_publishers, game_picture, game_background, game_price, game_promotion)
                    }

                    val adapter = GamesAdapter(allGames)
                    rvGames.adapter = adapter
                    requestQueue.stop()
                }
            ) { error -> // Do something when get error
                requestQueue.stop()
            }
            requestQueue.add(stringRequest)
        }
    }
}
