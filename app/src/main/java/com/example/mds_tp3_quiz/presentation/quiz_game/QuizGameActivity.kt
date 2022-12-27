package com.example.mds_tp3_quiz.presentation.quiz_game

import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.mds_tp3_quiz.R
import com.example.mds_tp3_quiz.game.QuizGame
import com.example.mds_tp3_quiz.model.Quiz
import com.example.mds_tp3_quiz.presentation.quiz_game.fragments.GameOverFragment
import com.example.mds_tp3_quiz.presentation.quiz_game.fragments.InformationFragment
import com.example.mds_tp3_quiz.presentation.quiz_game.fragments.QuestionFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_quiz_game.*

class QuizGameActivity : AppCompatActivity(), OnGameReadyListener {
    private val quizGame: QuizGame = QuizGame(this, this)

    private lateinit var informationFragment: InformationFragment
    private lateinit var questionFragment: QuestionFragment
    private lateinit var gameOverFragment: GameOverFragment
    private var paused: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_game)

        addListeners()
        quizGame.start()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { return }
        })
    }

    override fun onResume() {
        super.onResume()

        game_watch.base = SystemClock.elapsedRealtime() - paused
        game_watch.start()
    }

    override fun onPause() {
        super.onPause()

        paused = SystemClock.elapsedRealtime() - game_watch.base
    }

    private fun addListeners(){
        btn_quit.setOnClickListener {
            finish()
        }
    }

    private fun showInformationFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, informationFragment)
        transaction.commit()
    }

    private fun showQuestionFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, questionFragment)
        transaction.commit()
    }

    private fun showGameOverFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, gameOverFragment)
        transaction.commit()
    }

    fun getNextQuiz(): Quiz {
        return quizGame.getNextQuestion()
    }

    fun onProceedButtonClick() {
        showQuestionFragment()
    }

    override fun onGameReady() {
        informationFragment = InformationFragment()
        questionFragment = QuestionFragment()
        gameOverFragment = GameOverFragment()

        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, informationFragment)
            .commit()
    }

    fun submitAnswer(answer: String): Boolean {
        return quizGame.submitAnswer(answer)
    }

    fun getCurrentQuiz(): Quiz {
        return quizGame.getCurrentQuiz()
    }

    fun getCurrentRound(): String {
        return quizGame.getCurrentRound()
    }

    fun getScore(): Int {
        return quizGame.getScore()
    }

    fun checkMatchFinish(){
        if (quizGame.isFinished()) {
            game_watch.stop()
            showGameOverFragment()
            updateUserPoints()
        } else {
            showInformationFragment()
        }
    }

    private fun updateUserPoints() {
        val db = Firebase.firestore

        db.collection("Users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if (document.get("email").toString() == FirebaseAuth.getInstance().currentUser?.email) {
                        val id = document.id
                        val newPoints =  (quizGame.getScore() * 10) + document.getLong("globalPoints")!!
                        val totalMatches = document.getLong("totalMatches")!! + 1
                        val data = hashMapOf(
                            "globalPoints" to newPoints,
                            "totalMatches" to totalMatches
                        )
                        db.collection("Users").document(id)
                            .set(data, SetOptions.merge())
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error updating points", Toast.LENGTH_SHORT).show()
            }
    }
}