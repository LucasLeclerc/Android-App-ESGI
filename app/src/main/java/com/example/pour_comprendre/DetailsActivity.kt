package com.example.pour_comprendre

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.pour_comprendre.retrofit.helpers.ReviewAdapter
import com.example.pour_comprendre.retrofit.models.Review
import com.example.pour_comprendre.retrofit.models.User
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*

class DetailsActivity : AppCompatActivity() {
    private lateinit var id_game: Number
    private lateinit var rvNotice: RecyclerView
    private lateinit var btnDescription: MaterialButton
    private lateinit var btnReviews: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        rvNotice = findViewById(R.id.rv_notice)
        btnDescription = findViewById(R.id.description_btn)
        btnReviews = findViewById(R.id.reviews_btn)

        id_game = intent.getStringExtra("game_id")!!.toInt()
        apiCallGameIsInFavorite()
        apiCallGameIsInWishlist()
        apiCallGetDatasOfGame()
        supportActionBar?.hide()
    }

    fun goBackHome(view: View) {
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
    }

    fun see_reviews(view: View) {
        apiCallReviewsOfGame()
        findViewById<TextView>(R.id.description).visibility = View.GONE
        btnDescription.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(applicationContext, R.color.bg_color_global))
        btnDescription.setStrokeColorResource(R.color.buttons)
        btnDescription.strokeWidth = 6
        btnReviews.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(applicationContext, R.color.buttons))
    }

    fun see_description(view: View) {
        findViewById<TextView>(R.id.error_no_content).visibility = View.GONE
        findViewById<RecyclerView>(R.id.rv_notice).visibility = View.GONE
        findViewById<TextView>(R.id.description).visibility = View.VISIBLE
        btnReviews.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(applicationContext, R.color.bg_color_global))
        btnReviews.setStrokeColorResource(R.color.buttons)
        btnReviews.strokeWidth = 6
        btnDescription.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(applicationContext, R.color.buttons))
    }

    fun add_to_wishlist(view: View) {
        if (findViewById<ImageView>(R.id.star).tag == "fill") {
            Picasso.get().load(R.drawable.star_empty).into(findViewById<ImageView>(R.id.star))
            findViewById<ImageView>(R.id.star).tag = "not_fill"
            apiCallRemoveGameFromWishlist()
        } else {
            Picasso.get().load(R.drawable.star_fill).into(findViewById<ImageView>(R.id.star))
            findViewById<ImageView>(R.id.star).tag = "fill"
            apiCallAddGameInWishlist()
        }
    }

    fun add_to_favorite(view: View) {
        if (findViewById<ImageView>(R.id.heart).tag == "fill") {
            Picasso.get().load(R.drawable.heart_empty).into(findViewById<ImageView>(R.id.heart))
            findViewById<ImageView>(R.id.heart).tag = "not_fill"
            apiCallRemoveGameFromFavorite()
        } else {
            Picasso.get().load(R.drawable.heart_fill).into(findViewById<ImageView>(R.id.heart))
            findViewById<ImageView>(R.id.heart).tag = "fill"
            apiCallAddGameInFavorite()
        }
    }

    private var ioScope = CoroutineScope(Dispatchers.IO + Job() )
    private fun apiCallGameIsInFavorite() {
        ioScope.launch {
            val requestQueue = Volley.newRequestQueue(this@DetailsActivity)
            val stringRequest = StringRequest(
                Request.Method.GET, "http://141.94.245.122:3000/users/${User.id}/game/${id_game}/favorites",
                { response -> // Do something with response string
                    requestQueue.stop()
                }
            ) { error -> // Do something when get error
                if(error.message?.contains("Failed to connect") == true) {
                    val snackbar = Snackbar.make(findViewById(R.id.details), R.string.failed_to_connect_to_server, Snackbar.LENGTH_LONG)
                    snackbar.setTextColor(resources.getColor(R.color.white))
                    snackbar.setBackgroundTint(resources.getColor(R.color.red))
                    val view = snackbar.view
                    val params = view.layoutParams as FrameLayout.LayoutParams
                    params.gravity = Gravity.TOP
                    snackbar.view.layoutParams = params
                    snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
                    snackbar.show()
                } else {
                    if(error.networkResponse.statusCode == 409) {
                        Picasso.get().load(R.drawable.heart_fill).into(findViewById<ImageView>(R.id.heart))
                        findViewById<ImageView>(R.id.heart).tag = "fill"
                    }
                }

                requestQueue.stop()
            }
            requestQueue.add(stringRequest)
        }
    }

    private fun apiCallGameIsInWishlist() {
        ioScope.launch {
            val requestQueue = Volley.newRequestQueue(this@DetailsActivity)
            val stringRequest = StringRequest(
                Request.Method.GET, "http://141.94.245.122:3000/users/${User.id}/game/${id_game}/wishlist",
                { response -> // Do something with response string
                    requestQueue.stop()
                }
            ) { error -> // Do something when get error
                if(error.message?.contains("Failed to connect") == true) {
                    val snackbar = Snackbar.make(findViewById(R.id.details), R.string.failed_to_connect_to_server, Snackbar.LENGTH_LONG)
                    snackbar.setTextColor(resources.getColor(R.color.white))
                    snackbar.setBackgroundTint(resources.getColor(R.color.red))
                    val view = snackbar.view
                    val params = view.layoutParams as FrameLayout.LayoutParams
                    params.gravity = Gravity.TOP
                    snackbar.view.layoutParams = params
                    snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
                    snackbar.show()
                } else {
                    if(error.networkResponse.statusCode == 409) {
                        Picasso.get().load(R.drawable.star_fill).into(findViewById<ImageView>(R.id.star))
                        findViewById<ImageView>(R.id.star).tag = "fill"
                    }
                }

                requestQueue.stop()
            }
            requestQueue.add(stringRequest)
        }
    }

    private fun apiCallGetDatasOfGame() {
        ioScope.launch {
            val requestQueue = Volley.newRequestQueue(this@DetailsActivity)
            val stringRequest = StringRequest(
                Request.Method.GET, "https://store.steampowered.com/api/appdetails?lan=${Locale.getDefault().language}&appids=${id_game}",
                { response -> // Do something with response string

                    val title_game = findViewById<TextView>(R.id.title_game)
                    val editor_game = findViewById<TextView>(R.id.editor_game)
                    val picture_game_jacket = findViewById<ImageView>(R.id.picture_game_jacket)
                    val background = findViewById<ImageView>(R.id.background)
                    val description = findViewById<TextView>(R.id.description)
                    description.movementMethod = ScrollingMovementMethod()
                    description.movementMethod = LinkMovementMethod.getInstance()
                    val main_pic = findViewById<ImageView>(R.id.main_pic)

                    val game = JSONObject(response)
                    val games_array = game.getJSONObject(id_game.toString())
                    val games_array2 = games_array.getJSONObject("data")

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        description.text = Html.fromHtml(games_array2.get("detailed_description").toString(), Html.FROM_HTML_MODE_LEGACY);
                    } else {
                        description.text = Html.fromHtml(games_array2.get("detailed_description").toString());
                    }
                    Picasso.get().load(games_array2.get("header_image").toString()).into(main_pic)
                    editor_game.text = games_array2.getJSONArray("publishers").get(0).toString()
                    Picasso.get().load(games_array2.get("background").toString()).into(background)
                    title_game.text = games_array2.get("name").toString()
                    Picasso.get().load(games_array2.get("header_image").toString()).resize(240, 300).into(picture_game_jacket)


                    requestQueue.stop()
                }
            ) { error -> // Do something when get error
                Log.d("error : ",error.message.toString())
                if(error.message?.contains("Failed to connect") == true) {
                    val snackbar = Snackbar.make(findViewById(R.id.details), R.string.failed_to_connect_to_server, Snackbar.LENGTH_LONG)
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

    private fun apiCallReviewsOfGame() {
        ioScope.launch {
            val requestQueue = Volley.newRequestQueue(this@DetailsActivity)
            val stringRequest = StringRequest(
                Request.Method.GET, "https://store.steampowered.com/appreviews/${id_game}?json=1",
                { response -> // Do something with response string
                    val manager = LinearLayoutManager(applicationContext)
                    rvNotice.layoutManager = manager
                    rvNotice.setHasFixedSize(true)
                    val reviews = JSONObject(response)
                    val reviewsArray = reviews.getJSONArray("reviews")
                    var allReviews: Array<Review> = arrayOf()
                    if(reviewsArray.length() > 0) {
                        for (i in 0 until reviewsArray.length()) {
                            val reviewFor = reviewsArray.getJSONObject(i)
                            System.out.println(reviewFor.getJSONObject("author"))
                            val author_id = reviewFor.getJSONObject("author").getString("steamid")
                            val review = reviewFor.getString("review").toString()

                            allReviews += Review(author_id, review)
                        }
                        val adapter = ReviewAdapter(allReviews)
                        rvNotice.adapter = adapter
                        rvNotice.visibility = View.VISIBLE
                    } else {
                        findViewById<TextView>(R.id.error_no_content).visibility = View.VISIBLE
                    }
                    requestQueue.stop()
                }
            ) { error -> // Do something when get error
                Log.d("error : ",error.message.toString())
                if(error.message?.contains("Failed to connect") == true) {
                    val snackbar = Snackbar.make(findViewById(R.id.details), R.string.failed_to_connect_to_server, Snackbar.LENGTH_LONG)
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

    private fun apiCallAddGameInFavorite() {
        ioScope.launch {
            val requestQueue = Volley.newRequestQueue(applicationContext)
            val stringRequest = object : StringRequest(
                Method.POST, "http://141.94.245.122:3000/games/add_in_favorite",
                { response -> // Do something with response string
                    //System.out.println(response)
                    requestQueue.stop()
                },
                { error -> // Do something when get error
                    requestQueue.stop()
                }) {
                override fun getBody(): ByteArray {
                    val params = HashMap<String, String>()
                    params["game_id"] = id_game.toString()
                    params["user_id"] = User.id.toString()
                    return JSONObject(params as Map<*, *>?).toString().toByteArray()
                }

                override fun getBodyContentType(): String {
                    return "application/json"
                }
            }
            requestQueue.add(stringRequest)
        }
    }

    private fun apiCallRemoveGameFromFavorite() {
        ioScope.launch {
            val requestQueue = Volley.newRequestQueue(applicationContext)
            val stringRequest = object : StringRequest(
                Method.POST, "http://141.94.245.122:3000/games/remove_from_favorite",
                { response -> // Do something with response string
                    System.out.println(response)
                    apiCallGameIsInFavorite()
                    requestQueue.stop()
                },
                { error -> // Do something when get error
                    requestQueue.stop()
                }) {
                override fun getBody(): ByteArray {
                    val params = HashMap<String, String>()
                    System.out.println(id_game)
                    params["game_id"] = id_game.toString()
                    params["user_id"] = User.id.toString()
                    return JSONObject(params as Map<*, *>?).toString().toByteArray()
                }

                override fun getBodyContentType(): String {
                    return "application/json"
                }
            }
            requestQueue.add(stringRequest)
        }
    }

    private fun apiCallAddGameInWishlist() {
        ioScope.launch {
            val requestQueue = Volley.newRequestQueue(applicationContext)
            val stringRequest = object : StringRequest(
                Method.POST, "http://141.94.245.122:3000/games/add_in_wishlist",
                { response -> // Do something with response string
                    //System.out.println(response)
                    requestQueue.stop()
                },
                { error -> // Do something when get error
                    requestQueue.stop()
                }) {
                override fun getBody(): ByteArray {
                    val params = HashMap<String, String>()
                    params["game_id"] = id_game.toString()
                    params["user_id"] = User.id.toString()
                    return JSONObject(params as Map<*, *>?).toString().toByteArray()
                }

                override fun getBodyContentType(): String {
                    return "application/json"
                }
            }
            requestQueue.add(stringRequest)
        }
    }

    private fun apiCallRemoveGameFromWishlist() {
        ioScope.launch {
            val requestQueue = Volley.newRequestQueue(applicationContext)
            val stringRequest = object : StringRequest(
                Method.POST, "http://141.94.245.122:3000/games/remove_from_wishlist",
                { response -> // Do something with response string
                    System.out.println(response)
                    apiCallGameIsInWishlist()
                    requestQueue.stop()
                },
                { error -> // Do something when get error
                    requestQueue.stop()
                }) {
                override fun getBody(): ByteArray {
                    val params = HashMap<String, String>()
                    params["game_id"] = id_game.toString()
                    params["user_id"] = User.id.toString()
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