package com.siscofran.loplop.ui.inputData.photo

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.siscofran.loplop.R
import com.siscofran.loplop.databinding.FragmentUploadPhotoBinding
import kotlinx.android.synthetic.main.bs_upload_image.view.*

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
                if(!checkPermissionGranted()){
                    ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION)
                }else{
                    showDialog()
                }
            }
            R.id.btn_next -> {

            }
        }
    }

    private fun checkPermissionGranted() : Boolean {
        return ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED //permission already granted
    }

    private fun showDialog() {
        val bs = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        val bsView = layoutInflater.inflate(R.layout.bs_upload_image, binding.root, false)
        bs.setContentView(bsView)

        bsView.btn_camera.setOnClickListener {
            getImageFromCamera()
            bs.dismiss()
        }
        bsView.btn_gallery.setOnClickListener {
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
                }
                IMAGE_CAMERA_RESULT -> {
                    val photos = data?.extras?.get("data") as Bitmap
                }
            }
        }
    }

    private fun getImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK) // take image from gallery
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_GALLERY_RESULT)
    }

    private fun getImageFromCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(view!!.context.packageManager) != null)
            startActivityForResult(cameraIntent, IMAGE_CAMERA_RESULT)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
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