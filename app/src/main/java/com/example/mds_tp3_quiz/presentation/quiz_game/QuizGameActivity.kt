package com.example.mds_tp3_quiz.presentation.quiz_game

import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
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
    private var seconds: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_game)

        addListeners()
        quizGame.start()
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

        game_watch.format = getString(R.string.ellapsed_time)
        game_watch.base = SystemClock.elapsedRealtime()
        game_watch.setOnChronometerTickListener {
            seconds++
        }
        game_watch.start()
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
                        val newPoints = getPointsEarned() + document.getLong("globalPoints")!!
                        val data = hashMapOf("globalPoints" to newPoints)
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

    fun getPointsEarned(): Int = when {
            seconds <= 60 -> {
                // Finished under a minute
                (quizGame.getScore() * 10) * 3
            }
            seconds <= 120 -> {
                // Finished between 1 and 2 minutes
                (quizGame.getScore() * 10) * 2
            }
            else -> {
                // Finished over 2 minutes
                quizGame.getScore() * 10
            }
    }
}