package com.example.mds_tp3_quiz.game

import com.example.mds_tp3_quiz.model.Quiz

class QuizGame {
    private val quizList = mutableListOf<Quiz>()
    private var currentRound = 0
    private var score = 0

    fun start() {
        // TODO Load the quiz questions into the quizList here
        currentRound = 0
        score = 0
    }

    fun getNextQuestion(): Quiz? {
        if (!isFinished()) {
            val quiz = quizList[currentRound]
            currentRound++
            return quiz
        }
        return null
    }

    fun submitAnswer(answer: String) {
        val quiz = quizList[currentRound - 1]
        if (answer == quiz.correctAnswer) {
            score++
        }
    }

    fun getScore(): Int {
        return score
    }

    fun isFinished(): Boolean {
        return currentRound > quizList.size
    }
}
