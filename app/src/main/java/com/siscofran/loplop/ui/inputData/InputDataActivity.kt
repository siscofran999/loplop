package com.siscofran.loplop.ui.inputData

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.siscofran.loplop.R
import com.siscofran.loplop.databinding.ActivityInputDataBinding
import com.siscofran.loplop.ui.inputData.name.NameFragment
import kotlinx.android.synthetic.main.activity_input_data.*

class InputDataActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInputDataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_input_data, NameFragment(), nameFragment).commit()
    }

    companion object{
        const val nameFragment = "NameFragment"
        const val genderFragment = "GenderFragment"
        const val photoFragment = "PhotoFragment"
        const val hobbyFragment = "HobbyFragment"
    }
}