package com.noble.sync.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.noble.sync.Constants
import com.noble.sync.enum.SupportedGenres

class AuthViewModel : ViewModel() {
    private val emailLiveData = MutableLiveData("")
    private val passwordLiveData = MutableLiveData("")
    private val showPasswordLiveData = MutableLiveData(false)
    private val nameLiveData = MutableLiveData("")
    private val yearLiveData = MutableLiveData(Constants.MIN_YEAR)
    private val genderLiveData = MutableLiveData(SupportedGenres.MALE)
    private val profileUriLiveData = MutableLiveData<Uri>(null)

    val email: LiveData<String> = emailLiveData;
    val name: LiveData<String> = nameLiveData;
    val year: LiveData<Int> = yearLiveData;
    val password: LiveData<String> = passwordLiveData;
    val showPassword: LiveData<Boolean> = showPasswordLiveData
    val gender: LiveData<SupportedGenres> = genderLiveData
    val uri: LiveData<Uri> = profileUriLiveData

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

    fun changeUri(uri: Uri?) {
        if (uri != null) profileUriLiveData.value = uri
    }

    fun resetProfileUri() {
        profileUriLiveData.value = null
    }
}