package com.app.userecommerce.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.userecommerce.MainActivity
import com.app.userecommerce.R
import com.app.userecommerce.Utils
import com.app.userecommerce.adapter.OrderedProductAdapter
import com.app.userecommerce.databinding.ActivityOrderDetailsBinding
import com.app.userecommerce.model.Bill
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class OrderDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.orderDetails)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setStatusBarColor()


        val orderId = intent.getStringExtra("orderId") ?: return



        Firebase.firestore.collection("bills").document(orderId).get()
            .addOnSuccessListener { document ->
                val bill = document.toObject(Bill::class.java)
                if (bill != null) {
                    displayOrderDetails(bill)
                }
            }
            .addOnFailureListener {
                // Handle the error
            }


    }

    private fun displayOrderDetails(bill: Bill) {
        binding.tvOrderGreeting.text = "Hey, ${bill.userName}!"
        binding.tvOrderThankYou.text = "Thank you for your order! We'll keep you updated on its arrival."
        binding.tvOrderId.text = bill.orderId
        binding.toolbarOrderId.text = "${bill.orderId}"
        binding.tvOrderDate.text = bill.orderDate
        binding.tvOrderTime.text = bill.timestamp
        binding.tvOrderPayment.text = bill.paymentMode
        binding.tvOrderTotal.text = "₹${bill.totalCost}"
        binding.tvOrderSubtotal.text = "₹${bill.totalCost}" // Assuming subtotal and total cost are the same
        binding.tvOrderDelivery.text = "FREE" // Set this appropriately
        binding.tvOrderFinalTotal.text = "₹${bill.totalCost}"

        binding.userName.text = bill.userName
        binding.userNumber.text = bill.userPhoneNumber
        binding.shippingAddress.text = bill.userAddress
        binding.tvTotalProducts.text = bill.productDetails.size.toString()

        binding.orederStatus.text = bill.orderStatus
        // Update order status views
         val Orderstatus =  bill.orderStatus
        // Example usage, assuming you have the order ID available
        checkOrderStatus(Orderstatus)
        // These need to be updated according to the order status, assuming icons are set elsewhere

        // Update product details
        // Set up RecyclerView
        binding.rvOrderedProducts.layoutManager = LinearLayoutManager(this)
        binding.rvOrderedProducts.adapter = OrderedProductAdapter(this, bill.productDetails)


        binding.btnContinueShopping.text = "Continue Shopping" // Static text
        binding.btnContinueShopping.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

       // Static text
        binding.btnCancelOrder.setOnClickListener {
            cancleOrder(bill.orderId)
        }
    }

    private fun checkOrderStatus(status: String) {
        // Hide cancel button if the status is already canceled
        if (status == "Order is Canceled by User" || status == "Canceled"|| status == "Dispatched" || status == "Delivered") {
            binding.btnCancelOrder.visibility = View.GONE
        } else {
            binding.btnCancelOrder.visibility = View.VISIBLE
        }

        binding.orederStatus.text = status
    }


    private fun cancleOrder(orderId: String) {

        val newStatus = "Order is Canceled by User"

        // Update the order status in Firestore
        updateOrderStatus(orderId, newStatus) { success ->
            if (success) {
                // Only update UI if status update was successful
                binding.btnCancelOrder.visibility = View.GONE
                binding.orederStatus.text = newStatus
                Utils.showToast(this, "Your order is Canceled")
            } else {
                Utils.showToast(this, "Failed to cancel order")
            }
        }
    }


    private fun updateOrderStatus(
        orderId: String,
        newStatus: String,
        callback: (Boolean) -> Unit // Callback to indicate success or failure
    ) {
        val firestore = FirebaseFirestore.getInstance()
        val orderRef = firestore.collection("bills").document(orderId)

        orderRef.update("orderStatus", newStatus)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener { e ->
                callback(false)
                e.printStackTrace()
            }
    }




    // Set the status bar color
    private fun setStatusBarColor() {
        window.apply {
            val statusBarColors = ContextCompat.getColor(this@OrderDetailsActivity, R.color.yellow)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }






}