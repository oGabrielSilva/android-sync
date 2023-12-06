package com.noble.sync.auth

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.noble.sync.model.User
import com.noble.sync.res.Constants
import java.io.ByteArrayOutputStream

class SyncAuth(
    val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) {
    fun updatePhoto(
        bitmap: Bitmap,
        path: String,
        onComplete: (ex: java.lang.Exception?) -> Unit
    ) {
        if (auth.currentUser != null) {
            try {
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()
                val ref = storage.reference.child("/$path/${auth.currentUser?.uid!!}")
                ref.putBytes(data).addOnCompleteListener { res ->
                    val success = res.isSuccessful
                    if (!success) {
                        res.exception?.let {
                            Log.i("TEST", it.message!!)
                        }
                        onComplete(res.exception)
                    } else {
                        ref.downloadUrl.addOnCompleteListener {
                            if(it.isSuccessful) {
                                val updates = userProfileChangeRequest {
                                    photoUri = it.result
                                }
                                auth.currentUser!!.updateProfile(updates).addOnCompleteListener { c ->
                                    if (c.isSuccessful) onComplete(null)
                                    else onComplete(c.exception)
                                }
                            } else onComplete(it.exception)
                        }

                    }

                }
            } catch (e: Exception) {
                Log.i("TEST", e.message!!)
            }
        }
    }

    fun updateUser(
        user: User,
        onSuccess: () -> Unit,
        onFailure: (ex: java.lang.Exception?) -> Unit
    ) {
        if (auth.currentUser != null) {
            val hashUser = hashMapOf(
                "nickname" to user.nickname,
                "gender" to user.gender.toString(),
                "birthYear" to user.birthYear,
                "author" to auth.currentUser!!.uid
            )

            db.collection(Constants.USER_DB_COLLECTION).document(auth.currentUser?.uid.toString())
                .set(hashUser)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val updates = userProfileChangeRequest {
                            displayName = user.name
                        }
                        auth.currentUser?.updateProfile(updates)?.addOnCompleteListener { c ->
                            if (c.isSuccessful) onSuccess()
                            else onFailure(c.exception)
                        }
                    } else {
                        Log.e(
                            "TEST",
                            it.exception?.message ?: "Erro ao setar o usuário no Firestore"
                        )
                        onFailure(it.exception)
                    }
                }
        }
    }
}