package com.example.mds_tp3_quiz.presentation.navigation.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mds_tp3_quiz.R
import com.example.mds_tp3_quiz.model.User
import com.example.mds_tp3_quiz.presentation.navigation.NavigationActivity
import com.facebook.internal.FacebookDialogFragment.Companion.TAG
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var db: FirebaseFirestore
    private lateinit var currentUser: User
    private val userList = arrayListOf<User>()
    private val uristring:String ="content://com.google.android.apps.photos.contentprovider/0/1/content%3A%2F%2Fmedia%2Fexternal%2Fimages%2Fmedia%2F23136/ORIGINAL/NONE/image%2Fjpeg/117479025"
    private val path:String ="/0/1/content%3A%2F%2Fmedia%2Fexternal%2Fimages%2Fmedia%2F23136/ORIGINAL/NONE/image%2Fjpeg/117479025"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        loadLeaderboard()

        db = Firebase.firestore
        val docRef = db.collection("Users").document(FirebaseAuth.getInstance().uid.toString())
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    currentUser = getUserFromDB(document)
                    setupUserProfile()
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onResume() {
        super.onResume()
        setupListeners()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
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

        getProfilePicture()
    }

    private fun getProfilePicture(){
        FirebaseAuth.getInstance().currentUser?.photoUrl?.let { setImage(it) }
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
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error obtaining leaderboard", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getUserRank(): Int{
        val sortedPlayers = userList.sortedWith(compareByDescending { it.globalPoints })
        var position: Int = 0
        for(player in sortedPlayers){
            position++
            if(player.email.equals(currentUser.email)){
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

    fun setImage(data: Uri){
        Picasso.get()
            .load(data)
            .into(userPicture)
    }

}