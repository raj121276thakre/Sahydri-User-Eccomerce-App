package com.app.userecommerce.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.userecommerce.R
import com.app.userecommerce.Utils
import com.app.userecommerce.activity.OrderDetailsActivity
import com.app.userecommerce.model.Bill


class OrdersAdapter(private val list: List<Bill>, private val context: Context) :
    RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder>() {

        //Set the views to .....................................................................................................................

    inner class OrdersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(bill: Bill) {
            itemView.findViewById<TextView>(R.id.order_Number).text =  "Order : ${bill.orderNumber}"
            itemView.findViewById<TextView>(R.id.tv_order_id).text = bill.orderId
            itemView.findViewById<TextView>(R.id.tv_order_date).text = bill.orderDate
            itemView.findViewById<TextView>(R.id.tv_order_time).text = bill.timestamp
            itemView.findViewById<TextView>(R.id.order_status).text = bill.orderStatus
            itemView.findViewById<TextView>(R.id.tv_order_total).text = "₹${bill.totalCost}"

            itemView.findViewById<ImageView>(R.id.show_Products_btn).setOnClickListener {
                itemView.findViewById<LinearLayout>(R.id.productsll).visibility = View.VISIBLE
                itemView.findViewById<ImageView>(R.id.show_Products_btn).visibility = View.GONE
                itemView.findViewById<ImageView>(R.id.hide_Products_btn).visibility = View.VISIBLE

            }

            itemView.findViewById<ImageView>(R.id.hide_Products_btn).setOnClickListener {
                itemView.findViewById<LinearLayout>(R.id.productsll).visibility = View.GONE
                itemView.findViewById<ImageView>(R.id.show_Products_btn).visibility = View.VISIBLE
                itemView.findViewById<ImageView>(R.id.hide_Products_btn).visibility = View.GONE

            }

            // products showing
            itemView.findViewById<RecyclerView>(R.id.rvOrderedProducts).layoutManager = LinearLayoutManager(context)
            itemView.findViewById<RecyclerView>(R.id.rvOrderedProducts).adapter = OrderedProductAdapter(context, bill.productDetails)


            itemView.setOnClickListener {
                val intent = Intent(context, OrderDetailsActivity::class.java)
                intent.putExtra("orderId", bill.orderId)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.order_bill_item, parent, false)
        return OrdersViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
