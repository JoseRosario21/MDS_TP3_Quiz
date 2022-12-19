package com.example.mds_tp3_quiz.presentation.quiz_game

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mds_tp3_quiz.R
import com.example.mds_tp3_quiz.game.QuizGame
import com.example.mds_tp3_quiz.presentation.quiz_game.fragments.FirstFragment
import com.example.mds_tp3_quiz.presentation.quiz_game.fragments.QuestionFragment

class QuizGameActivity : AppCompatActivity() {
    lateinit var quizGame: QuizGame

    private val firstFragment = FirstFragment()
    private val questionFragment = QuestionFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_game)

        quizGame = ViewModelProvider(this).get(QuizGame::class.java)

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
        transaction.replace(R.id.fragment_container, questionFragment)
        transaction.commit()
    }

    fun onProceedButtonClick() {
        showSecondFragment()
    }
}