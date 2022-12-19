package com.example.mds_tp3_quiz.presentation.quiz_game.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mds_tp3_quiz.R
import com.example.mds_tp3_quiz.presentation.quiz_game.QuizGameActivity
import kotlinx.android.synthetic.main.fragment_first.*


class FirstFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onResume() {
        super.onResume()
        addListeners()

        getNextQuiz()
    }

    private fun addListeners() {
        btn_proceed.setOnClickListener {
            (activity as QuizGameActivity).onProceedButtonClick()
        }
    }

    private fun getNextQuiz() {
        val currentQuiz = (activity as QuizGameActivity).getNextQuiz()
        tv_quiz_info.text = currentQuiz.question
    }
}