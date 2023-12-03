package com.noble.sync.model

import android.net.Uri
import com.noble.sync.enum.SupportedGenres

open class User(
    var nickname: String,
    var name: String,
    var email: String,
    var birthYear: Int,
    var gender: SupportedGenres,
    var photoURL: String?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as User
        return when {
            nickname != other.nickname -> false
            name != other.name -> false
            email != other.email -> false
            birthYear != other.birthYear -> false
            gender != other.gender -> false
            else -> photoURL == other.photoURL
        }
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + nickname.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + birthYear
        result = 31 * result + gender.hashCode()
        result = 31 * result + (photoURL?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "User(nickname='$nickname', name='$name', email='$email', birthYear=$birthYear, gender=$gender, photoURL=$photoURL)"
    }
}
