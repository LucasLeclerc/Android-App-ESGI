package com.example.pour_comprendre.tools

import java.util.regex.Pattern

class Tools {
    public fun isValidEmail(email: String): Boolean {
        val pattern = Pattern.compile(
            "[a-zA-Z0-9+._%-+]{1,256}" +
                    "@" +
                    "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" +
                    "(" +
                    "." +
                    "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" +
                    ")+"
        )
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }
}