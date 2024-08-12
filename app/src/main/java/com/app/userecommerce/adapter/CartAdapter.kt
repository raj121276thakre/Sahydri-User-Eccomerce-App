package com.app.userecommerce.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.userecommerce.activity.CategoryActivity
import com.app.userecommerce.activity.ProductDetailsActivity
import com.app.userecommerce.databinding.LayoutCartItemBinding
import com.app.userecommerce.databinding.LayoutProductItemBinding
import com.app.userecommerce.roomdb.AppDatabase
import com.app.userecommerce.roomdb.ProductModel
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class CartAdapter(val context: Context, val list: List<ProductModel>) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(val binding: LayoutCartItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding =
            LayoutCartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return CartViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {

        Glide.with(context).load(list[position].productImages).into(holder.binding.cartProductIV)

        holder.binding.cartProductName.text = list[position].productName
        holder.binding.cartProductToatl.text =" ₹${list[position].productTotal}"
        holder.binding.cartProductQuantity.text = "Quantity :  ${list[position].productQuantity}"


        holder.itemView.setOnClickListener {
            val intent = Intent(context, ProductDetailsActivity::class.java)
            intent.putExtra("id", list[position].productId)
            context.startActivity(intent)
        }

        val dao = AppDatabase.getInstance(context).productDao()
        holder.binding.deleteCartIV.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                dao.deleteProduct(
                    ProductModel(
                        list[position].productId,
                        list[position].productName,
                        list[position].productImages,
                        list[position].productTotal,
                        list[position].productQuantity
                    )
                )
            }

        }
    }


}









