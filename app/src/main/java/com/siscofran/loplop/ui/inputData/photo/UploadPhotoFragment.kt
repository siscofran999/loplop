package com.siscofran.loplop.ui.inputData.photo

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.siscofran.loplop.R
import com.siscofran.loplop.databinding.BsUploadImageBinding
import com.siscofran.loplop.databinding.FragmentUploadPhotoBinding
import com.siscofran.loplop.ui.inputData.InputDataActivity.Companion.hobbyFragment
import com.siscofran.loplop.ui.inputData.InputDataActivity.Companion.photoFragment
import com.siscofran.loplop.ui.inputData.hobby.HobbyFragment
import com.siscofran.loplop.utils.*
import com.siscofran.loplop.utils.ConstantVal.Companion.CAMERA_PERMISSION
import com.siscofran.loplop.utils.ConstantVal.Companion.IMAGE_GALLERY_RESULT
import io.fotoapparat.selector.*


class UploadPhotoFragment : Fragment() {

    private var _binding: FragmentUploadPhotoBinding? = null
    private val binding get() = _binding!!
    private var position = 0
    private var totalImage = 0
    private var firstLaunch1 = true
    private var firstLaunch2 = true
    private var firstLaunch3 = true
    private var firstLaunch4 = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentUploadPhotoBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imgProfile.setOnClickListener{onClick(it, 1)}
        binding.imgProfile2.setOnClickListener{onClick(it, 2)}
        binding.imgProfile3.setOnClickListener{onClick(it, 3)}
        binding.imgProfile4.setOnClickListener{onClick(it, 4)}
        binding.include.btnNext.setOnClickListener{onClick(it)}

        binding.include.btnNext.isEnabled = false
        binding.include.btnNext.text = getString(R.string.label_btn_lanjutkan_limit, "0", "2")

        val image1 = view.context.prefGetImage(1)
        val image2 = view.context.prefGetImage(2)
        val image3 = view.context.prefGetImage(3)
        val image4 = view.context.prefGetImage(4)

        if(image1 != ""){
            if(firstLaunch1){
                firstLaunch1 = false
                totalImage += 1
            }
            position = 1
            setImageByPosition(Uri.parse(image1))
        }
        if(image2 != ""){
            if(firstLaunch2){
                firstLaunch2 = false
                totalImage += 1
            }
            position = 2
            setImageByPosition(Uri.parse(image2))
        }
        if(image3 != ""){
            if(firstLaunch3){
                firstLaunch3 = false
                totalImage += 1
            }
            position = 3
            setImageByPosition(Uri.parse(image3))
        }
        if(image4 != ""){
            if(firstLaunch4){
                firstLaunch4 = false
                totalImage += 1
            }
            position = 4
            setImageByPosition(Uri.parse(image4))
        }

    }

    private fun onClick(view: View, mPosition: Int = 0) {
        position = mPosition
        when(view.id){
            R.id.img_profile, R.id.img_profile2, R.id.img_profile3, R.id.img_profile4 -> {
                if (!checkPermissionGranted(requireContext())) {
                    requestPermissions(
                        arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ), CAMERA_PERMISSION
                    )
                } else {
                    logi("totalImageeee -> $totalImage")
                    showDialog()
                }
            }
            R.id.include -> {
                val ft = fragmentManager?.beginTransaction()
                ft?.replace(R.id.fragment_input_data, HobbyFragment(), hobbyFragment)
                    ?.addToBackStack(
                        photoFragment
                    )?.commit()
            }
        }
    }

    private fun showDialog() {
        val bs = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        val bsView = BsUploadImageBinding.inflate(LayoutInflater.from(view?.context))
        bs.setContentView(bsView.root)

        bsView.btnCamera.setOnClickListener {
            if(activity != null){
                val dialog = CameraView()
                dialog.show(activity!!.supportFragmentManager, "")
            }
            bs.dismiss()
        }
        bsView.btnGallery.setOnClickListener {
            getImageFromGallery()
            bs.dismiss()
        }

        bs.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                IMAGE_GALLERY_RESULT -> {
                    val dataImage = data?.data
                    setImageByPosition(dataImage)
                }
            }
        }
    }

    fun setImageByPosition(dataImage: Uri?) {
        if (view?.context?.prefGetImage(position) == "") {
            totalImage += 1
        }
        logi("totalImage -> $totalImage")
        logi("uri -> ${dataImage?.lastPathSegment}")
        when(position){
            1 -> {
                Glide.with(this@UploadPhotoFragment).load(dataImage).circleCrop()
                    .into(binding.imgProfile)
            }
            2 -> {
                Glide.with(this@UploadPhotoFragment).load(dataImage).circleCrop()
                    .into(binding.imgProfile2)
            }
            3 -> {
                Glide.with(this@UploadPhotoFragment).load(dataImage).circleCrop()
                    .into(binding.imgProfile3)
            }
            4 -> {
                Glide.with(this@UploadPhotoFragment).load(dataImage).circleCrop()
                    .into(binding.imgProfile4)
            }
        }
        view?.context?.saveImage(position, dataImage.toString())
        if(totalImage >= 2){
            binding.include.btnNext.isEnabled = true
        }
        binding.include.btnNext.text = getString(
            R.string.label_btn_lanjutkan_limit,
            totalImage.toString(),
            "2"
        )
    }

    private fun getImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK) // take image from gallery
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_GALLERY_RESULT)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == CAMERA_PERMISSION){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                showDialog()
            }else{
                Toast.makeText(view?.context, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}