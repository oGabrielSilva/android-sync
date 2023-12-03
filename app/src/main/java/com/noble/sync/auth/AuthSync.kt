package com.noble.sync.auth

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.noble.sync.model.User
import com.noble.sync.res.Constants

class SyncAuth(
    val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) {
    fun updatePhoto(url: String, context: Context): Boolean {
        if (auth.currentUser == null) return false
        try {
            val ref = storage.reference.child(auth.currentUser?.uid!!)
            val stream = context.contentResolver.openInputStream(url.toUri())!!
            val task = ref.putStream(stream)
            task.continueWithTask {
                if (!task.isSuccessful) {
                    task.exception?.let {
                        Log.i("TEST", it.message!!)
                    }
                }
                val updates = userProfileChangeRequest {
                    photoUri = ref.downloadUrl.result
                }
                auth.currentUser!!.updateProfile(updates)
            }
        } catch (e: Exception) {
            Log.i("TEST", e.message!!)
        }
        return true
    }

    fun updateUser(user: User, context: Context): Boolean {
        if (auth.currentUser == null) return false
        val hashUser = hashMapOf(
            "nickname" to user.nickname,
            "gender" to user.gender.toString(),
            "birthYear" to user.birthYear,
            "author" to auth.currentUser!!.uid
        )

        db.collection(Constants.USER_DB_COLLECTION).document(auth.currentUser?.uid.toString())
            .set(hashUser)
            .addOnSuccessListener {
                if (user.photoURL != null) updatePhoto(user.photoURL!!, context)

                val updates = userProfileChangeRequest {
                    displayName = user.name
                }
                auth.currentUser?.updateProfile(updates)
            }
            .addOnFailureListener {
                Log.e("TEST", it.message ?: "Erro ao setar o usuário no Firestore")
            }
        return true
    }
}