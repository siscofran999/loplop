package com.siscofran.loplop.ui.inputData.photo

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.siscofran.loplop.R
import com.siscofran.loplop.databinding.BsUploadImageBinding
import com.siscofran.loplop.databinding.FragmentUploadPhotoBinding
import com.siscofran.loplop.ui.inputData.InputDataActivity
import com.siscofran.loplop.ui.inputData.InputDataActivity.Companion.hobbyFragment
import com.siscofran.loplop.ui.inputData.InputDataActivity.Companion.photoFragment
import com.siscofran.loplop.ui.inputData.gender.GenderFragment
import com.siscofran.loplop.ui.inputData.hobby.HobbyFragment
import com.siscofran.loplop.utils.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.OutputStream

class UploadPhotoFragment : Fragment() {

    private var _binding: FragmentUploadPhotoBinding? = null
    private val binding get() = _binding!!
    private var position = 0
    private var currentImagePath = ""
    private var totalImage = 0

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

        val image1 = view.context.prefGetImage1()
        val image2 = view.context.prefGetImage2()
        val image3 = view.context.prefGetImage3()
        val image4 = view.context.prefGetImage4()

        if(image1 != ""){
            position = 1
            setImageByPosition(Uri.parse(image1))
        }
        if(image2 != ""){
            position = 2
            setImageByPosition(Uri.parse(image2))
        }
        if(image3 != ""){
            position = 3
            setImageByPosition(Uri.parse(image3))
        }
        if(image4 != ""){
            position = 4
            setImageByPosition(Uri.parse(image4))
        }
    }

    private fun onClick(view: View, mPosition: Int = 0) {
        position = mPosition
        when(view.id){
            R.id.img_profile, R.id.img_profile2, R.id.img_profile3, R.id.img_profile4 -> {
                if (!checkPermissionGranted()) {
                    requestPermissions(arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), CAMERA_PERMISSION)
                } else {
                    showDialog()
                }
            }
            R.id.include -> {
                val ft = fragmentManager?.beginTransaction()
                ft?.replace(R.id.fragment_input_data, HobbyFragment(), hobbyFragment)?.addToBackStack(photoFragment)?.commit()
            }
        }
    }

    private fun checkPermissionGranted() : Boolean {
        return ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED //permission already granted
    }

    private fun showDialog() {
        val bs = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        val bsView = BsUploadImageBinding.inflate(LayoutInflater.from(view?.context))
        bs.setContentView(bsView.root)

        bsView.btnCamera.setOnClickListener {
            getImageFromCamera()
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
                IMAGE_CAMERA_RESULT -> {
                    val photos = MediaStore.Images.Media.getBitmap(view!!.context.contentResolver, Uri.fromFile(File(currentImagePath)))
                    if(photos != null){
                        val fileDir = File(Environment.getExternalStorageDirectory(), "/Loplop")
                        if (!fileDir.exists()) {
                            fileDir.mkdirs()
                        }
                        val fileName = File(fileDir, "${getString(R.string.app_name)}${System.currentTimeMillis()}.jpeg")
                        try {
                            val outputStream = FileOutputStream(fileName)
                            photos.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                            outputStream.flush()
                            outputStream.close()
                        }catch (e : FileNotFoundException){
                            loge("${e.printStackTrace()}")
                        }
                        setImageByPosition(Uri.fromFile(fileName))
                    }
                }
            }
        }
    }

    private fun setImageByPosition(dataImage: Uri?) {
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
        totalImage += 1
        logi("masukk $totalImage")
        view?.context?.saveImage(position, dataImage.toString())
        if(totalImage >= 2){
            logi("masukk takk $totalImage")
            binding.include.btnNext.isEnabled = true
        }
        binding.include.btnNext.text = getString(R.string.label_btn_lanjutkan_limit, totalImage.toString(), "2")
    }

    private fun getImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK) // take image from gallery
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_GALLERY_RESULT)
    }

    private fun getImageFromCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(view!!.context.packageManager) != null){
            val imageFile = getImageFile()
            if(imageFile != null){
                val imageUri = FileProvider.getUriForFile(view!!.context, "com.siscofran.loplop.fileprovider", imageFile)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                } else {
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile))
                }
                startActivityForResult(cameraIntent, IMAGE_CAMERA_RESULT)
            }
        }
    }

    private fun getImageFile(): File {
        val fileDir = File(Environment.getExternalStorageDirectory(), "/Loplop")
        val imageName = "${getString(R.string.app_name)}${System.currentTimeMillis()}"
        val imageFile = File.createTempFile(imageName, ".jpg", fileDir)
        currentImagePath = imageFile.absolutePath
        return imageFile
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

    companion object{
        private const val CAMERA_PERMISSION = 9999
        private const val IMAGE_GALLERY_RESULT = 888
        private const val IMAGE_CAMERA_RESULT = 999
    }
}