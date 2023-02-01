package com.example.pour_comprendre

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.pour_comprendre.databinding.ActivityMainBinding
import com.example.pour_comprendre.retrofit.helpers.GamesAdapter
import com.example.pour_comprendre.retrofit.models.Game
import kotlinx.coroutines.*
import org.json.JSONObject


class ResultSearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var url = "http://141.94.245.122:3000/games/search"
    private lateinit var search: EditText
    private lateinit var rvGames: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_result_search)

        val value = intent.getStringExtra("search")
        search = findViewById(R.id.search_bar)
        rvGames = findViewById(R.id.linear_game_research)

        search.setText(value.toString())
        apiCallSearchGames()

        search.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH
                || actionId == EditorInfo.IME_ACTION_DONE
                || event.action == KeyEvent.ACTION_DOWN
                && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                (applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                    currentFocus?.windowToken, 0)
                apiCallSearchGames()
                return@OnEditorActionListener true
            }
            false
        })
    }

    fun goBackHome(view: View) {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private val ioScope = CoroutineScope(Dispatchers.IO + Job() )
    private fun apiCallSearchGames() {
        ioScope.launch {
            val requestQueue = Volley.newRequestQueue(applicationContext)
            val stringRequest = object : StringRequest(
                Request.Method.POST, url,
                { response -> // Do something with response string
                    findViewById<ProgressBar>(R.id.progress_bar).visibility = View.VISIBLE
                    rvGames.layoutManager = LinearLayoutManager(applicationContext)
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

                    findViewById<TextView>(R.id.nb_results).text = getString(R.string.nb_result)+" "+games_array.length()

                    val adapter = GamesAdapter(allGames)
                    rvGames.adapter = adapter
                    requestQueue.stop()
                    findViewById<ProgressBar>(R.id.progress_bar).visibility = View.GONE
            },
            { error -> // Do something when get error
                requestQueue.stop()
            }) {
                override fun getBody(): ByteArray {
                    val params = HashMap<String, String>()
                    params["game_name"] = findViewById<EditText>(R.id.search_bar).text.toString()
                    return JSONObject(params as Map<*, *>?).toString().toByteArray()
                }

                override fun getBodyContentType(): String {
                    return "application/json"
                }
            }
            requestQueue.add(stringRequest)
        }
    }
}