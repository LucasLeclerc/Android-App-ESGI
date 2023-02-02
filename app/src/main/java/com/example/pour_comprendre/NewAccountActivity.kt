package com.example.pour_comprendre

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.pour_comprendre.tools.Tools
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

class NewAccountActivity : AppCompatActivity() {
    private val url = "http://141.94.245.122:3000/users/register"
    private val tools = Tools()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.hide()
    }

    fun check_if_password_are_equals(view: View) {
        val pwd = findViewById<TextView>(R.id.password)
        val pwd_check = findViewById<TextView>(R.id.check_password)
        val pwd_failed_icon = findViewById<ImageView>(R.id.fail_pass)
        val email = findViewById<TextView>(R.id.email)
        val username = findViewById<TextView>(R.id.username)

        if(email.text.toString().isNotEmpty() && pwd.text.toString().isNotEmpty() && pwd_check.text.toString().isNotEmpty() && username.text.toString().isNotEmpty()) {
            if(!tools.isValidEmail(email.text.toString())) {
                Toast.makeText(applicationContext, R.string.email_not_valid, Toast.LENGTH_LONG).show()
            }

            if (pwd.text.toString() != pwd_check.text.toString()) {
                pwd_failed_icon.visibility = View.VISIBLE
            } else {
                apiCallRegister()
                val intent = Intent(this, ConnectionActivity::class.java)
                intent.putExtra("mail_user", email.text.toString())
                startActivity(intent)
                finish()
            }
        }
    }

    private val ioScope = CoroutineScope(Dispatchers.IO + Job() )
    private fun apiCallRegister() {
        ioScope.launch {
            val requestQueue = Volley.newRequestQueue(applicationContext)
            val username: String = findViewById<TextView>(R.id.username).text.toString().trim()
            val mail: String = findViewById<TextView>(R.id.email).text.toString().trim()
            val password: String = findViewById<TextView>(R.id.password).text.toString().trim()
            val stringRequest: StringRequest = object : StringRequest( Method.POST, url,
                Response.Listener { response ->
                    Toast.makeText(applicationContext, response, Toast.LENGTH_LONG).show()
                },
                Response.ErrorListener { error ->
                    //Log.d("error", error.message.toString())
                    Toast.makeText(applicationContext, error.toString(), Toast.LENGTH_LONG).show()
                }) {
                override fun getBody(): ByteArray {
                    val params = HashMap<String, String>()
                    params["pseudo"] = username
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