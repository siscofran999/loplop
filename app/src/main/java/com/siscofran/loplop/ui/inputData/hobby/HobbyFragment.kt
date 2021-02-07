package com.siscofran.loplop.ui.inputData.hobby

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.siscofran.loplop.R
import com.siscofran.loplop.databinding.FragmentHobbyBinding
import com.siscofran.loplop.databinding.FragmentUploadPhotoBinding

class HobbyFragment : Fragment() {

    private var _binding: FragmentHobbyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHobbyBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}