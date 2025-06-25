package com.example.tugasakhirprogmob.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.tugasakhirprogmob.R
import com.example.tugasakhirprogmob.fragment.loginRegister.LoginFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginRegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register)

        // Tampilkan LoginFragment pertama kali
        if (savedInstanceState == null) {
            replaceFragment(LoginFragment())
        }
    }

    // Fungsi untuk ganti fragment
    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main, fragment)
            .commit()
    }
}
