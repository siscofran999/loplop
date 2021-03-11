package com.siscofran.loplop.ui.inputData.name

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.siscofran.loplop.R
import com.siscofran.loplop.databinding.FragmentNameBinding
import com.siscofran.loplop.ui.inputData.InputDataActivity.Companion.genderFragment
import com.siscofran.loplop.ui.inputData.InputDataActivity.Companion.nameFragment
import com.siscofran.loplop.ui.inputData.gender.GenderFragment
import com.siscofran.loplop.utils.getAge
import com.siscofran.loplop.utils.logi
import com.siscofran.loplop.utils.prefGetDate
import com.siscofran.loplop.utils.saveName
import java.util.*

class NameFragment : Fragment(), View.OnClickListener {

    private lateinit var auth: FirebaseAuth

    private var _binding: FragmentNameBinding? = null
    private val binding get() = _binding!!
    private var day = 0
    private var month = 0
    private var year = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentNameBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        
        val bornDate = view.context.prefGetDate()
        if(bornDate != ""){
            val mBornDate = bornDate.split("-")
            day = mBornDate[0].toInt()
            month = mBornDate[1].toInt()
            year = mBornDate[2].toInt()
            val date = "$day-$month-$year"
            if(date != ""){
                binding.edtTgl.setText(date)
            }
        }

        binding.edtName.setText(auth.currentUser?.displayName)
        binding.edtTgl.setOnClickListener(this)
        binding.include.btnNext.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when(view.id){
            R.id.edt_tgl -> {
                showDatePicker(view.context)
            }
            R.id.include -> {
                btnNextClicked()
            }
        }
    }

    private fun btnNextClicked() {
        val tgl = binding.edtTgl.text.toString()
        val name = binding.edtName.text.toString()
        val email = auth.currentUser?.email.toString()
        if(name.isNotEmpty() && tgl.isNotEmpty()){
            if(getAge(year, month, day) < 18){
                binding.edtLayoutTgl.error = getString(R.string.label_min_age)
            }else{
                view?.context?.saveName(name, tgl, email)
                binding.edtLayoutTgl.isErrorEnabled = false
                val ft = fragmentManager?.beginTransaction()
                ft?.replace(R.id.fragment_input_data, GenderFragment(), genderFragment)?.addToBackStack(nameFragment)?.commit()
            }
        }
        if(name.isEmpty()){
            binding.edtLayoutName.error = getString(R.string.label_name_not_empty)
        }
        if(tgl.isEmpty()){
            binding.edtLayoutTgl.error = getString(R.string.label_born_date_not_empty)
        }
    }

    private fun showDatePicker(context: Context) {
        val calendar = Calendar.getInstance(Locale("id"))
        val mDay = if(day != 0) day else calendar.get(Calendar.DAY_OF_MONTH)
        if(month >= 12){
            month--
        }
        val mMonth = if(month != 0) month else calendar.get(Calendar.MONTH)
        val mYear = if(year != 0) year else calendar.get(Calendar.YEAR)

        val picker = DatePickerDialog(context, { _, pickYear, pickMonth, pickDay ->
            day = pickDay
            month = pickMonth
            year = pickYear
            val date = "$day-${month.plus(1)}-$year"
            binding.edtTgl.setText(date)
            if (getAge(year, month, day) < 18) {
                binding.edtLayoutTgl.error = getString(R.string.label_min_age)
            } else {
                binding.edtLayoutTgl.isErrorEnabled = false
            }
        }, mYear, mMonth, mDay)
        picker.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}