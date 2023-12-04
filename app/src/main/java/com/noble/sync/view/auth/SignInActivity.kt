package com.noble.sync.view.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import com.noble.sync.res.Constants
import com.noble.sync.R
import com.noble.sync.auth.SyncAuth
import com.noble.sync.databinding.ActivitySignInBinding
import com.noble.sync.util.Animations
import com.noble.sync.view.dialog.ProgressDialog
import com.noble.sync.viewmodel.AuthViewModel

class SignInActivity : AppCompatActivity() {
    private lateinit var dialog: ProgressDialog
    private lateinit var authSystem: SyncAuth
    private lateinit var viewModel: AuthViewModel
    private lateinit var binding: ActivitySignInBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private val activityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            Log.i("TEST", it.toString())
            dialog.show(getString(R.string.wait))
            if (it.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("TEST", "firebaseAuthWithGoogle:" + account.id)
                    authWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("TEST", "Google sign in failed", e)
                    dialog.hidden()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dialog = ProgressDialog(this)
        googleSignInClient = GoogleSignIn.getClient(
            this, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build()
        )

        authSystem = SyncAuth(Firebase.auth, Firebase.firestore, Firebase.storage)

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
            if (formIsOK()) signIn()
        }

        binding.signUpButton.setOnClickListener {
            if (formIsOK()) signUp()
        }

        binding.signInGoogle.setOnClickListener {
            googleSignIn()
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
                if (it.length >= 8) getColor(R.color.title)
                else getColor(R.color.danger)
            )
        }

        viewModel.showPassword.observe(this) {
            binding.password.transformationMethod =
                if (it) HideReturnsTransformationMethod.getInstance() else PasswordTransformationMethod.getInstance()
            binding.password.setSelection(binding.password.length())
            binding.buttonPassword.setImageDrawable(
                AppCompatResources.getDrawable(
                    this, if (it) R.drawable.visibility_off_24 else R.drawable.visibility_24
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
        dialog.show(getString(R.string.wait))

        val email = viewModel.email.value!!
        val password = viewModel.password.value!!
        authSystem.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.i("TEST", task.result.toString())
                } else {
                    dialog.hidden()
                    Log.w("TEST", "signInWithEmail:failure", task.exception)
                    Snackbar.make(binding.root, R.string.error_credentials, Snackbar.LENGTH_LONG)
                        .show()
                }
            }
    }

    private fun signUp() {
        val i = Intent(this, SignUpActivity::class.java)
        val bundle = Bundle()
        bundle.putString("email", viewModel.email.value)
        bundle.putString("password", viewModel.password.value)
        i.putExtras(bundle)
        startActivity(i)
    }

    private fun googleSignIn() {
        activityResult.launch(googleSignInClient.signInIntent)
    }

    private fun authWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        authSystem.auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.i("TEST", "signInWithCredential:success")
                } else {
                    dialog.hidden()
                    Snackbar.make(
                        binding.root,
                        getString(R.string.generic_error),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
    }
}
