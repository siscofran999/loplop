package com.siscofran.loplop.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.siscofran.loplop.R
import com.siscofran.loplop.data.model.Data
import com.siscofran.loplop.data.model.User
import com.siscofran.loplop.databinding.ItemSwipestackBinding
import com.siscofran.loplop.utils.getAge
import com.siscofran.loplop.utils.logi

class MainAdapter(
    val onClick: (String) -> Unit,
    private val users: ArrayList<User>,
    private val datas: ArrayList<Data>,
    val onLike: (String) -> Unit,
    val onNope: (String) -> Unit
): RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val binding = ItemSwipestackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bind(users[position], datas[position])
    }

    override fun getItemCount(): Int = users.size

    inner class MainViewHolder(private val binding: ItemSwipestackBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User, data: Data) {
            val dateBorn = user.dateBorn.split("-")
            val age = getAge(dateBorn[2].toInt(), dateBorn[1].toInt(), dateBorn[0].toInt())
            binding.txvNameAge.text = itemView.context.getString(R.string.label_name_age, user.name, age.toString())
            logi("usersPhoto -> ${data.key}")
            val radius = itemView.context.resources.getDimension(R.dimen.dimen_8dp)
            binding.imgUser.shapeAppearanceModel = binding.imgUser.shapeAppearanceModel.toBuilder().setAllCornerSizes(radius).build()
            Glide.with(itemView.context).load(data.urlPhoto).transform(RoundedCorners(itemView.context.resources.getDimension(R.dimen.dimen_16dp)
                .toInt())).into(binding.imgUser)
            binding.imgUser.setOnClickListener {
                onClick(data.key.toString())
            }
            binding.btnLike.setOnClickListener {
                binding.btnLike.setImageResource(R.drawable.ic_like_clicked)
                onLike(data.key.toString())
            }
            binding.btnNope.setOnClickListener {
                binding.btnNope.setImageResource(R.drawable.ic_nope_clicked)
                onNope(data.key.toString())
            }
        }

    }
}