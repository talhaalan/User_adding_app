package com.example.fragments


import android.Manifest
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.provider.MediaStore
import com.example.fragments.databinding.FragmentUserProfileBinding
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.app.Activity.RESULT_OK
import android.widget.Toast
import com.squareup.picasso.Picasso

import androidx.activity.result.ActivityResultLauncher
import android.content.*
import android.util.Patterns
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar


class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!
    var imageData: Uri? = null

    private var intentActivityResultLauncher: ActivityResultLauncher<Intent>? = null
    var permissionLauncher: ActivityResultLauncher<String>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.toolbarProfile.title = getString(R.string.profile)
        binding.toolbarProfile.navigationIcon = ContextCompat.getDrawable(requireContext(),R.drawable.arrow_back)
        binding.toolbarProfile.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_userProfileFragment_to_homeFragment)
        }

        registerLauncher()


        binding.userProfile.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Snackbar.make(it, "İzin iptal edildi.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("İzin ver") { permissionLauncher?.launch(Manifest.permission.READ_EXTERNAL_STORAGE) }
                        .show()
                } else {
                    permissionLauncher?.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            } else {
                val intentToGallery = Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                intentActivityResultLauncher!!.launch(intentToGallery)
            }
        }

        return view
    }

    fun registerLauncher() {
        val database = UserDatabase.getUserDatabase(requireContext())
        var userProfileList : ArrayList<UserProfile> = database?.userProfileDao()?.getAllUserProfile() as ArrayList<UserProfile>
        val sharedPref : SharedPreferences = requireContext().getSharedPreferences("sharedPref", Context.MODE_PRIVATE)

        intentActivityResultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val intentFromResult = result.data
                if (intentFromResult != null) {
                    imageData = intentFromResult.data

                    val editor : SharedPreferences.Editor = sharedPref.edit()
                    editor.putString("imageData",imageData.toString())
                    editor.apply()

                    Picasso.get().load(imageData).transform(CircleTransform()).into(binding.userProfile)
                    //binding.userProfile.setImageURI(imageData)

                }
            }
        }
        permissionLauncher = registerForActivityResult(RequestPermission()) { result ->
            if (result) {
                val intentToGaleri = Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                intentActivityResultLauncher!!.launch(intentToGaleri)
            } else {
                Toast.makeText(context, "İzin iptal edildi...", Toast.LENGTH_SHORT).show()
            }
        }

        if (userProfileList.isEmpty()) {
            binding.saveButton.visibility = View.VISIBLE
        } else {
            binding.updateButton.visibility = View.VISIBLE
        }

        binding.saveButton.setOnClickListener {
            var userEmail = binding.editTextEmail.text.toString()
            var userAge = binding.editTextAge.text.toString()
            var userSchool = binding.editTextSchool.text.toString()
            var userJob = binding.editTextJob.text.toString()

            val isValid = Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()

            if (imageData == null) {
                Snackbar.make(it,getString(R.string.profile_photo_error_message),Snackbar.LENGTH_SHORT).show()
            } else if (!isValid) {
                binding.textInputLayoutEmail.error = getString(R.string.invalid_email_message)
            } else if (userEmail.isEmpty()) {
                binding.textInputLayoutEmail.error = getString(R.string.empty_text)
            } else if (userAge.isEmpty()) {
                binding.textInputLayoutAge.error = getString(R.string.empty_text)
            } else if (userSchool.isEmpty()) {
                binding.textInputLayoutSchool.error = getString(R.string.empty_text)
            } else if (userJob.isEmpty()) {
                binding.textInputLayoutJob.error = getString(R.string.empty_text)
            } else {
                val userProfile = UserProfile(0,imageData.toString(),userEmail,userAge,userSchool,userJob)
                database.userProfileDao().addUserDetails(userProfile)
                findNavController().navigate(R.id.action_userProfileFragment_self)
                Snackbar.make(it,getString(R.string.saved),Snackbar.LENGTH_SHORT).setAction(getString(
                    R.string.homepage),
                    View.OnClickListener {
                        findNavController().navigate(R.id.action_userProfileFragment_to_homeFragment)
                    }).show()

            }
        }

        binding.updateButton.setOnClickListener {
            var userEmail = binding.editTextEmail.text.toString()
            var userAge = binding.editTextAge.text.toString()
            var userSchool = binding.editTextSchool.text.toString()
            var userJob = binding.editTextJob.text.toString()

            val isValid = Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()

            if (!isValid) {
                binding.textInputLayoutEmail.error = getString(R.string.invalid_email_message)
            } else if (userEmail.isEmpty()) {
                binding.textInputLayoutEmail.error = getString(R.string.empty_text)
            } else if (userAge.isEmpty()) {
                binding.textInputLayoutAge.error = getString(R.string.empty_text)
            } else if (userSchool.isEmpty()) {
                binding.textInputLayoutSchool.error = getString(R.string.empty_text)
            } else if (userJob.isEmpty()) {
                binding.textInputLayoutJob.error = getString(R.string.empty_text)
            } else {
                val userProfile = UserProfile(0,imageData.toString(),userEmail,userAge,userSchool,userJob)
                database.userProfileDao().update(userProfile)
                findNavController().navigate(R.id.action_userProfileFragment_self)
                Snackbar.make(it,getString(R.string.updated),Snackbar.LENGTH_SHORT).setAction(getString(
                    R.string.homepage),
                    View.OnClickListener {
                        findNavController().navigate(R.id.action_userProfileFragment_to_homeFragment)
                    }).show()
            }

        }

        for (userDetails in userProfileList) {
            val data = sharedPref.getString("imageData","null")
            val profilePhoto = Uri.parse(data)
            Picasso.get().load(profilePhoto).transform(CircleTransform()).into(binding.userProfile)
            binding.editTextEmail.setText(userDetails.userEmail)
            binding.editTextAge.setText(userDetails.userAge)
            binding.editTextSchool.setText(userDetails.userSchool)
            binding.editTextJob.setText(userDetails.userJob)

        }
    }

}