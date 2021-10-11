package com.example.fragments

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.fragments.databinding.FragmentRegisterBinding
import com.example.fragments.databinding.FragmentUserNewRegisterBinding
import java.io.InputStream
import java.net.URL


class UserNewRegisterFragment : Fragment() {
    private var _binding: FragmentUserNewRegisterBinding? = null
    var isRegister : Boolean = false
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentUserNewRegisterBinding.inflate(inflater, container, false)
        val view = binding.root

        var sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("sharedPref" ,android.content.Context.MODE_PRIVATE)
        binding.buttonRegister.setOnClickListener {

            val database = UserDatabase.getUserDatabase(requireContext())

            var username = binding.editTextUsername.text.toString()
            var password = binding.editTextPassword.text.toString()


            val uri =
                Uri.parse("android.resource://" + requireContext().packageName.toString() + "/drawable/profile_user")

            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireContext().contentResolver,
                    uri!!
                ))
            } else {
                MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
            }

            val userProfile = UserProfile(0,"","","","")
            val userImage = UserImage(0,bitmap)
            val userList : ArrayList<RegisterUser> = database?.registerUserDao()?.getAllRegisterUsers() as ArrayList<RegisterUser>
            database.userProfileDao().update(userProfile)
            database.userImageDao().updateImage(userImage)
            for (user in userList) {
                if (user.registerUsername == username) {
                    binding.editTextUsername.error = "Bu kullanıcı var"
                }
            }

            isRegister = true

            val editor : SharedPreferences.Editor = sharedPreferences.edit()
            editor.putBoolean("isRegister",isRegister)
            editor.apply()

            var registerUser = RegisterUser(0,username,password)

            database.registerUserDao().addRegisterUser(registerUser)

            findNavController().navigate(R.id.action_userNewRegisterFragment_to_homeFragment)

        }

        /*
        val sharedRegister = sharedPreferences.getBoolean("isRegister",false)
        if (sharedRegister == true) {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

         */





        binding.textViewLogin.setOnClickListener {
            findNavController().navigate(R.id.action_userNewRegisterFragment_to_loginFragment)
        }

        return view
    }
}