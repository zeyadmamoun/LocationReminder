package com.example.android.locationreminder.base

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.android.locationreminder.BR

class DataBindingViewHolder <T>(private val binding: ViewDataBinding) :
    RecyclerView.ViewHolder(binding.root){

        fun bind(item: T) {
            binding.setVariable(BR.item, item)
            binding.executePendingBindings()
        }
}