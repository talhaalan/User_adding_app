package com.example.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.fragments.databinding.FragmentFormBinding
import com.example.fragments.databinding.FragmentUpdateUserBinding
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class UpdateUserFragment : Fragment() {

    private var _binding: FragmentUpdateUserBinding? = null
    private val binding get() = _binding!!

    private var year: String = "1900"
    private var month: String = "1"
    private var day: String = "1"
    var birthTime = 0L

    var date = ""

    private var size = 100
    private var kilos = 40

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentUpdateUserBinding.inflate(inflater, container, false)

        val view = binding.root

        binding.seekBarSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
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

        binding.seekBarKilos.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
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

        binding.toolbarUpdate.title = getString(R.string.edit_user)
        binding.toolbarUpdate.navigationIcon = ContextCompat.getDrawable(requireContext(),R.drawable.arrow_back)
        binding.toolbarUpdate.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_updateUserFragment_to_homeFragment)
        }

        getSpinner()

        val editId = arguments?.getInt("editId") ?: 0
        val editName = arguments?.getString("editName") ?:""
        val editPassword = arguments?.getString("editPassword") ?:""
        val editDate = arguments?.getLong("editDate") ?: 0L
        val editSize = arguments?.getString("editSize") ?: ""
        val editKilos = arguments?.getString("editKilos") ?: ""


        var format = SimpleDateFormat("dd MMMM yyyy")
        date = format.format(editDate)
        println("edit date : " + date)

        binding.editTextName.setText(editName)
        binding.editTextPassword.setText(editPassword)
        binding.textViewDate.text = date
        binding.textViewSize.text = editSize
        binding.textViewKilos.text = editKilos


        binding.buttonUpdate.setOnClickListener {

            var editTextName = binding.editTextName.text.toString()
            var editTextPassword = binding.editTextPassword.text.toString()
            var textViewSize = binding.textViewSize.text.toString()
            var textViewKilos = binding.textViewKilos.text.toString()

            val editUser = User(editId,editTextName,editTextPassword,birthTime,textViewSize,textViewKilos)
            val userDatabase = UserDatabase.getUserDatabase(view.context)
            userDatabase?.userDao()?.updateUser(editUser)

            Snackbar.make(it,getString(R.string.user_edited_message),Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.ok_message)) {}.show()

            val aciton = UpdateUserFragmentDirections.actionUpdateUserFragmentToHomeFragment(editUser,editUser)
            findNavController().navigate(aciton)

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