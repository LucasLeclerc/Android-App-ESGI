package com.example.pour_comprendre.retrofit.models

data class Game (
    var game_id: Int?,
    var game_name: String?,
    var game_publishers: String,
    var game_picture: String,
    var game_background: String,
    var game_price: String,
    var game_promotion: Int
) {
    
}