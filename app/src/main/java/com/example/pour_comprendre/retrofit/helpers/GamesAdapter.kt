package com.example.pour_comprendre.retrofit.helpers

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pour_comprendre.ConnectionActivity
import com.example.pour_comprendre.DetailsActivity
import com.example.pour_comprendre.R
import com.example.pour_comprendre.retrofit.models.Game
import com.google.android.material.button.MaterialButton
import com.squareup.picasso.Picasso
import java.security.AccessController

class GamesAdapter(private val games: Array<Game>) : RecyclerView.Adapter<GamesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view  = LayoutInflater.from(parent.context).inflate(R.layout.result_search,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return games.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        return viewHolder.bind(games[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView = itemView.findViewById<TextView>(R.id.title_game)
        val editorTextView = itemView.findViewById<TextView>(R.id.editor_game)
        val pictureImageView = itemView.findViewById<ImageView>(R.id.picture_game_jacket)
        val priceTextView = itemView.findViewById<TextView>(R.id.price_game)
        val backgroundImage = itemView.findViewById<ImageView>(R.id.background)

        fun bind(game: Game) {
            val name = game.game_name
            val editor = game.game_publishers
            val picture = game.game_picture
            val background = game.game_background
            val price = game.game_price

            nameTextView.text = name
            priceTextView.text = price
            editorTextView.text = editor
            Picasso.get().load(picture).resize(240, 300).into(pictureImageView)
            Picasso.get().load(background).resize(660, 300).into(backgroundImage)
        }

        init {
            val to_know_more = itemView.findViewById<MaterialButton>(R.id.to_know_more)
            to_know_more.setOnClickListener {
                val intent = Intent(itemView.context, DetailsActivity::class.java)
                intent.putExtra("game_id", games[adapterPosition].game_id.toString())
                itemView.context.startActivity(intent)
            }
        }
    }

}