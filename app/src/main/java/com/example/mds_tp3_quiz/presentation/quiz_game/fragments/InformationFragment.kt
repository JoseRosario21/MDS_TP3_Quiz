package com.example.mds_tp3_quiz.presentation.quiz_game.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mds_tp3_quiz.R
import com.example.mds_tp3_quiz.model.Quiz
import com.example.mds_tp3_quiz.presentation.quiz_game.QuizGameActivity
import kotlinx.android.synthetic.main.fragment_information.*


class InformationFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_information, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addListeners()
        loadQuiz((activity as QuizGameActivity).getNextQuiz())
    }

    private fun addListeners() {
        btn_proceed.setOnClickListener {
            (activity as QuizGameActivity).onProceedButtonClick()
        }
    }

    fun loadQuiz(quiz: Quiz) {
        val formattedString = quiz.quizInfo.replace(". ", ".\n")
        tv_quiz_info.text = formattedString
    }
}