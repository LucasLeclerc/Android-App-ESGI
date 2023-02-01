package com.example.pour_comprendre

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

class ForgottenPasswordActivity : AppCompatActivity()  {
    private val url = "http://141.94.245.122:3000/users/reset_password"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgotten_password)
        supportActionBar?.hide()
    }

    fun reset_password(view: View) {
        val pwd = findViewById<TextView>(R.id.password)
        val email = findViewById<TextView>(R.id.email)

        if(email.text.toString().isNotEmpty() && pwd.text.toString().isNotEmpty()) {
            apiCallRegister()
            val intent = Intent(this, ConnectionActivity::class.java)
            intent.putExtra("mail_user", email.text.toString())
            startActivity(intent)
            finish()
        }
    }

    private val ioScope = CoroutineScope(Dispatchers.IO + Job() )
    private fun apiCallRegister() {
        ioScope.launch {
            val requestQueue = Volley.newRequestQueue(applicationContext)
            val mail: String = findViewById<TextView>(R.id.email).text.toString().trim()
            val password: String = findViewById<TextView>(R.id.password).text.toString().trim()
            val stringRequest: StringRequest = object : StringRequest( Method.POST, url,
                Response.Listener { response ->
                    Toast.makeText(applicationContext, response, Toast.LENGTH_LONG).show()
                    try {
                        val jsonObject = JSONObject(response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    Toast.makeText(applicationContext, error.toString(), Toast.LENGTH_LONG).show()
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