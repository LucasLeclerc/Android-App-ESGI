package com.example.pour_comprendre.ui.favorites

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.pour_comprendre.R
import com.example.pour_comprendre.databinding.FragmentFavoritesBinding
import com.example.pour_comprendre.retrofit.helpers.GamesAdapter
import com.example.pour_comprendre.retrofit.models.Game
import com.example.pour_comprendre.retrofit.models.User
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONObject


class FavoritesFragment : Fragment() {
    private var _binding: FragmentFavoritesBinding? = null
    private lateinit var rvGames: RecyclerView
    private lateinit var imageView: ImageView
    private lateinit var textView: TextView
    private val url = "http://141.94.245.122:3000/users/games_favorite"
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view: View = inflater.inflate(R.layout.fragment_favorites, container, false)
        rvGames = view.findViewById(R.id.linear_game_favorites)
        imageView = view.findViewById(R.id.no_like)
        textView = view.findViewById(R.id.no_like_text)
        progressBar = view.findViewById(R.id.progress_bar)

        this.apiCallFavorite()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val ioScope = CoroutineScope(Dispatchers.IO + Job() )
    private fun apiCallFavorite() {
        ioScope.launch {
            val requestQueue = Volley.newRequestQueue(context)
            val stringRequest = StringRequest(
                Request.Method.GET, url+"?user_id="+User.id,
                { response -> // Do something with response string
                    val manager = LinearLayoutManager(context)
                    rvGames.layoutManager = manager
                    rvGames.setHasFixedSize(true)

                    val game = JSONObject(response)
                    val games_array = game.getJSONArray("games")

                    var allGames = arrayOf<Game>()

                    for (i in 0 until games_array.length()) {
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

                    if(allGames.isEmpty()) {
                        imageView.visibility = View.VISIBLE
                        textView.visibility = View.VISIBLE
                    } else {
                        imageView.visibility = View.INVISIBLE
                        textView.visibility = View.INVISIBLE
                    }
                    progressBar.visibility = View.GONE

                    requestQueue.stop()
                }
            ) { error -> // Do something when get error
                //Log.d("error : ",error.message.toString())
                imageView.visibility = View.VISIBLE
                textView.visibility = View.VISIBLE
                progressBar.visibility = View.GONE

                if(error.message?.contains("Failed to connect") == true) {
                    val snackbar = Snackbar.make(requireView(), R.string.failed_to_connect_to_server, Snackbar.LENGTH_LONG)
                    snackbar.setTextColor(resources.getColor(R.color.white))
                    snackbar.setBackgroundTint(resources.getColor(R.color.red))
                    val view = snackbar.view
                    val params = view.layoutParams as FrameLayout.LayoutParams
                    params.gravity = Gravity.TOP
                    snackbar.view.layoutParams = params
                    snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
                    snackbar.show()
                }

                requestQueue.stop()
            }
            requestQueue.add(stringRequest)
        }
    }
}