package com.example.mds_tp3_quiz.presentation.quiz_game

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mds_tp3_quiz.R
import com.example.mds_tp3_quiz.game.QuizGame
import com.example.mds_tp3_quiz.model.Quiz
import com.example.mds_tp3_quiz.presentation.quiz_game.fragments.FirstFragment
import com.example.mds_tp3_quiz.presentation.quiz_game.fragments.QuestionFragment

class QuizGameActivity : AppCompatActivity(), OnGameReadyListener {
    private val quizGame: QuizGame = QuizGame(this, this)

    private lateinit var firstFragment: FirstFragment
    private lateinit var questionFragment: QuestionFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_game)

        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, FirstFragment())
            .commit()

        quizGame.start()
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

    fun getNextQuiz(): Quiz {
        return quizGame.getNextQuestion()
    }

    fun onProceedButtonClick() {
        showSecondFragment()
    }

    override fun onGameReady() {
        firstFragment = FirstFragment()
        questionFragment = QuestionFragment()

        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, firstFragment)
            .commit()
    }

    fun submitAnswer(answer: String){
        quizGame.submitAnswer(answer)
    }
    fun nextQuestion(){
        quizGame.getNextQuestion()
    }
}