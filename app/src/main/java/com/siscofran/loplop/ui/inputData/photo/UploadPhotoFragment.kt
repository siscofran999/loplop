package com.siscofran.loplop.ui.inputData.photo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.siscofran.loplop.R
import com.siscofran.loplop.databinding.FragmentUploadPhotoBinding

class UploadPhotoFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentUploadPhotoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentUploadPhotoBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imgProfile.setOnClickListener(this)
        binding.imgProfile2.setOnClickListener(this)
        binding.imgProfile3.setOnClickListener(this)
        binding.imgProfile4.setOnClickListener(this)
        binding.include.btnNext.setOnClickListener(this)

        binding.include.btnNext.isEnabled = false
    }

    override fun onClick(view: View) {
        when(view.id){
            R.id.img_profile, R.id.img_profile2, R.id.img_profile3, R.id.img_profile4 -> {
                showDialog()
            }
            R.id.btn_next -> {

            }
        }
    }

    private fun showDialog() {

        val bs = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        val bsView = layoutInflater.inflate(R.layout.bs_upload_image, binding.root, false)
        bs.setContentView(bsView)

        bs.show()
    }
}