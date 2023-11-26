package com.noble.sync.view.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.noble.sync.Constants
import com.noble.sync.R
import com.noble.sync.databinding.ActivitySignUpBinding
import com.noble.sync.enum.SupportedGenres
import com.noble.sync.util.Animations
import com.noble.sync.viewmodel.AuthViewModel

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var viewModel: AuthViewModel
    private val shakeAnim = Animations.getShake(500, 10f, 4f)
    private val request = registerForActivityResult(ActivityResultContracts.GetContent()) {
        viewModel.changeUri(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        binding.yearSmall.text =
            binding.yearSmall.text.toString().plus(" ".plus(Constants.MIN_YEAR.toString()))


        setListeners()
        setObservers()
        setGender()
//        val email = intent.extras?.getString("email") ?: ""
//        val password = intent.extras?.getString("password") ?: ""
    }

    private fun setObservers() {
        viewModel.uri.observe(this) {
            if (it == null) {
                binding.profile.visibility = View.GONE
                binding.profilePlaceholder.visibility = View.VISIBLE
            } else {
                binding.profile.visibility = View.VISIBLE
                binding.profilePlaceholder.visibility = View.GONE
                binding.profile.setImageURI(it)
            }
        }

        viewModel.name.observe(this) {
            binding.name.setTextColor(
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

    private fun signUp() {
        TODO("Not yet implemented")
    }

    private fun authIsOK(): Boolean {
        val nameIsValid = viewModel.name.value != null && viewModel.name.value!!.length >= 2
        val yearIsValid = viewModel.year.value != null && (
                        viewModel.year.value!! > Constants.MAX_YEAR ||
                        viewModel.year.value!! < Constants.MIN_YEAR
                )

        if (!nameIsValid) binding.name.startAnimation(shakeAnim)
        else if (!yearIsValid) binding.year.startAnimation(shakeAnim)

        return nameIsValid && yearIsValid
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
        request.launch("image/*")
    }
}