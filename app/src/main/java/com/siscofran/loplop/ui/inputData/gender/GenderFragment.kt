package com.siscofran.loplop.ui.inputData.gender

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.siscofran.loplop.R
import com.siscofran.loplop.databinding.FragmentGenderBinding
import com.siscofran.loplop.ui.inputData.InputDataActivity.Companion.genderFragment
import com.siscofran.loplop.ui.inputData.InputDataActivity.Companion.photoFragment
import com.siscofran.loplop.ui.inputData.photo.UploadPhotoFragment
import com.siscofran.loplop.utils.prefGetGender
import com.siscofran.loplop.utils.prefGetInterest
import com.siscofran.loplop.utils.saveGender

class GenderFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentGenderBinding? = null
    private val binding get() = _binding!!
    private var gender = ""
    private var interest = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentGenderBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gender = view.context.prefGetGender()
        interest = view.context.prefGetInterest()
        if(gender == ""){
            gender = getString(R.string.label_male)
        }
        if (interest == ""){
            interest = getString(R.string.label_male)
        }
        view.context.pickGender(gender)
        view.context.pickInterest(interest)

        binding.txvMale.setOnClickListener(this)
        binding.txvFemale.setOnClickListener(this)
        binding.imgInterestMale.setOnClickListener(this)
        binding.imgInterestFemale.setOnClickListener(this)
        binding.include.btnNext.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when(view.id){
            R.id.txv_male -> {
                view.context.pickGender(getString(R.string.label_male))
            }
            R.id.txv_female -> {
                view.context.pickGender(getString(R.string.label_female))
            }
            R.id.img_interest_male -> {
                view.context.pickInterest(getString(R.string.label_male))
            }
            R.id.img_interest_female -> {
                view.context.pickInterest(getString(R.string.label_female))
            }
            R.id.include -> {
                btnNextClicked()
            }
        }
    }

    private fun Context.pickInterest(mInterest: String) {
        if(mInterest == "male"){
            interest = getString(R.string.label_male)
            binding.imgInterestMale.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_interest_male_white))
            binding.imgInterestFemale.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_interest_female_black))
        }else{
            interest = getString(R.string.label_female)
            binding.imgInterestMale.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_interest_male_black))
            binding.imgInterestFemale.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_interest_female_white))
        }
    }

    private fun Context.pickGender(mGender: String) {
        if(mGender == "male"){
            gender = getString(R.string.label_male)
            binding.imgGender.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_male))
            binding.txvMale.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.txvFemale.setTextColor(ContextCompat.getColor(this, R.color.black))
        }else{
            gender = getString(R.string.label_female)
            binding.imgGender.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_female))
            binding.txvMale.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding.txvFemale.setTextColor(ContextCompat.getColor(this, R.color.white))
        }
    }

    private fun btnNextClicked() {
        view?.context?.saveGender(gender, interest)
        val ft = fragmentManager?.beginTransaction()
        ft?.replace(R.id.fragment_input_data, UploadPhotoFragment(), photoFragment)?.addToBackStack(genderFragment)?.commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}