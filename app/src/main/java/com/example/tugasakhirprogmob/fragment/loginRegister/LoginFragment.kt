package com.example.tugasakhirprogmob.fragment.loginRegister

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.tugasakhirprogmob.R
import com.example.tugasakhirprogmob.activities.LoginRegisterActivity

class LoginFragment : Fragment(R.layout.fragment_login) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Tambahkan log untuk cek apakah fragment berhasil dimuat
        Log.d("LoginFragment", "Fragment berhasil dimuat")

        val signUpText = view.findViewById<TextView>(R.id.tvSignUp)
        signUpText.setOnClickListener {
            (activity as? LoginRegisterActivity)?.replaceFragment(RegisterFragment())
        }
    }
}
