package com.noble.sync.model

import android.net.Uri
import com.noble.sync.enum.SupportedGenres

open class User(
    var name: String,
    var email: String,
    var birthYear: Int,
    var gender: SupportedGenres,
    var photoUri: Uri?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (name != other.name) return false
        if (email != other.email) return false
        if (birthYear != other.birthYear) return false
        if (gender != other.gender) return false
        if (photoUri != other.photoUri) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + birthYear
        result = 31 * result + gender.hashCode()
        result = 31 * result + (photoUri?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "User(name='$name', email='$email', birthYear=$birthYear, gender=$gender, photoUri=$photoUri)"
    }
}
