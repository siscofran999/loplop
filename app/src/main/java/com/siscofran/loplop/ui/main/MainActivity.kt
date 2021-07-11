package com.siscofran.loplop.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.siscofran.loplop.R
import com.siscofran.loplop.data.model.Data
import com.siscofran.loplop.data.model.User
import com.siscofran.loplop.databinding.ActivityMainBinding
import com.siscofran.loplop.ui.inputData.InputDataActivity
import com.siscofran.loplop.ui.login.LoginActivity
import com.siscofran.loplop.utils.loge
import com.siscofran.loplop.utils.logi
import com.siscofran.loplop.utils.toast
import com.yuyakaido.android.cardstackview.*
import java.io.FileNotFoundException
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), CardStackListener {

    private lateinit var binding: ActivityMainBinding
//     LocationCallback - Called when FusedLocationProviderClient has a new Location.
    private lateinit var mAuth: FirebaseAuth
    private var currentUserId: String = ""
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private var users: ArrayList<User> = ArrayList()
    private var datas: ArrayList<Data> = ArrayList()
    private lateinit var adapter: MainAdapter
    private var isLogin = false
    private lateinit var googleSignInClient: GoogleSignInClient

    private val manager by lazy { CardStackLayoutManager(this, this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().getReference("users")
        storage = FirebaseStorage.getInstance()
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        currentUserId = currentUser?.uid.toString()
        isLogin = currentUserId != "null"
        logi("masukk userId -> $currentUserId")
        logi("masukk isLogin -> $isLogin")

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        if(isLogin && currentUser != null){
            Glide.with(this).load(currentUser.photoUrl).transform(
                RoundedCorners(resources.getDimension(R.dimen.dimen_16dp).toInt())).into(binding.imgProfile)
        }else{
            Glide.with(this).load(R.drawable.ic_person).transform(
                RoundedCorners(resources.getDimension(R.dimen.dimen_16dp).toInt())).into(binding.imgProfile)
        }
        fillWithTestData()
        manager.setStackFrom(StackFrom.None)
        manager.setVisibleCount(3)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(true)
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        manager.setOverlayInterpolator(LinearInterpolator())
        binding.swipeStack.layoutManager = manager
        adapter = MainAdapter({ item -> doClick(item) }, users, datas)
        binding.swipeStack.adapter = adapter

        binding.imgProfile.setOnClickListener {
            if(isLogin){
                logOut()
            }else{
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun logOut() {
        mAuth.signOut()
        googleSignInClient.signOut().addOnCompleteListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun doClick(key: String) {
        logi("key -> $key")
    }

    private fun fillWithTestData() {
        if(isLogin){ // login
            val userListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    binding.progressBar.visibility = View.GONE
                    if(snapshot.value == null){
                        toast("Maaf, tidak ada data")
                    }else{
                        val dataUsers = snapshot.children
                        logi("masukk dataUsers2 -> $snapshot")

                        for (user in dataUsers){
                            logi("user login")
                            if(user.key != currentUserId){
                                val mUser = user.getValue(User::class.java)
                                if (mUser != null) {
                                    val gsReference = storage.getReferenceFromUrl("gs://loplop-686d0.appspot.com/${mUser.photo[0]}")
                                    gsReference.downloadUrl.addOnCompleteListener {
                                        users.add(
                                            User(
                                                mUser.name,
                                                mUser.dateBorn,
                                                mUser.gender,
                                                mUser.interest,
                                                mUser.photo,
                                                mUser.hobby,
                                                mUser.email
                                            )
                                        )
                                        datas.add(Data(user.key, mUser.photo[0]))
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
                }

                override fun onCancelled(error: DatabaseError) {
                    loge(error.message)
                }
            }
            database.addValueEventListener(userListener)
        }else{
            val userListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    binding.progressBar.visibility = View.GONE
                    if(snapshot.value == null){
                        toast("Maaf, tidak ada data")
                    }else{
                        val dataUsers = snapshot.children
                        logi("masukk dataUsers2 -> $snapshot")
                        for (user in dataUsers){
                            val mUser = user.getValue(User::class.java)
                            if (mUser != null) {
                                val gsReference = storage.getReferenceFromUrl("gs://loplop-686d0.appspot.com/${mUser.photo[0]}")
                                gsReference.downloadUrl.addOnCompleteListener {
                                    users.add(
                                        User(
                                            mUser.name,
                                            mUser.dateBorn,
                                            mUser.gender,
                                            mUser.interest,
                                            mUser.photo,
                                            mUser.hobby,
                                            mUser.email
                                        )
                                    )
                                    datas.add(Data(user.key, it.result.toString()))
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
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {
        logi("onCardDragging direction -> $direction")
    }

    override fun onCardSwiped(direction: Direction?) {
        logi("onCardSwiped direction -> $direction")
    }

    override fun onCardRewound() {
        logi("onCardRewound")
    }

    override fun onCardCanceled() {
        logi("onCardCanceled")
    }

    override fun onCardAppeared(view: View?, position: Int) {
        logi("onCardAppeared")
    }

    override fun onCardDisappeared(view: View?, position: Int) {
        logi("onCardDisappeared")
    }
}