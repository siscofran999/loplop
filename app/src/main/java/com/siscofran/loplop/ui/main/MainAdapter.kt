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

class MainAdapter(val adapterOnClick : (Int) -> Unit, private val users: ArrayList<User>, private val usersPhoto: ArrayList<String>): BaseAdapter() {

//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
//        val binding = ItemSwipestackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return MainViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
//        holder.bind(users[position])
//    }
//
//    override fun getItemCount(): Int = users.size

    inner class MainViewHolder(private val binding: ItemSwipestackBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            val dateBorn = user.dateBorn.split("-")
            val age = getAge(dateBorn[0].toInt(), dateBorn[1].toInt(), dateBorn[2].toInt())
            binding.txvNameAge.text = String.format(itemView.context.getString(R.string.label_name_age), user.name, age)
            Glide.with(itemView.context).load(usersPhoto).into(binding.imgUser)

        }
    }

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
            logi("usersPhoto -> ${usersPhoto[position]}")
            Glide.with(parent.context).load(usersPhoto[position]).transform(RoundedCorners(parent.context.resources.getDimension(R.dimen.dimen_16dp)
                .toInt())).into(binding.imgUser)
        }
        return binding.root
    }
}