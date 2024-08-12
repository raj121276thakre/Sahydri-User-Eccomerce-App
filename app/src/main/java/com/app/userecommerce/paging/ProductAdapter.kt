package com.app.userecommerce.paging

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.userecommerce.R
import com.app.userecommerce.Utils
import com.app.userecommerce.activity.ProductDetailsActivity
import com.app.userecommerce.databinding.ItemLoadMoreBinding
import com.app.userecommerce.databinding.LayoutProductItemBinding
import com.app.userecommerce.model.AddProductModel
import com.bumptech.glide.Glide

class ProductAdapter(
    private val context: Context,
    private val products: MutableList<AddProductModel>,
    private val onLoadMore: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_PRODUCT = 0
        private const val VIEW_TYPE_LOAD_MORE = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_PRODUCT) {
            val view = LayoutInflater.from(context).inflate(R.layout.layout_product_item, parent, false)
            ProductViewHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.item_load_more, parent, false)
            LoadMoreViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ProductViewHolder) {
            holder.bind(products[position])
        } else if (holder is LoadMoreViewHolder) {
            holder.bind(onLoadMore)
        }
    }

    override fun getItemCount(): Int {
        return products.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < products.size) {
            VIEW_TYPE_PRODUCT
        } else {
            VIEW_TYPE_LOAD_MORE
        }
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = LayoutProductItemBinding.bind(itemView)

        fun bind(product: AddProductModel) {
            binding.productName.text = product.productName
            binding.productCategory.text = product.productCategory
            binding.tvProductDiscount.text = "${product.productDiscount}% off"
            binding.productMrp.text = "₹${product.productMrp}"
            binding.productSp.text = "₹${product.productSp}"
            Glide.with(itemView.context).load(product.productCoverImg).into(binding.productCoverImage)

            binding.btnBuyNow.setOnClickListener {
                // Handle Buy Now click
            }

            binding.btnAddToCart.setOnClickListener {
                // Handle Add to Cart click
            }


            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ProductDetailsActivity::class.java).apply {
                    putExtra("id", product.productId)
                }
                itemView.context.startActivity(intent)
            }


        }
    }

    class LoadMoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemLoadMoreBinding.bind(itemView)
        fun bind(onLoadMore: () -> Unit) {
            binding.btnLoadMore.setOnClickListener {
                onLoadMore()
            }
        }
    }
}
