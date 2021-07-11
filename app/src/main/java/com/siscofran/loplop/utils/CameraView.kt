package com.siscofran.loplop.utils

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.siscofran.loplop.R
import com.siscofran.loplop.databinding.ViewCameraBinding
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.log.logcat
import io.fotoapparat.parameter.Resolution
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.selector.*

class CameraView : DialogFragment() {

    private var _binding: ViewCameraBinding? = null
    private val binding get() = _binding!!
    private lateinit var fotoapparat: Fotoapparat
    private lateinit var listener: CameraListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ViewCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val configuration = CameraConfiguration(
            pictureResolution = firstAvailable(
                { Resolution(width=960, height=720) },
                highestResolution()
            ),
            focusMode = firstAvailable(continuousFocusPicture(), autoFocus()),
            previewResolution = firstAvailable(
                wideRatio(highestResolution()),
                standardRatio(highestResolution())
            ),
            previewFpsRange = highestFps(),
            jpegQuality = manualJpegQuality(90)
        )

        fotoapparat = Fotoapparat(
            requireContext(),
            binding.cameraView,
            focusView = binding.focusView,
            cameraConfiguration = configuration,
            lensPosition = back(),
            logger = logcat(),
            scaleType = ScaleType.CenterCrop,
            cameraErrorCallback = {
                Log.e("TAG", "Camera error: ", it)
            })

        binding.btnTakeCamera.setOnClickListener {
            getImageFromCamera()
        }
    }

    private fun getImageFromCamera(){
        val photoResult = fotoapparat.takePicture()
        photoResult.toBitmap().whenAvailable { bitmapPhoto ->
            val dataImage = bitmapPhoto?.bitmap?.rotate(90f)
            if(dataImage != null){
                val uriImage = saveImage(
                    view!!.context, dataImage, getString(R.string.app_name), "${
                        getString(
                            R.string.app_name
                        )
                    }${System.currentTimeMillis()}"
                )
                listener.onFinishGetPicture(uriImage)
                dismiss()
            }
        }
    }

    override fun getTheme(): Int {
        return R.style.DialogTheme
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as CameraListener
    }

    override fun onStart() {
        super.onStart()
        if(checkPermissionGranted(requireContext())){
            fotoapparat.start()
        }
    }

    override fun onStop() {
        super.onStop()
        if(checkPermissionGranted(requireContext())){
            fotoapparat.stop()
        }
    }

    interface CameraListener {
        fun onFinishGetPicture(uriImage: Uri?)
    }

}