package com.example.mds_tp3_quiz.presentation.quiz_game.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mds_tp3_quiz.R
import com.example.mds_tp3_quiz.model.Quiz
import com.example.mds_tp3_quiz.presentation.quiz_game.QuizGameActivity
import kotlinx.android.synthetic.main.fragment_question.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class QuestionFragment() : Fragment() {
    private lateinit var quiz: Quiz

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quiz = (activity as QuizGameActivity).getCurrentQuiz()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_question, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupQuiz()
        setupAnswerListener()
    }

    private fun setupQuiz(){
        question.text = quiz.question
        counter.text = (activity as QuizGameActivity).getCurrentRound()
        answer1.text = quiz.answerA
        answer2.text = quiz.answerB
        answer3.text = quiz.answerC
        answer4.text = quiz.answerD
    }

    private fun setupAnswerListener() {
        answer1.setOnClickListener {
            if(submitAnswer(answer1.text.toString())){
                answer1.setBackgroundColor(Color.GREEN)
            }else{
                answer1.setBackgroundColor(Color.RED)
            }
            checkMatchFinish()
        }
        answer2.setOnClickListener {
            if(submitAnswer(answer2.text.toString())){
                answer2.setBackgroundColor(Color.GREEN)
            }else{
                answer2.setBackgroundColor(Color.RED)
            }
            checkMatchFinish()
        }
        answer3.setOnClickListener {
            if(submitAnswer(answer3.text.toString())){
                answer3.setBackgroundColor(Color.GREEN)
            }else{
                answer3.setBackgroundColor(Color.RED)
            }
            checkMatchFinish()
        }
        answer4.setOnClickListener {
            if(submitAnswer(answer4.text.toString())){
                answer4.setBackgroundColor(Color.GREEN)
            }else{
                answer4.setBackgroundColor(Color.RED)
            }
            checkMatchFinish()
        }
    }

    private fun submitAnswer(answer: String): Boolean {
        return (activity as QuizGameActivity).submitAnswer(answer)
    }

    private fun checkMatchFinish(){
        GlobalScope.launch {
            delay(1000)
            GlobalScope.launch(Dispatchers.Main) {
                //Action on UI thread
                (activity as QuizGameActivity).checkMatchFinish()
            }
        }
    }
}