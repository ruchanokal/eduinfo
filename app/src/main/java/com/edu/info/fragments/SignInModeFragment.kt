package com.edu.info.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.edu.info.R
import com.edu.info.activities.MainActivity
import com.edu.info.databinding.FragmentSignInModeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class SignInModeFragment : Fragment() {

    private var binding : FragmentSignInModeBinding? = null

    private lateinit var mAuth : FirebaseAuth
    private lateinit var user : FirebaseUser
    var loginType = ""
    var value = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInModeBinding.inflate(inflater,container,false)
        val view = binding!!.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        val preferences = requireActivity().getSharedPreferences("com.edu.info", Context.MODE_PRIVATE)
        quickLogin(preferences)

        binding!!.studentLoginButton.setOnClickListener {

            val action = SignInModeFragmentDirections.actionSignInModeFragmentToStuSignInFragment()
            Navigation.findNavController(it).navigate(action)
            preferences.edit().putString("login","student").apply()

        }

        binding!!.teacherLoginButton.setOnClickListener {

            val action = SignInModeFragmentDirections.actionSignInModeFragmentToAdminSignInFragment()
            Navigation.findNavController(it).navigate(action)
            preferences.edit().putString("login","teacher").apply()

        }

    }

    private fun quickLogin(preferences  : SharedPreferences) {
        if (mAuth.currentUser != null ) {

            loginType = preferences.getString("login","")!!

            if (loginType.equals("student")) {
                value = 2
            } else if (loginType.equals("teacher")){
                value = 4
            } else
                value = 0

            val intent = Intent(activity, MainActivity::class.java)
            intent.putExtra("definite",value)
            startActivity(intent)
            requireActivity().finish()

        }
    }


}