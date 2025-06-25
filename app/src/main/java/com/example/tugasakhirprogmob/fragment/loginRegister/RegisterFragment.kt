package com.example.tugasakhirprogmob.fragment.loginRegister

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.tugasakhirprogmob.R
import com.example.tugasakhirprogmob.activities.LoginRegisterActivity

class RegisterFragment : Fragment(R.layout.fragment_register) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loginText = view.findViewById<TextView>(R.id.tvAlreadyLogin) // ganti dengan ID TextView kamu
        loginText.setOnClickListener {
            (activity as? LoginRegisterActivity)?.replaceFragment(LoginFragment())
        }
    }
}
