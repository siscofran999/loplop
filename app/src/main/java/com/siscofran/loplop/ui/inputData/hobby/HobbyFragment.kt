package com.siscofran.loplop.ui.inputData.hobby

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.siscofran.loplop.databinding.FragmentHobbyBinding
import com.siscofran.loplop.R
import com.siscofran.loplop.data.model.User
import com.siscofran.loplop.databinding.ItemHobbyBinding
import com.siscofran.loplop.ui.inputData.InputDataActivity
import com.siscofran.loplop.ui.main.MainActivity
import com.siscofran.loplop.utils.*
import java.io.File

class HobbyFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentHobbyBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private lateinit var user : DatabaseReference
    private lateinit var imgRef : StorageReference
    private lateinit var mAuth: FirebaseAuth
    private var currentUserId: String = ""
    private var dataHobby = ArrayList<String>()
    private var count = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentHobbyBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        database = FirebaseDatabase.getInstance().reference
        user = FirebaseDatabase.getInstance().getReference("users")
        imgRef = FirebaseStorage.getInstance().reference
        mAuth = FirebaseAuth.getInstance()
        currentUserId = mAuth.currentUser?.uid.toString()

        getHobby(object : CallbackData<List<String>>{
            override fun callback(data: List<String>) {
                if(data.isNotEmpty()){
                    for (hobby in data){
                        val view = ItemHobbyBinding.inflate(LayoutInflater.from(view!!.context))
                        view.chip.text = hobby

                        val mHobby = context?.prefGetHobby()
                        mHobby?.forEach {
                            if(hobby == it){
                                dataHobby.add(it)
                                count = mHobby.size
                                binding.include.btnNext.isEnabled = count >= 3
                                binding.include.btnNext.text = getString(R.string.label_btn_lanjutkan_limit, mHobby.size.toString(), "3")
                                view.chip.isChecked = true
                            }
                        }

                        view.chip.setOnClickListener {
                            if(view.chip.isChecked){
                                dataHobby.add(hobby)
                                count += 1
                            }else{
                                dataHobby.remove(hobby)
                                count -= 1
                            }
                            context?.saveHobby(dataHobby)
                            binding.include.btnNext.isEnabled = count >= 3
                            binding.include.btnNext.text = getString(R.string.label_btn_lanjutkan_limit, count.toString(), "3")
                        }
                        binding.chipsHobby.addView(view.root)
                    }
                }
            }
        })

        binding.include.btnNext.isEnabled = false
        binding.include.btnNext.text = getString(R.string.label_btn_lanjutkan_limit, "0", "3")
        binding.include.btnNext.setOnClickListener(this)
    }

    private fun getHobby(callBackData: CallbackData<List<String>>){
        val hobbyListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.child("hobby").value.toString()
                logi("masukk")
                callBackData.callback(data.split(","))
            }

            override fun onCancelled(error: DatabaseError) {
                loge(error.message)
                callBackData.callback(emptyList())
            }
        }
        database.addValueEventListener(hobbyListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(view: View) {
        if(view.id == R.id.include){
            binding.include.btnNext.isEnabled = false
            val name = view.context.prefGetName()
            val date = view.context.prefGetDate()
            val gender = view.context.prefGetGender()
            val interest = view.context.prefGetInterest()
            val photoLocation = ArrayList<String>()
            val photoUrl = ArrayList<String>()
            val hobby = view.context.prefGetHobby()
            val email = view.context.prefGetEmail()
            for (i in 0..3) {
                val photo = view.context.prefGetImage(i.plus(1))
                if (photo != "") {
                    logi("masukk url $i -> $photo")
                    photoLocation.add(photo)
                    val file = Uri.fromFile(File(getPath(view.context,Uri.parse(photo))))
                    val riversRef = imgRef.child("profile/${file.lastPathSegment}")
                    val uploadTask = riversRef.putFile(file)

                    uploadTask.addOnFailureListener {
                        loge("error -> ${it.message}")
                    }.addOnSuccessListener {
                        if(it.task.isSuccessful){
                            logi("masukk success")
                            photoUrl.add(it.metadata?.path.toString())
                            logi("photoLoc -> ${photoLocation.size} == photoUrl -> ${photoUrl.size}")
                            if(photoLocation.size == photoUrl.size){
                                logi("masukk sama")
                                val mUser = User(name, date, gender, interest, photoUrl, ArrayList(hobby), email)
                                user.child(currentUserId).setValue(mUser).addOnCompleteListener {
                                    view.context.toast("Selamat Datang $name")
                                    val intent = Intent(activity, MainActivity::class.java)
                                    startActivity(intent)
                                    activity?.finish()
                                }
                            }else{
                                logi("tidak masukk sama")
                            }
                        }
                    }
                }else{
                    logi("masukk url else $i -> $photo")
                }
            }
        }
    }
}