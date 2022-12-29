package com.example.mds_tp3_quiz.presentation.navigation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mds_tp3_quiz.R
import com.example.mds_tp3_quiz.model.User
import com.example.mds_tp3_quiz.presentation.navigation.fragments.adapter.LeaderboardAdapter
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_ranking.*

class RankingFragment : Fragment() {
    private val userList = arrayListOf<User>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ranking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rank_loading_animation.visibility = View.VISIBLE
        loadLeaderboard()
    }

    override fun onResume() {
        super.onResume()
        loadLeaderboard()
    }

    private fun loadLeaderboard() {
        val db = Firebase.firestore

        db.collection("Users")
            .get()
            .addOnSuccessListener { result ->
                userList.clear()
                for (document in result) {
                    userList.add(getUserFromDB(document))
                }
                rank_loading_animation.visibility = View.GONE
                val sortedPlayers = userList.sortedWith(compareByDescending { it.globalPoints })
                val layoutManager = LinearLayoutManager(requireContext())
                val adapter = LeaderboardAdapter(requireContext())
                adapter.setLeaderboard(sortedPlayers)
                rv_leaderboard.layoutManager = layoutManager
                rv_leaderboard.adapter = adapter
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error obtaining leaderboard", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getUserFromDB(document: QueryDocumentSnapshot): User {
        val id = document.get("id").toString()
        val username = document.get("username").toString()
        val email = document.get("email").toString()
        val points = document.getLong("globalPoints")?.toInt() ?: 0
        val totalMatches = document.getLong("totalMatches")?.toInt() ?: 0
        val level = document.getLong("level")?.toInt() ?: 1
        val currentExp = document.getLong("currentExp")?.toInt() ?: 0

        return User(id, username, email, points, totalMatches, level, currentExp)
    }
}