package com.example.mds_tp3_quiz.presentation.navigation.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.mds_tp3_quiz.R
import com.example.mds_tp3_quiz.databinding.FragmentHomeBinding
import com.example.mds_tp3_quiz.presentation.quiz_game.QuizGameActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        auth = Firebase.auth

        if(auth.currentUser != null) {

            val name: String = showDetails(auth.currentUser)
            binding.homeWelcomeBack.text = String.format(getString(R.string.home_welcomeText), name)
        }

        binding.homeNewGame.setOnClickListener {
            startActivity(Intent(this@HomeFragment.requireContext(), QuizGameActivity::class.java))
        }

        return binding.root
    }

    private fun showDetails(user: FirebaseUser?): String{

        return when (user) {
            null -> "No Authenticated user"
            else -> "${user.displayName}"
        }
    }
}