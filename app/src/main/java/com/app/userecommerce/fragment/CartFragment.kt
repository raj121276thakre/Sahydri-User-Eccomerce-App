package com.app.userecommerce.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.app.userecommerce.R
import com.app.userecommerce.activity.AddressProductsActvity
import com.app.userecommerce.adapter.CartAdapter
import com.app.userecommerce.databinding.FragmentCartBinding
import com.app.userecommerce.roomdb.AppDatabase
import com.app.userecommerce.roomdb.ProductModel
import com.google.firebase.firestore.FirebaseFirestore

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
//    private lateinit var list : ArrayList<String>
//    private var quantity = 1

    private lateinit var firestore: FirebaseFirestore
    private lateinit var cartList: ArrayList<ProductModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(layoutInflater)
        setStatusBarColor()
        // Inflate the layout for this fragment

        val preference =
            requireContext().getSharedPreferences("info", AppCompatActivity.MODE_PRIVATE)
        val editor = preference.edit()
        editor.putBoolean("isCart", false)
        editor.apply()

        val dao = AppDatabase.getInstance(requireContext()).productDao()

        cartList = ArrayList()

        dao.getAllProducts().observe(requireActivity()) {
            binding.cartRecycler.adapter = CartAdapter(requireContext(), it)

            cartList.clear()
            for (data in it) {
                //list.add(data.productId)
                cartList.add(data)
            }
            toatalCost(it)
        }


        firestore = FirebaseFirestore.getInstance()



        return binding.root
    }




    private fun proceedToCheckout(total: Int) {
        val intent = Intent(context, AddressProductsActvity::class.java)
        val bundle = Bundle()
//            bundle.putStringArrayList("productIds",list)
//            bundle.putString("totalCost",total.toString())
//            bundle.putString("quantity",quantity.toString())
//
        intent.putParcelableArrayListExtra("cartList", ArrayList(cartList))
        bundle.putString("totalCost",total.toString())
        intent.putExtras(bundle)
        startActivity(intent)
    }




    private fun toatalCost(data: List<ProductModel>?) {
        var total = 0

        for (item in data!!) {
            // Remove currency symbol before parsing to integer
            val priceWithoutCurrency = item.productTotal?.replace("₹", "")
            total += priceWithoutCurrency?.toIntOrNull() ?: 0       //
            //total += item.productSp!!.toInt()
        }

        binding.totalCartItemTV.text = "Total item in cart is ${data.size}"
        binding.totalCartCostTV.text = "Total cost : ₹${total}"


        binding.btnCheckOut.setOnClickListener {
            proceedToCheckout(total)
//            val intent = Intent(context, AddressProductsActvity::class.java)
//            val bundle = Bundle()
//            bundle.putStringArrayList("productIds",list)
//            bundle.putString("totalCost",total.toString())
//            bundle.putString("quantity",quantity.toString())
//            intent.putExtras(bundle)
//
////            intent.putExtra("totalCost",total)
////            intent.putExtra("productIds",list)
//
//            startActivity(intent)
        }


    }


    //statusbar color
    private fun setStatusBarColor() {
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(requireContext(), R.color.yellow)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }


}