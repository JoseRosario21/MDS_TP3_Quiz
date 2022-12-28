package com.example.mds_tp3_quiz.presentation.quiz_game.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mds_tp3_quiz.R
import com.example.mds_tp3_quiz.presentation.quiz_game.QuizGameActivity
import kotlinx.android.synthetic.main.fragment_game_over.*

class GameOverFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game_over, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupScores()
        addListeners()
    }

    private fun addListeners() {
        playAgain.setOnClickListener {
            (activity as QuizGameActivity).finishGameWithScore()
        }
    }

    private fun setupScores(){
        correctAnswers.text = String.format(getString(R.string.correct_answers), (activity as QuizGameActivity).getScore())
        pointsEarned.text = String.format(getString(R.string.points_earned), (activity as QuizGameActivity).getPointsEarned())
    }
}