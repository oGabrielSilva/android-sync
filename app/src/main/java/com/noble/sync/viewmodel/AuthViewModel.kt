package com.noble.sync.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.noble.sync.res.Constants
import com.noble.sync.enum.SupportedGenres
import com.noble.sync.model.User

class AuthViewModel : ViewModel() {
    private val emailLiveData = MutableLiveData("")
    private val passwordLiveData = MutableLiveData("")
    private val showPasswordLiveData = MutableLiveData(false)
    private val nameLiveData = MutableLiveData("")
    private val yearLiveData = MutableLiveData(Constants.MIN_YEAR)
    private val genderLiveData = MutableLiveData(SupportedGenres.MALE)
    private val profileURLLiveData = MutableLiveData<String>(null)
    private val nicknameLiveData = MutableLiveData("")

    val email: LiveData<String> = emailLiveData;
    val name: LiveData<String> = nameLiveData;
    val year: LiveData<Int> = yearLiveData;
    val password: LiveData<String> = passwordLiveData;
    val showPassword: LiveData<Boolean> = showPasswordLiveData
    val gender: LiveData<SupportedGenres> = genderLiveData
    val uri: LiveData<String> = profileURLLiveData
    val nickname: LiveData<String> = nicknameLiveData

    fun changeEmailValue(email: String) {
        emailLiveData.value = email
    }

    fun changePasswordValue(password: String) {
        passwordLiveData.value = password
    }

    fun toggleShowPasswordValue() {
        showPasswordLiveData.value = !(showPasswordLiveData.value)!!
    }

    fun changeNameValue(name: String) {
        nameLiveData.value = name
    }

    fun changeYearValue(year: Int) {
        yearLiveData.value = year
    }

    fun changeGender(gender: SupportedGenres) {
        genderLiveData.value = gender
    }

    fun changeURL(uri: Uri?) {
        if (uri != null) profileURLLiveData.value = uri.toString()
    }

    fun resetProfileUri() {
        profileURLLiveData.value = null
    }

    fun changeNickname(nick: String) {
        nicknameLiveData.value = nick
    }

    fun catchUser(): User {
        return User(nickname.value!!, name.value!!, email.value!!, year.value!!, gender.value!!, uri.value)
    }
}