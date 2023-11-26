package com.noble.sync.authentication.model

import android.net.Uri
import com.noble.sync.enum.SupportedGenres
import com.noble.sync.model.User

class AuthUser(
    val id: String?,
    private var password: String,
    name: String,
    email: String,
    birthYear: Int,
    gender: SupportedGenres,
    photoUri: Uri?
) : User(name, email, birthYear, gender, photoUri) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AuthUser

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "AuthUser(id=$id, password='$password')"
    }


}
