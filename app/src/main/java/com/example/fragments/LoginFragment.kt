package com.example.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.example.fragments.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    var isLogin : Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        val view = binding.root

        userLoginControl(requireContext(),binding)

        val sharedPreferences : SharedPreferences = requireContext().getSharedPreferences("sharedPrefLogin",android.content.Context.MODE_PRIVATE)

        binding.buttonLogin.setOnClickListener {
            val username = binding.editTextUsername.text.toString()
            val password = binding.editTextPassword.text.toString()
            loginUser(username, password)
            isLogin = true

            val editor : SharedPreferences.Editor = sharedPreferences.edit()
            editor.putBoolean("sharedLogin",isLogin)
            editor.apply()

        }

        val loginUser = sharedPreferences.getBoolean("sharedLogin",false)
        if (loginUser == true) {
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
        }

        binding.textViewBackRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_userNewRegisterFragment)

        }

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this,object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                // Back Pressed
                //activity?.finish()
            }
        })
    }


    fun loginUser(username: String, password: String) {
        val database = UserDatabase.getUserDatabase(requireContext())

        val userList: ArrayList<RegisterUser> =
            database?.registerUserDao()?.getAllRegisterUsers() as ArrayList<RegisterUser>
        for (user in userList) {
            if (user.registerUsername == username && user.registerPassword == password) {
                println("giriş başarılı")
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            } else {
                binding.textInputLayoutUsername.error = "Bilgileri kontrol et"
                binding.textInputLayoutPassword.error = "Bilgileri kontrol et"
                println("giriş başarısız")
            }
        }
    }

}


fun userLoginControl(context: Context,binding : FragmentLoginBinding) {
    val database = UserDatabase.getUserDatabase(context)
    val userList : ArrayList<RegisterUser> = database?.registerUserDao()?.getAllRegisterUsers() as ArrayList<RegisterUser>
    for (user in userList) {
        binding.editTextUsername.setText(user.registerUsername)
        binding.editTextPassword.setText(user.registerPassword)
        /*
        if (binding.editTextUsername.text.toString() != "" && binding.editTextPassword.text.toString() != "") {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

         */
    }
}
