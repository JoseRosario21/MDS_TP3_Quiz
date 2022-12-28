package com.example.mds_tp3_quiz.presentation.navigation.fragments.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mds_tp3_quiz.R
import com.example.mds_tp3_quiz.model.User
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class LeaderboardAdapter(val context: Context) : RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {
    private lateinit var leaderboard: List<User>

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val positionTextView: TextView = view.findViewById(R.id.position)
        val nameTextView: TextView = view.findViewById(R.id.name)
        val scoreTextView: TextView = view.findViewById(R.id.score)
        val profilePicView: ImageView = view.findViewById(R.id.iv_profile_pic)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_leaderboard, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val player = leaderboard[position]
        holder.positionTextView.text = String.format("%dº", position + 1)
        holder.nameTextView.text = player.username
        holder.scoreTextView.text = player.globalPoints.toString()

        // Reference to an image file in Cloud Storage
        val storageReference = Firebase.storage.reference
        val imageRef = storageReference.child("images/" + player.id + "/profilePicture")

        Glide.with(context /* context */)
            .load(imageRef)
            .into(holder.profilePicView)
    }

    override fun getItemCount(): Int = leaderboard.size

    fun setLeaderboard(leaderboard: List<User>) {
        this.leaderboard = leaderboard
    }
}