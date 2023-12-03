package com.noble.sync.view.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import com.noble.sync.res.Constants
import com.noble.sync.R
import com.noble.sync.auth.SyncAuth
import com.noble.sync.databinding.ActivitySignUpBinding
import com.noble.sync.enum.SupportedGenres
import com.noble.sync.util.Animations
import com.noble.sync.viewmodel.AuthViewModel

class SignUpActivity : AppCompatActivity() {
    private lateinit var authSystem: SyncAuth
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var viewModel: AuthViewModel
    private val shakeAnim = Animations.getShake(500, 10f, 4f)
    private val requestGallery = registerForActivityResult(ActivityResultContracts.GetContent()) {
        viewModel.changeURL(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        binding.yearSmall.text =
            binding.yearSmall.text.toString().plus(" ".plus(Constants.MIN_YEAR.toString()))
        authSystem = SyncAuth(Firebase.auth, Firebase.firestore, Firebase.storage)

        setListeners()
        setObservers()
        setGender()
        val email = intent.extras?.getString("email")
        if (email == null) finish()
        val password = intent.extras?.getString("password")
        if (password == null) finish()
        viewModel.changeEmailValue(email!!)
        viewModel.changePasswordValue(password!!)
    }

    private fun setObservers() {
        viewModel.uri.observe(this) {
            if (it == null) {
                binding.profile.visibility = View.GONE
                binding.profilePlaceholder.visibility = View.VISIBLE
            } else {
                binding.profile.visibility = View.VISIBLE
                binding.profilePlaceholder.visibility = View.GONE
                binding.profile.setImageURI(it.toUri())
            }
        }

        viewModel.name.observe(this) {
            binding.name.setTextColor(
                if (it.length >= 2) getColor(R.color.title)
                else getColor(R.color.danger)
            )
        }

        viewModel.nickname.observe(this) {
            binding.nick.setTextColor(
                if (it.length >= 2) getColor(R.color.title)
                else getColor(R.color.danger)
            )
        }

        viewModel.year.observe(this) {
            binding.year.setTextColor(
                if (it < Constants.MAX_YEAR || it > Constants.MIN_YEAR) getColor(R.color.danger)
                else getColor(R.color.title)
            )
        }

        viewModel.gender.observe(this) {
            setGender()
        }
    }

    private fun setListeners() {
        binding.card.setOnClickListener {
            pickImage()
        }

        binding.removeProfile.setOnClickListener {
            viewModel.resetProfileUri()
        }

        binding.name.addTextChangedListener {
            viewModel.changeNameValue(binding.name.text.toString())
        }

        binding.year.addTextChangedListener {
            viewModel.changeYearValue(
                try {
                    binding.year.text.toString().toInt()
                } catch (e: Exception) {
                    0
                }
            )
        }

        binding.nick.addTextChangedListener {
            viewModel.changeNickname(it.toString())
        }

        binding.male.setOnClickListener {
            viewModel.changeGender(SupportedGenres.MALE)
        }

        binding.female.setOnClickListener {
            viewModel.changeGender(SupportedGenres.FEMALE)
        }

        binding.other.setOnClickListener {
            viewModel.changeGender(SupportedGenres.OTHER)
        }

        binding.goBack.setOnClickListener {
            finish()
        }

        binding.signUpButton.setOnClickListener {
            if (authIsOK()) signUp()
        }
    }

    private fun authIsOK(): Boolean {
        val nameIsValid = viewModel.name.value != null && viewModel.name.value!!.length >= 2
        val yearIsValid = viewModel.year.value != null && (
                viewModel.year.value!! > Constants.MAX_YEAR ||
                        viewModel.year.value!! < Constants.MIN_YEAR
                )
        val nicknameIsValid =
            viewModel.nickname.value != null && viewModel.nickname.value!!.length >= 2

        if (!nameIsValid) binding.name.startAnimation(shakeAnim)
        else if (!nicknameIsValid) binding.nick.startAnimation(shakeAnim)
        else if (!yearIsValid) binding.year.startAnimation(shakeAnim)

        return nameIsValid && yearIsValid && nicknameIsValid
    }

    private fun setGender() {
        binding.male.setTextColor(getColor(R.color.textPlaceholder))
        binding.female.setTextColor(getColor(R.color.textPlaceholder))
        binding.other.setTextColor(getColor(R.color.textPlaceholder))

        when (viewModel.gender.value) {
            SupportedGenres.MALE -> binding.male.setTextColor(getColor(R.color.success))
            SupportedGenres.FEMALE -> binding.female.setTextColor(getColor(R.color.success))
            else -> binding.other.setTextColor(getColor(R.color.success))
        }
    }

    private fun pickImage() {
        requestGallery.launch("image/*")
    }

    private fun signUp() {
        val user = viewModel.catchUser()
        authSystem.auth.createUserWithEmailAndPassword(user.email, viewModel.password.value!!)
            .addOnCompleteListener(this) {
                val success = authSystem.updateUser(user, this)
                if (!success)
                    Snackbar.make(
                        binding.root,
                        R.string.generic_error,
                        Snackbar.LENGTH_LONG
                    ).show()
                else
                    finish()
            }
    }
}