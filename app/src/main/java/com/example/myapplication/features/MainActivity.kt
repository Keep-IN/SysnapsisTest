package com.example.myapplication.features

import android.content.Intent
import android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG
import android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.example.myapplication.R
import com.example.myapplication.data.model.UserData
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.features.home.HomeActivity
import com.example.myapplication.features.signup.SignUp
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            validateInput(binding.usernameTextInput.editText?.text.toString(), binding.passwordTextInput.editText?.text.toString())
        }

        binding.forgotPass.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
        }

        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback(){
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(this@MainActivity, "Authentication error: $errString", Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Toast.makeText(this@MainActivity, "Authentication succeded", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@MainActivity, HomeActivity::class.java))
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(this@MainActivity, "Authentication Failed", Toast.LENGTH_SHORT).show()
            }
        })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Login with fingerprint")
            .setNegativeButtonText("Close")
            .build()

        binding.floatingActionButton.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }

        binding.usernameTextInput.editText?.doOnTextChanged { text, start, before, count ->
            validateCredentials()
        }

        binding.passwordTextInput.editText?.doOnTextChanged { text, start, before, count ->
            validateCredentials()
        }

    }

    fun validateCredentials(){
        binding.btnLogin.isEnabled = binding.usernameTextInput.editText?.text.toString().isNotBlank()
                && binding.passwordTextInput.editText?.text.toString().isNotBlank()
    }

    fun validateInput(username: String, password: String){
        UserData.listUser.forEach { user ->
            if(username == user.username && password == user.password){
                Toast.makeText(this, "Login Succes", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomeActivity::class.java))
            } else {
                Toast.makeText(this, "Account is not registered", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun checkDeviceBiometric(){
        val biometricManager = BiometricManager.from(this)
        when(biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)){
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Log.d("APP_TAG", "Has Biometric")
                binding.floatingActionButton.isEnabled = true
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Log.d("APP_TAG", "Doesnt Have biometrics")
                Toast.makeText(this,"Device dont have biometrics", Toast.LENGTH_SHORT).show()
                binding.floatingActionButton.isEnabled = false
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
                }
                binding.floatingActionButton.isEnabled = false

                startActivityForResult(enrollIntent, 100)
            }
        }
    }
}