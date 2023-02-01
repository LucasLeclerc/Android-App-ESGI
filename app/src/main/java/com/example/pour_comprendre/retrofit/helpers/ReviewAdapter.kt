package com.example.pour_comprendre.retrofit.helpers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pour_comprendre.R
import com.example.pour_comprendre.retrofit.models.Review

class ReviewAdapter(private val reviews: Array<Review>) : RecyclerView.Adapter<ReviewAdapter.ViewHolder>()  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewAdapter.ViewHolder {
        val view  = LayoutInflater.from(parent.context).inflate(R.layout.notice,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        return viewHolder.bind(reviews[position])
    }

    override fun getItemCount(): Int {
        return reviews.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username = itemView.findViewById<TextView>(R.id.username)
        val notice_tv = itemView.findViewById<TextView>(R.id.notice)

        fun bind(review: Review) {
            val user_id = review.user_id
            val notice = review.notice

            username.text = user_id
            notice_tv.text = notice
        }
    }
}