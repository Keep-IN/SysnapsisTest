package com.example.myapplication.features.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.example.myapplication.R
import com.example.myapplication.data.model.UserData
import com.example.myapplication.data.model.UserModel
import com.example.myapplication.databinding.ActivitySignUpBinding
import com.example.myapplication.features.MainActivity

class SignUp : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.usernameTextInput.editText?.doOnTextChanged { text, start, before, count ->
            validateInput()
        }

        binding.passwordTextInput.editText?.doOnTextChanged { text, start, before, count ->
            validateInput()
        }

        binding.btnLogin.setOnClickListener {
            saveCredential(binding.usernameTextInput.editText?.text.toString(), binding.passwordTextInput.editText?.text.toString())
            Toast.makeText(this, "Credential saved", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        }
    }

    fun validateInput(){
        binding.btnLogin.isEnabled = binding.usernameTextInput.editText?.text.toString().isNotBlank()
                && binding.passwordTextInput.editText?.text.toString().isNotBlank()
    }

    fun saveCredential(username: String, password: String){
        UserData.listUser.add(0, UserModel(username, password))
    }
}