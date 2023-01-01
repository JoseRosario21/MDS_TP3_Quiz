package com.example.mds_tp3_quiz.presentation.navigation.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.mds_tp3_quiz.R
import com.example.mds_tp3_quiz.model.User
import com.example.mds_tp3_quiz.presentation.quiz_game.QuizGameActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {
    private lateinit var user: User
    private var level = 1
    private var exp = 0

    private val gameLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        run {
            if (result.resultCode == Activity.RESULT_OK) {
                val score = result.data?.getIntExtra("GAME_SCORE", 0)
                if (score != null) {
                    val progress = home_profileExpBar.progress + score

                    if (progress < 100) {

                        home_profileExpBar.progress += score
                        home_current_exp.text = String.format(getString(R.string.home_currentExp), 100 - home_profileExpBar.progress)
                        updateUserExp(home_profileExpBar.progress)
                    }
                    else {

                        level += progress / 100
                        home_profileExpBar.progress = progress % 100
                        home_current_exp.text = String.format(getString(R.string.home_leveled_up) + "\n" + getString(R.string.home_currentExp), 100 - home_profileExpBar.progress)
                        home_user_level.text = String.format(getString(R.string.home_currentLevel), level)
                        updateUserLevel(level, home_profileExpBar.progress)
                    }
                }
            }
        }
    }

    private fun updateUserExp(currentExp: Int) {
        val db = Firebase.firestore

        val data = hashMapOf(
            "currentExp" to currentExp
        )
        db.collection("Users").document(user.id)
            .set(data, SetOptions.merge())
    }

    private fun updateUserLevel(level: Int, currentExp: Int) {
        val db = Firebase.firestore

        val data = hashMapOf(
            "level" to level,
            "currentExp" to currentExp
        )
        db.collection("Users").document(user.id)
            .set(data, SetOptions.merge())
    }

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        addListeners()
        setupLayout()
    }

    private fun addListeners() {
        home_newGame.setOnClickListener {
            gameLauncher.launch(Intent(this@HomeFragment.requireContext(), QuizGameActivity::class.java))
        }
    }

    private fun setupLayout() {
        val name = getUserIdentification(auth.currentUser!!)

        val db = Firebase.firestore
        db.collection("Users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if (document.id == FirebaseAuth.getInstance().currentUser?.uid) {
                        user = getUserFromDB(document)

                        level = user.level
                        exp = user.currentExp

                        home_welcomeBack.text = String.format(getString(R.string.home_welcomeText), name)
                        home_user_level.text = String.format(getString(R.string.home_currentLevel), level)
                        home_current_exp.text = String.format(getString(R.string.home_currentExp), 100 - exp)
                        home_profileExpBar.progress = exp
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error obtaining leaderboard", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getUserIdentification(user: FirebaseUser): String = if (user.isAnonymous) "Anonymous user" else user.displayName ?: ""

    private fun getUserFromDB(document: DocumentSnapshot): User {
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