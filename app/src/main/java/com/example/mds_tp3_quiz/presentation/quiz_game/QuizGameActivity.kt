package com.example.mds_tp3_quiz.presentation.quiz_game

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mds_tp3_quiz.R
import com.example.mds_tp3_quiz.game.QuizGame
import com.example.mds_tp3_quiz.presentation.quiz_game.fragments.FirstFragment
import com.example.mds_tp3_quiz.presentation.quiz_game.fragments.SecondFragment

class QuizGameActivity : AppCompatActivity() {
    private val quizGame: QuizGame = QuizGame()

    private val firstFragment = FirstFragment()
    private val secondFragment = SecondFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_game)

        quizGame.start()

        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, FirstFragment())
            .commit()
    }

    private fun showFirstFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, firstFragment)
        transaction.commit()
    }

    private fun showSecondFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, secondFragment)
        transaction.commit()
    }

    fun onProceedButtonClick() {
        showSecondFragment()
    }
}