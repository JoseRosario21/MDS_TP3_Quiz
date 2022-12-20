package com.example.mds_tp3_quiz.presentation.navigation.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mds_tp3_quiz.R
import com.example.mds_tp3_quiz.presentation.quiz_game.QuizGameActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onResume() {
        super.onResume()
        auth = FirebaseAuth.getInstance()

        addListeners()
        setupLayout()
    }

    private fun addListeners() {
        home_newGame.setOnClickListener {
            startActivity(Intent(this@HomeFragment.requireContext(), QuizGameActivity::class.java))
        }
    }

    private fun setupLayout() {
        val name = getUserIdentification(auth.currentUser)
        home_welcomeBack.text = String.format(getString(R.string.home_welcomeText), name)
        home_daysStrike.text = String.format(getString(R.string.home_daysStrike), 0)
        home_dailyPoints.text = String.format(getString(R.string.home_dailyPoints), 100)
    }

    private fun getUserIdentification(user: FirebaseUser?): String = when (user) {
            null -> "Anon_User"
            else -> "${user.displayName}"
    }
}