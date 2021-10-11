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

import androidx.activity.result.ActivityResultLauncher
import android.content.*
import android.provider.MediaStore.Images.Media.getBitmap
import android.util.Patterns
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.content.Intent
import android.os.Environment
import android.util.Log
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import java.io.File


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


        binding.exitButton.setOnClickListener {
            findNavController().navigate(R.id.action_userProfileFragment_to_loginFragment)
        }

        val requestMultiplePermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            permissions.entries.forEach {

                binding.userProfile.setOnClickListener {
                    var popupMenu : PopupMenu = PopupMenu(requireContext(),it)
                    popupMenu.menuInflater.inflate(R.menu.pop_up_menu,popupMenu.menu)

                    popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                        if (item.title == getString(R.string.add_photo_from_camera)) {
                            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            try {
                                startActivityForResult(takePictureIntent, 1)
                            } catch (e: ActivityNotFoundException) {
                                Log.e("hata",e.toString())
                            }

                        } else if (item.title == getString(R.string.add_photo_from_gallery)) {
                            val intentToGallery = Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                            intentActivityResultLauncher!!.launch(intentToGallery)
                        }
                        true
                    })
                    popupMenu.show()
                }

            }
        }


        registerLauncher()

        val database = UserDatabase.getUserDatabase(requireContext())
        //var userProfileList : ArrayList<UserProfile> = database?.userProfileDao()?.getAllUserProfile() as ArrayList<UserProfile>
        val userImage : ArrayList<UserImage> = database?.userImageDao()?.getUserImage() as ArrayList<UserImage>
        for (image in userImage) {
            binding.userProfile.setImageBitmap(image.profilePhoto)
        }

        /*
        sharedPref = requireContext().getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        for(u in userProfileList) {
            val user = u.userImage
            val data = Uri.parse(user)


            val editor : SharedPreferences.Editor = sharedPref!!.edit()
            editor.putString("imageData",data.toString())
            editor.apply()

            val dataImage = sharedPref!!.getString("imageData","null")
            val profilePhoto = Uri.parse(dataImage)
            Picasso.get().load(profilePhoto).transform(CircleTransform()).into(binding.userProfile)

        }

         */


        binding.userProfile.setOnClickListener {


            requestMultiplePermissions.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )

            /*
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
            }
            else {
                val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
                val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Add Photo!")
                builder.setItems(options, DialogInterface.OnClickListener() { dialog, which ->
                    if (options[which] == "Take Photo") {
                        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        intentActivityResultLauncher!!.launch(takePictureIntent)
                    } else if (options[which] == "Choose from Gallery") {
                        val intentToGallery = Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        intentActivityResultLauncher!!.launch(intentToGallery)
                    }
                })
                builder.show()


            }

             */
        }

        return view
    }

    fun registerLauncher() {
        val database = UserDatabase.getUserDatabase(requireContext())
        var userProfileList : ArrayList<UserProfile> = database?.userProfileDao()?.getAllUserProfile() as ArrayList<UserProfile>


        intentActivityResultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val intentFromResult = result.data

                if (intentFromResult != null) {
                    imageData = intentFromResult.data
                    val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireContext().contentResolver,
                            imageData!!
                        ))
                    } else {
                        getBitmap(requireContext().contentResolver, imageData)
                    }
                    binding.userProfile.setImageBitmap(bitmap)

                    //Picasso.get().load(imageData).transform(CircleTransform()).into(binding.userProfile)
                    //binding.userProfile.setImageURI(imageData)

                }
            }

        }
        permissionLauncher = registerForActivityResult(RequestPermission()) { result ->
            if (result) {
                val intentToGallery = Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                intentActivityResultLauncher!!.launch(intentToGallery)
            } else {
                Toast.makeText(context, "İzin iptal edildi...", Toast.LENGTH_SHORT).show()
            }
        }

        if (userProfileList.isEmpty()) {
            binding.saveButton.visibility = View.VISIBLE
            binding.exitButton.visibility = View.INVISIBLE
        } else {
            binding.updateButton.visibility = View.VISIBLE
            binding.updateImageButton.visibility = View.VISIBLE
        }

        binding.saveButton.setOnClickListener {
            var userEmail = binding.editTextEmail.text.toString()
            var userAge = binding.editTextAge.text.toString()
            var userSchool = binding.editTextSchool.text.toString()
            var userJob = binding.editTextJob.text.toString()

            val isValid = Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()

            if (!isValid) {
                binding.textInputLayoutEmail.error = getString(R.string.invalid_email_message)
            }
            else if (userEmail.isEmpty()) {
                binding.editTextEmail.error = getString(R.string.empty_text)
            } else if (userAge.isEmpty()) {
                binding.editTextAge.error = getString(R.string.empty_text)
            } else if (userSchool.isEmpty()) {
                binding.editTextSchool.error = getString(R.string.empty_text)
            } else if (userJob.isEmpty()) {
                binding.editTextJob.error = getString(R.string.empty_text)
            } else {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireContext().contentResolver,
                    imageData!!
                ))
            } else {
                getBitmap(requireContext().contentResolver, imageData)
            }


                lifecycleScope.launch {
                    val userProfile = UserProfile(0,userEmail,userAge,userSchool,userJob)
                    val userImage = UserImage(0,bitmap)
                    database.userImageDao().addImage(userImage)
                    database.userProfileDao().addUserDetails(userProfile)
                }

                findNavController().navigate(R.id.action_userProfileFragment_to_homeFragment)
                /*
                findNavController().navigate(R.id.action_userProfileFragment_self)
                Snackbar.make(it,getString(R.string.saved),Snackbar.LENGTH_SHORT).setAction(getString(
                    R.string.homepage),
                    View.OnClickListener {
                        findNavController().navigate(R.id.action_userProfileFragment_to_homeFragment)
                    }).show()
                 */
            }
        }

        binding.updateImageButton.setOnClickListener {
            var userEmail = binding.editTextEmail.text.toString()
            var userAge = binding.editTextAge.text.toString()
            var userSchool = binding.editTextSchool.text.toString()
            var userJob = binding.editTextJob.text.toString()

            if (imageData == null)  {
                Snackbar.make(it,getString(R.string.update_info_text),Snackbar.LENGTH_SHORT).show()
            } else {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireContext().contentResolver,
                        imageData!!
                    ))
                } else {
                    getBitmap(requireContext().contentResolver, imageData)
                }

                val userProfile = UserProfile(0,userEmail,userAge,userSchool,userJob)
                val userImage = UserImage(0,bitmap)
                database.userImageDao().updateImage(userImage)
                database.userProfileDao().update(userProfile)

                findNavController().navigate(R.id.action_userProfileFragment_self)
                Snackbar.make(it,getString(R.string.updated),Snackbar.LENGTH_SHORT).setAction(getString(
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
            }
            else if (userEmail.isEmpty()) {
                binding.editTextEmail.error = getString(R.string.empty_text)
            } else if (userAge.isEmpty()) {
                binding.editTextAge.error = getString(R.string.empty_text)
            } else if (userSchool.isEmpty()) {
                binding.editTextSchool.error = getString(R.string.empty_text)
            } else if (userJob.isEmpty()) {
                binding.editTextJob.error = getString(R.string.empty_text)
            } else {

                val userProfile = UserProfile(0,userEmail,userAge,userSchool,userJob)
                database.userProfileDao().update(userProfile)


                findNavController().navigate(R.id.action_userProfileFragment_self)


            }

        }

        for (userDetails in userProfileList) {
            binding.editTextEmail.setText(userDetails.userEmail)
            binding.editTextAge.setText(userDetails.userAge)
            binding.editTextSchool.setText(userDetails.userSchool)
            binding.editTextJob.setText(userDetails.userJob)

        }
    }
    private fun hasPermissions(context: Context?, vararg PERMISSIONS: String): Boolean {
        if (context != null && PERMISSIONS != null) {
            for (permission in PERMISSIONS) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val database = UserDatabase.getUserDatabase(requireContext())
        var userProfileList : ArrayList<UserProfile> = database?.userProfileDao()?.getAllUserProfile() as ArrayList<UserProfile>
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap


            binding.saveButton.setOnClickListener {
                var userEmail = binding.editTextEmail.text.toString()
                var userAge = binding.editTextAge.text.toString()
                var userSchool = binding.editTextSchool.text.toString()
                var userJob = binding.editTextJob.text.toString()

                val isValid = Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()

                if (!isValid) {
                    binding.textInputLayoutEmail.error = getString(R.string.invalid_email_message)
                } else if (userEmail.isEmpty()) {
                    binding.editTextEmail.error = getString(R.string.empty_text)
                } else if (userAge.isEmpty()) {
                    binding.editTextAge.error = getString(R.string.empty_text)
                } else if (userSchool.isEmpty()) {
                    binding.editTextSchool.error = getString(R.string.empty_text)
                } else if (userJob.isEmpty()) {
                    binding.editTextJob.error = getString(R.string.empty_text)
                } else {
                    val userImage = UserImage(0, imageBitmap)
                    database.userImageDao().addImage(userImage)
                }
                findNavController().navigate(R.id.action_userProfileFragment_to_homeFragment)
            }

            binding.updateImageButton.setOnClickListener {
                val userImage = UserImage(0,imageBitmap)
                database.userImageDao().updateImage(userImage)

                findNavController().navigate(R.id.action_userProfileFragment_self)
                Snackbar.make(it,getString(R.string.updated),Snackbar.LENGTH_SHORT).setAction(getString(
                    R.string.homepage),
                    View.OnClickListener {
                        findNavController().navigate(R.id.action_userProfileFragment_to_homeFragment)
                    }).show()
            }
            binding.userProfile.setImageBitmap(imageBitmap)
        }

    }

}