package com.example.pour_comprendre

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.pour_comprendre.retrofit.models.User
import com.example.pour_comprendre.tools.Tools
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONObject

class ConnectionActivity : AppCompatActivity() {
    private val url = "http://141.94.245.122:3000/users/login"
    private val tools = Tools()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connection)
        supportActionBar?.hide()
        val intent = intent
        val str = intent.getStringExtra("mail_user")
        findViewById<TextView>(R.id.email).text = str
    }

    fun go_to_home(view: View) {
        val email = findViewById<TextView>(R.id.email)
        val pwd = findViewById<TextView>(R.id.password)

        if(!tools.isValidEmail(email.text.toString())) {
            Toast.makeText(applicationContext, R.string.email_not_valid, Toast.LENGTH_LONG).show()
        } else {
            if(email.text.toString().isNotEmpty() && pwd.text.toString().isNotEmpty()) {
                apiCallConnection()
            }
        }
    }

    fun go_home() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    fun go_to_register(view: View) {
        startActivity(Intent(this, NewAccountActivity::class.java))
        finish()
    }

    fun go_to_forgotten_password(view: View) {
        startActivity(Intent(this, ForgottenPasswordActivity::class.java))
        finish()
    }

    private val ioScope = CoroutineScope(Dispatchers.IO + Job() )
    private fun apiCallConnection() {
        ioScope.launch {
            val requestQueue = Volley.newRequestQueue(applicationContext)
            val mail: String = findViewById<TextView>(R.id.email).text.toString().trim()
            val password: String = findViewById<TextView>(R.id.password).text.toString().trim()

            val stringRequest = object : StringRequest(
                Method.POST, url,
                { response ->
                    val user = JSONObject(response)
                    User.id = user.getJSONObject("user").getString("id").toString().toInt()
                    go_home()
                    requestQueue.stop()
                },
             { error ->
                 if(error.networkResponse.statusCode == 403) {
                     Toast.makeText(applicationContext, R.string.password_invalid, Toast.LENGTH_LONG).show()
                 }
                requestQueue.stop()
            }) {
                override fun getBody(): ByteArray {
                    val params = HashMap<String, String>()
                    params["mail"] = mail
                    params["pwd"] = password
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