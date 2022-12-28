package com.example.mds_tp3_quiz.game

import android.content.Context
import android.widget.Toast
import com.example.mds_tp3_quiz.R
import com.example.mds_tp3_quiz.model.Quiz
import com.example.mds_tp3_quiz.presentation.quiz_game.OnGameReadyListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.random.Random

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
                val quizIndexes = mutableListOf<Int>()
                while (quizList.size < 2) {
                    val randomIndex = Random.nextInt(0, result.size())
                    if (!quizIndexes.contains(randomIndex)) {
                        quizIndexes.add(randomIndex)
                        quizList.add(getQuizFromDocument(result.documents[randomIndex]))
                    }
                }
                onGameReadyListener.onGameReady()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error obtaining quiz", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getQuizFromDocument(document: DocumentSnapshot): Quiz {
        val quizInfo = document.getString("quizInfo") ?: ""
        val question = document.getString("question") ?: ""
        val answerA = document.getString("answerA") ?: ""
        val answerB = document.getString("answerB") ?: ""
        val answerC = document.getString("answerC") ?: ""
        val answerD = document.getString("answerD") ?: ""
        val correctAnswer = document.getString("correctAnswer") ?: ""

        return Quiz(quizInfo, question, answerA, answerB, answerC, answerD, correctAnswer)
    }

    fun getNextQuestion(): Quiz {
        val quiz = quizList[currentRound]
        currentRound++
        return quiz
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

    fun getCurrentQuiz(): Quiz {
        return quizList[currentRound - 1]
    }

    fun getCurrentRound(): String {
        return String.format(context.getString(R.string.current_round_formatter, currentRound))
    }
}
