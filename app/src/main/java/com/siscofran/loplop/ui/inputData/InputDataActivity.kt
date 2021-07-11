package com.siscofran.loplop.ui.inputData

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.siscofran.loplop.R
import com.siscofran.loplop.databinding.ActivityInputDataBinding
import com.siscofran.loplop.ui.inputData.name.NameFragment
import com.siscofran.loplop.ui.inputData.photo.UploadPhotoFragment
import com.siscofran.loplop.utils.CameraView
import kotlinx.android.synthetic.main.activity_input_data.*

class InputDataActivity : AppCompatActivity(), CameraView.CameraListener {

    private lateinit var binding: ActivityInputDataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_input_data, NameFragment(), nameFragment).commit()
    }

    override fun onFinishGetPicture(uriImage: Uri?) {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_input_data) as UploadPhotoFragment
        fragment.setImageByPosition(uriImage)
    }

    companion object{
        const val nameFragment = "NameFragment"
        const val genderFragment = "GenderFragment"
        const val photoFragment = "PhotoFragment"
        const val hobbyFragment = "HobbyFragment"
    }
}