package com.example.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.fragments.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    var isRegister : Boolean = false
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val view = binding.root

        var sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("sharedPref" ,android.content.Context.MODE_PRIVATE)

        binding.buttonRegister.setOnClickListener {

            val database = UserDatabase.getUserDatabase(requireContext())

            var username = binding.editTextUsername.text.toString()
            var password = binding.editTextPassword.text.toString()

            val userList : ArrayList<RegisterUser> = database?.registerUserDao()?.getAllRegisterUsers() as ArrayList<RegisterUser>


            isRegister = true

            val editor : SharedPreferences.Editor = sharedPreferences.edit()
            editor.putBoolean("isRegister",isRegister)
            editor.apply()



            var registerUser = RegisterUser(0,username,password)

            database?.registerUserDao()?.addRegisterUser(registerUser)

            findNavController().navigate(R.id.action_registerFragment_to_homeFragment)

        }

        var sharedRegister = sharedPreferences.getBoolean("isRegister",false)

        if (sharedRegister == true) {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }



        binding.textViewLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
        return view
    }

}

