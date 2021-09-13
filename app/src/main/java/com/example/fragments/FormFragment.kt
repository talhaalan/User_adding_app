package com.example.fragments

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fragments.databinding.FragmentFormBinding
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class FormFragment : Fragment() {

    private var _binding: FragmentFormBinding? = null
    private val binding get() = _binding!!

    private var year: String = "1900"
    private var month: String = "1"
    private var day: String = "1"
    var birthTime = 0L

    private var size = 100
    private var kilos = 40


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentFormBinding.inflate(inflater, container, false)

        val view = binding.root




        binding.toolbarForm.title = getString(R.string.add_user)
        binding.toolbarForm.navigationIcon = ContextCompat.getDrawable(requireContext(),R.drawable.arrow_back)
        binding.toolbarForm.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_formFragment_to_homeFragment)
        }

        getSpinner()

        binding.seekBarSize.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                size = progress
                binding.textViewSize.text = size.toString() + " " + "cm"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                println("progress2 : " + seekBar)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                println("progress3 : " + seekBar)
            }

        })

        binding.seekBarKilos.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                kilos = progress
                binding.textViewKilos.text = kilos.toString() + " " + "kg"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                println("progress2 : " + seekBar)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                println("progress3 : " + seekBar)
            }

        })

        binding.buttonSave.setOnClickListener {

            var name = binding.editTextName.text.toString()
            var password = binding.editTextPassword.text.toString()
            var size = binding.textViewSize.text.toString()
            var kilos = binding.textViewKilos.text.toString()

            /*
            val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy")
            val dateString = simpleDateFormat.format(birthTime)
            */

            if (name.isEmpty()) {
                binding.textInputLayoutName.error = getString(R.string.empty_error_message)
            } else if (password.isEmpty()) {
                binding.textInputLayoutPassword.error = getString(R.string.empty_error_message_password)
            } else if (name.length < 6) {
                binding.textInputLayoutName.error = getString(R.string.size_error_message)
            } else if (password.length < 6) {
                binding.textInputLayoutPassword.error = getString(R.string.size_error_message_password)
            } else {
                val user = User(0,name,password,birthTime,size,kilos)

                val userDatabase = UserDatabase.getUserDatabase(view.context)

                userDatabase?.userDao()?.addUser(user)

                /*
                val action = FormFragmentDirections.actionFormFragmentToDetailFragment(user)
                findNavController().navigate(R.id.action_formFragment_to_detailFragment, bundleOf("name" to name))
                */

                val aciton = FormFragmentDirections.actionFormFragmentToHomeFragment(user,user)
                findNavController().navigate(aciton)

                //findNavController().navigate(R.id.action_formFragment_to_detailFragment)

            }

        }

        return view
    }

    private fun getSpinner() {

        //Day
        val days = ArrayList<String>()
        for (i in 1..31) {
            days.add(i.toString())
        }
        val dayArrayAdapter : ArrayAdapter<String> = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item,days)
        binding.spinnerDay.adapter = dayArrayAdapter

        binding.spinnerDay.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                day = parent?.selectedItem.toString()
                convertDate()

                //binding.textViewDate.text = Date().time.toString()

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }


        //Month
        val monthList = ArrayList<String>()
        for (i in 1..12) {
            monthList.add(i.toString())
        }
        val monthArrayAdapter : ArrayAdapter<String> = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item,monthList)
        binding.spinnerMonth.adapter = monthArrayAdapter

        binding.spinnerMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                month = parent?.selectedItem.toString()
                convertDate()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        //Year
        val years = ArrayList<String>()
        val thisYear = Calendar.getInstance()[Calendar.YEAR]
        for (i in 1900..thisYear) {
            years.add(i.toString())
        }
        val yearArrayAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, years)

        binding.spinnerYear.adapter = yearArrayAdapter

        binding.spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                year = parent?.selectedItem.toString()
                convertDate()

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        println("b: " + binding.spinnerYear.selectedItem.toString())


    }

    fun convertDate() {
        var formatter = SimpleDateFormat("dd.MM.yyyy")
        var birthDate = formatter.parse("${day}.${month}.${year}")
        binding.textViewDate.text = SimpleDateFormat("dd MMMM yyyy").format(birthDate)
        birthTime = birthDate.time
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}