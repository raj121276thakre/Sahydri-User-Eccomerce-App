package com.app.userecommerce.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.userecommerce.databinding.ItemBillBinding
import java.io.File

class BillAdapter(private val onClick: (File) -> Unit) : RecyclerView.Adapter<BillAdapter.BillViewHolder>() {

    private var billFiles: List<File> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        val binding = ItemBillBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BillViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        val file = billFiles[position]
        holder.bind(file)
    }

    override fun getItemCount(): Int = billFiles.size

    fun submitList(files: List<File>) {
        billFiles = files
        notifyDataSetChanged()
    }

    inner class BillViewHolder(private val binding: ItemBillBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(file: File) {
            binding.fileName.text = file.name
            binding.root.setOnClickListener { onClick(file) }
        }
    }
}
