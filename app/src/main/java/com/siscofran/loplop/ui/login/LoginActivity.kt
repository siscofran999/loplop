package com.siscofran.loplop.ui.login

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import com.google.firebase.storage.StorageException
import com.siscofran.loplop.R
import com.siscofran.loplop.databinding.ActivityLoginBinding
import com.siscofran.loplop.ui.inputData.InputDataActivity
import com.siscofran.loplop.ui.main.MainActivity
import com.siscofran.loplop.utils.*

class LoginActivity : AppCompatActivity() {

    companion object {
        private const val RC_SIGN_IN = 999
    }

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var binding: ActivityLoginBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val lat = locationResult.lastLocation.latitude.toString()
                val long = locationResult.lastLocation.longitude.toString()
                logi("masukk lat -> $lat")
                logi("masukk long -> $long")
                if (lat != "" && long != "") {
                    saveLocationTracking("$lat,$long")
                    fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                }
            }
        }
        if (!foregroundPermissionApproved()) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    ConstantVal.LOCATION_PERMISSION
            )
        } else {
            getCurrentLocation()
        }

        database = FirebaseDatabase.getInstance().getReference("users")
        mAuth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btnLoginGoogle.setOnClickListener {
            signIn()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ConstantVal.LOCATION_PERMISSION && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                toast("Permission Denied")
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
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

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if (task.isSuccessful) {
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)
                    Log.d("TAG", "firebaseAuthWithGoogle:" + account?.id)
                    account?.idToken?.let { firebaseAuthWithGoogle(it) }
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("TAG", "Google sign in failed", e)
                    // ...
                }
            } else {
                Log.e("TAG", "onActivityResult: ${exception.toString()}")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val currentUserId = mAuth.currentUser?.uid.toString()
                        logi("masukk currentUserId -> $currentUserId")
                        if(currentUserId != "null"){
                            val userListener = object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if(snapshot.value != null){
                                        // Sign in success & user registered
                                        logi("masukk MainActivity")
                                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                        finish()
                                    }else{
                                        // Sign in success, but user not registered
                                        logi("masukk InputDataActivity")
                                        startActivity(Intent(this@LoginActivity, InputDataActivity::class.java))
                                        finish()
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    loge(error.message)
                                }
                            }
                            database.orderByKey().equalTo(currentUserId).addValueEventListener(userListener)
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "signInWithCredential:failure", task.exception)
                    }
                }
    }
}