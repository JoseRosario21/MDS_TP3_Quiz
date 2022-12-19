package com.example.mds_tp3_quiz.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mds_tp3_quiz.model.Quiz

class QuizGame : ViewModel() {
    private val quizList = mutableListOf<Quiz>()
    private var currentRound = 0
    private var score = 0
    private val _quiz = MutableLiveData<Quiz>()
    val quiz: LiveData<Quiz>
        get() {
            return _quiz
        }

    init {
        currentRound = 0
        score = 0

        getMatchData()
        getNextQuestion()
    }

    fun getNextQuestion() {
        if (!isFinished()) {
            this._quiz.value = quizList.get(currentRound)
            currentRound++
        }
    }

    fun submitAnswer(answer: String): Boolean {
        val quiz = quizList[currentRound - 1]
        if (answer == quiz.correctAnswer) {
            score++
            return true
        }
        return false
    }

    fun getScore(): Int {
        return score
    }

    fun isFinished(): Boolean {
        return currentRound == quizList.size
    }

    private fun getMatchData(){
        val q1 = Quiz("A que dia calha a Pascoa",
            "Segunda", "Terça", "Domingo", "Sábado", "Domingo");
        quizList.add(q1)
        val q2 = Quiz("Quantos dias tem o carnaval?",
            "1", "2", "3", "4", "3");
        quizList.add(q2)
    }

    fun getRoundCounterString(): String{
        return "Question " + currentRound + " out of 10."
    }
}
