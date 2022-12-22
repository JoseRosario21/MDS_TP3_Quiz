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
                    val sortedPlayers = userList.sortedWith(compareByDescending { it.globalPoints })
                    val layoutManager = LinearLayoutManager(requireContext())
                    val adapter = LeaderboardAdapter()
                    adapter.setLeaderboard(sortedPlayers)
                    rv_leaderboard.layoutManager = layoutManager
                    rv_leaderboard.adapter = adapter

                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error obtaining leaderboard", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getUserFromDB(document: QueryDocumentSnapshot): User {
        val username = document.get("username").toString()
        val email = document.get("email").toString()
        val points = document.getLong("globalPoints")?.toInt() ?: 0

        return User(username, email, points)
    }
}