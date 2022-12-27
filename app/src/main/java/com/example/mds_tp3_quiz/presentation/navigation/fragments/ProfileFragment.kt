package com.example.mds_tp3_quiz.presentation.navigation.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mds_tp3_quiz.R
import com.example.mds_tp3_quiz.model.User
import com.example.mds_tp3_quiz.presentation.navigation.NavigationActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {
    private lateinit var currentUser: User
    private val userList = arrayListOf<User>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loading_animation.visibility = View.VISIBLE

        loadLeaderboard()

        setupListeners()
    }

    private fun getUserFromDB(document: DocumentSnapshot): User {
        val username = document.get("username").toString()
        val email = document.get("email").toString()
        val points = document.getLong("globalPoints")?.toInt() ?: 0
        val totalMatches = document.getLong("totalMatches")?.toInt() ?: 0

        return User(username, email, points, totalMatches)
    }

    private fun setupUserProfile(){
        email.text = String.format(getString(R.string.email), currentUser.email)
        nickname.text = String.format(getString(R.string.nickname), '@'+ currentUser.username)
        tv_matches.text = String.format(getString(R.string.total_matches), currentUser.totalMatches)
        tv_total_points.text = String.format(getString(R.string.total_points), currentUser.globalPoints)
        tv_rank.text = String.format(getString(R.string.rank), getUserRank().toString() + 'º')

        setImage()
    }

    private fun loadLeaderboard() {
        val db = Firebase.firestore
        db.collection("Users")
            .get()
            .addOnSuccessListener { result ->
                userList.clear()
                for (document in result) {
                    userList.add(getUserFromDB(document))
                }
                currentUser = userList.find { it.email == FirebaseAuth.getInstance().currentUser?.email }!!
                setupUserProfile()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error obtaining leaderboard", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getUserRank(): Int{
        val sortedPlayers = userList.sortedWith(compareByDescending { it.globalPoints })
        var position = 0
        for(player in sortedPlayers){
            position++
            if(player.email == currentUser.email){
                return position
            }
        }
        return position
    }

    private fun setupListeners(){
        userPicture.setOnClickListener { updatePicture() }
    }

    private fun updatePicture(){
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        (activity as NavigationActivity).startActivityForResult(gallery, NavigationActivity.getRequestCode())
    }

    fun saveNewProfilePicture(data: Uri){
        val user = FirebaseAuth.getInstance().currentUser

        val storage = Firebase.storage
        // Create a storage reference from our app
        val storageRef = storage.reference
        val imagesRef = storageRef.child("images")

        // Child references can also take paths
        // spaceRef now points to "images/space.jpg
        // imagesRef still points to "images"

        val userProfilePicRef = imagesRef.child(user?.uid + "/profilePicture")
        val uploadTask = userProfilePicRef.putFile(data)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            userProfilePicRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                val profileUpdates = userProfileChangeRequest {
                    photoUri = downloadUri
                }

                user!!.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            setImage()
                        }
                    }
            }
        }
    }

    private fun setImage() {
        Picasso.get()
            .load(FirebaseAuth.getInstance().currentUser?.photoUrl)
            .into(userPicture)

        loading_animation.visibility = View.GONE
    }
}