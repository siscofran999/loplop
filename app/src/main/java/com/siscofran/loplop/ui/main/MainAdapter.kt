package com.siscofran.loplop.ui.main

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.siscofran.loplop.R
import com.siscofran.loplop.data.model.User
import com.siscofran.loplop.databinding.ItemSwipestackBinding
import com.siscofran.loplop.utils.getAge
import com.siscofran.loplop.utils.logi

class MainAdapter(val adapterOnClick : (String) -> Unit, private val users: ArrayList<User>): BaseAdapter() {

    override fun getCount(): Int = users.size

    override fun getItem(position: Int): Any = users[position]

    override fun getItemId(position: Int): Long = position.toLong()

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val binding = ItemSwipestackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        users[position].apply {
            val dateBorn = this.dateBorn.split("-")
            val age = getAge(dateBorn[2].toInt(), dateBorn[1].toInt(), dateBorn[0].toInt())
            binding.txvNameAge.text = parent.context.getString(R.string.label_name_age, this.name, age.toString())
            logi("usersPhoto -> ${this.key}")
            Glide.with(parent.context).load(this.urlPhoto).transform(RoundedCorners(parent.context.resources.getDimension(R.dimen.dimen_16dp)
                .toInt())).into(binding.imgUser)
            binding.imgUser.setOnClickListener {
                adapterOnClick(this.key.toString())
            }
        }
        return binding.root
    }
}