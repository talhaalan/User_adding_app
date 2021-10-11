package com.example.fragments

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fragments.databinding.FragmentHomeBinding
import android.content.SharedPreferences
import android.content.Context.MODE_PRIVATE
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso


class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FormAdapter

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val view = binding.root

        //notificationChannel()


        val database = UserDatabase.getUserDatabase(requireContext())

        val userImage : ArrayList<UserImage> = database?.userImageDao()?.getUserImage() as ArrayList<UserImage>
        //val userProfile : ArrayList<UserProfile> = database?.userProfileDao()?.getAllUserProfile() as ArrayList<UserProfile>

        for (profilePhoto in userImage) {
            binding.toolbarProfileImage.setImageBitmap(profilePhoto.profilePhoto)
        }


        binding.buttonForm.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_formFragment)
        }

        binding.toolbarHome.title = getString(R.string.users)

        /*
        binding.toolbarHome.inflateMenu(R.menu.menu)
        binding.toolbarHome.setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener {
            when(it.itemId) {
                R.id.user_profile -> {
                    findNavController().navigate(R.id.action_homeFragment_to_userProfileFragment)
                }
            }
            true
        })

         */



        val registerDatabase = UserDatabase.getUserDatabase(requireContext())
        val registerUserList : ArrayList<RegisterUser> = registerDatabase?.registerUserDao()?.getAllRegisterUsers() as ArrayList<RegisterUser>
        for (r in registerUserList) {
            println("id : " + r.registerUserId)
        }



        binding.toolbarProfileImage.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_userProfileFragment)
        }




        recyclerView = view.findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(view.context,
            LinearLayoutManager.VERTICAL,false)

        val userDatabase = UserDatabase.getUserDatabase(view.context)
        val userList : ArrayList<User> = userDatabase?.userDao()?.getAllUsers() as ArrayList<User>

        if (userList.isEmpty()) {
            emptyUserlist()
        }

        adapter = FormAdapter(userList,view.context)

        adapter.onActionListener = object : FormAdapter.OnActionListener {
            override fun onItemClick(user: User) {

                findNavController().navigate(R.id.action_homeFragment_to_updateUserFragment,
                    bundleOf("editName" to user.name,
                        "editPassword" to user.password,
                        "editId" to user.userId,
                        "editDate" to user.date,
                        "editSize" to user.size,
                        "editKilos" to user.kilos)
                )

                //findNavController().navigate(R.id.action_detailFragment_to_formFragment)
            }

            override fun onRemoveClick(user: User) {
                val alert = AlertDialog.Builder(context)
                alert.setMessage(getString(R.string.delete_alert_message))
                alert.setTitle(getString(R.string.delete_alert))
                alert.setIcon(R.drawable.ic_baseline_delete_24)
                alert.setPositiveButton(getString(R.string.yes_alert), DialogInterface.OnClickListener { dialog, which ->
                    val userDatabase = UserDatabase.getUserDatabase(requireContext())

                    userDatabase?.userDao()?.deleteUser(user)
                    adapter.userList.remove(user)

                    if (adapter.userList.isEmpty()) {
                        emptyUserlist()
                    }

                    adapter.notifyDataSetChanged()

                })
                alert.setNegativeButton(getString(R.string.cancel_alert), DialogInterface.OnClickListener { dialog, which -> })
                    .show()
            }

        }
        recyclerView.adapter = adapter

        return view

    }

    private fun emptyUserlist() {
        binding.textViewEmptyList.text = getString(R.string.none_users_message)
    }

    private fun notificationStart() {
        val channelId = "notification"
        val contentTitle = getString(R.string.hi)
        val contentText = getString(R.string.welcome)

        val i = Intent(context,MainActivity::class.java)
        val pendingIntent : PendingIntent = PendingIntent.getActivity(context,0,i,0)

        val builder = NotificationCompat.Builder(requireContext(),channelId)
        builder.setChannelId(channelId)
            .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
        val notificationManager = NotificationManagerCompat.from(requireContext())
        notificationManager.notify(123,builder.build())
    }

    private fun notificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "notification"
            val channelName = "User Channel"
            val description = "i.apps.userNotification"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId,channelName,importance)
            channel.description = description

            val notificationManeger = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManeger.createNotificationChannel(channel)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        activity?.onBackPressedDispatcher?.addCallback(this,object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                // Back Pressed
                activity?.finish()
            }

        })


        /*
        object : CountDownTimer(10000,1000) {
            override fun onTick(millisUntilFinished: Long) {
                println("m: " + millisUntilFinished / 1000)
            }

            override fun onFinish() {
                //notificationStart()
            }

        }.start()

         */
    }

}