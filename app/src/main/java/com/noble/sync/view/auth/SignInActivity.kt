package com.noble.sync.view.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.noble.sync.Constants
import com.noble.sync.R
import com.noble.sync.databinding.ActivitySignInBinding
import com.noble.sync.util.Animations
import com.noble.sync.viewmodel.AuthViewModel

class SignInActivity : AppCompatActivity() {
    private lateinit var viewModel: AuthViewModel
    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        binding = ActivitySignInBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setObservers()
        setListeners()
    }

    private fun setListeners() {
        binding.email.addTextChangedListener {
            viewModel.changeEmailValue(it.toString())
        }
        binding.password.addTextChangedListener {
            viewModel.changePasswordValue(it.toString())
        }

        binding.buttonPassword.setOnClickListener {
            viewModel.toggleShowPasswordValue()
        }

        binding.signInButton.setOnClickListener {
            if(formIsOK()) signIn()
        }

        binding.signUpButton.setOnClickListener {
            if(formIsOK()) signUp()
        }
    }

    private fun setObservers() {
        viewModel.email.observe(this) {
            binding.email.setTextColor(
                if (it.matches(Constants.EMAIL_REGEX)) getColor(R.color.title) else getColor(
                    R.color.danger
                )
            )
        }

        viewModel.password.observe(this) {
            binding.password.setTextColor(
                if (it.length >= 8) getColor(R.color.title) else getColor(
                    R.color.danger
                )
            )
        }

        viewModel.showPassword.observe(this) {
            binding.password.transformationMethod =
                if (it) HideReturnsTransformationMethod.getInstance() else PasswordTransformationMethod.getInstance()
            binding.password.setSelection(binding.password.length())
            binding.buttonPassword.setImageDrawable(
                AppCompatResources.getDrawable(
                    this,
                    if (it) R.drawable.visibility_off_24 else R.drawable.visibility_24
                )
            )
        }
    }

    private fun formIsOK(): Boolean {
        val shake = Animations.getShake(500, 10f, 4f)
        val emailIsValid = viewModel.email.value?.matches(Constants.EMAIL_REGEX) ?: false
        val passwordIsValid = viewModel.password.value?.length!! >= 8

        if (!emailIsValid) {
            binding.email.requestFocus()
            binding.email.startAnimation(shake)
        } else if (!passwordIsValid) {
            binding.password.requestFocus()
            binding.password.startAnimation(shake)
        }
        return emailIsValid && passwordIsValid
    }

    private fun signIn() {
        TODO("Not yet implemented")
    }

    private fun signUp() {
        val i = Intent(this, SignUpActivity::class.java)
        val bundle = Bundle()
        bundle.putString("email", viewModel.email.value)
        bundle.putString("password", viewModel.password.value)
        i.putExtras(bundle)
        startActivity(i)
    }
}
