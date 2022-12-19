package com.example.mds_tp3_quiz.game

import android.content.Context
import android.widget.Toast
import com.example.mds_tp3_quiz.model.Quiz
import com.example.mds_tp3_quiz.presentation.quiz_game.OnGameReadyListener
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class QuizGame(private val context: Context, private val onGameReadyListener: OnGameReadyListener) {
    private val quizList = mutableListOf<Quiz>()
    private var currentRound = 0
    private var score = 0

    fun start() {
        loadQuizFromDB()
        currentRound = 0
        score = 0
    }

    private fun loadQuizFromDB() {
        val db = Firebase.firestore
        db.collection("QuizCollection")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    quizList.add(getQuizFromDocument(document))
                }
                onGameReadyListener.onGameReady()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error obtaining quiz", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getQuizFromDocument(document: QueryDocumentSnapshot): Quiz {
        val question = document.getString("question") ?: "E1"
        val answerA = document.getString("answerA") ?: "E1"
        val answerB = document.getString("answerB") ?: "E1"
        val answerC = document.getString("answerC") ?: "E1"
        val answerD = document.getString("answerD") ?: "E1"
        val correctAnswer = document.getString("correctAnswer") ?: "E1"

        return Quiz(question, answerA, answerB, answerC, answerD, correctAnswer)
    }

    fun getNextQuestion(): Quiz {
        val quiz = quizList[currentRound]
        currentRound++
        return quiz
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
