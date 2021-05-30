package com.siscofran.loplop.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.siscofran.loplop.data.model.User
import com.siscofran.loplop.databinding.ActivityMainBinding
import com.siscofran.loplop.utils.ConstantVal.Companion.LOCATION_PERMISSION
import com.siscofran.loplop.utils.loge
import com.siscofran.loplop.utils.logi
import com.siscofran.loplop.utils.saveLocationTracking
import com.siscofran.loplop.utils.toast
import link.fls.swipestack.SwipeStack


class MainActivity : AppCompatActivity(), SwipeStack.SwipeStackListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
//     LocationCallback - Called when FusedLocationProviderClient has a new Location.
    private lateinit var locationCallback: LocationCallback
    private lateinit var mAuth: FirebaseAuth
    private var currentUserId: String = ""
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private var users: ArrayList<User> = ArrayList()
    private var usersProfile: ArrayList<String> = ArrayList()
    private lateinit var adapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().getReference("users")
        storage = FirebaseStorage.getInstance()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        mAuth = FirebaseAuth.getInstance()
        currentUserId = mAuth.currentUser?.uid.toString()
        logi("masukk userId -> $currentUserId")

        locationCallback = object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val lat = locationResult.lastLocation.latitude.toString()
                val long = locationResult.lastLocation.longitude.toString()
                logi("masukk lat -> $lat")
                logi("masukk long -> $long")
                if(lat != "" && long != ""){
                    saveLocationTracking("$lat,$long")
                    fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                }
            }
        }

        if(!foregroundPermissionApproved()){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION)
        }else{
            getCurrentLocation()
        }

        fillWithTestData()
        adapter = MainAdapter({item -> doClick(item)}, users, usersProfile)
        binding.swipeStack.adapter = adapter
    }

    private fun doClick(item: Int) {

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == LOCATION_PERMISSION && grantResults.isNotEmpty()){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation()
            }else{
                toast("Permission Denied")
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(){
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 3000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun foregroundPermissionApproved(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private fun fillWithTestData() {
        val userListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dataUsers = snapshot.children
                for (user in dataUsers){
                    if(user.key != currentUserId){
                        val mUser = user.getValue(User::class.java)
                        logi("masukk -> $mUser")
                        if (mUser != null) {
                            users.add(mUser) // gs://loplop-686d0.appspot.com/profile/FaceApp_1611210262526.jpg
                            val gsReference = storage.getReferenceFromUrl("gs://loplop-686d0.appspot.com/${mUser.photo[0]}")
                            gsReference.downloadUrl.addOnCompleteListener {
                                usersProfile.add(it.result.toString())
                                adapter.notifyDataSetChanged()
                            }.addOnFailureListener { exception ->
                                val errorCode = (exception as StorageException).errorCode
                                val errorMessage = exception.message
                                loge("$errorCode -> $errorMessage")
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                loge(error.message)
            }
        }
        database.addValueEventListener(userListener)
    }

    override fun onViewSwipedToLeft(position: Int) {
        TODO("Not yet implemented")
    }

    override fun onViewSwipedToRight(position: Int) {
        TODO("Not yet implemented")
    }

    override fun onStackEmpty() {
        TODO("Not yet implemented")
    }
}