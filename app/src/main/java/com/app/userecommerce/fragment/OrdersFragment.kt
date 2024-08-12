package com.app.userecommerce.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.userecommerce.R
import com.app.userecommerce.Utils
import com.app.userecommerce.activity.BillListActivity
import com.app.userecommerce.adapter.AllOrderAdapter
import com.app.userecommerce.adapter.OrdersAdapter
import com.app.userecommerce.databinding.FragmentMoreBinding
import com.app.userecommerce.model.AllOrderModel
import com.app.userecommerce.model.Bill
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class OrdersFragment : Fragment() {

    private lateinit var binding: FragmentMoreBinding
    private lateinit var list: ArrayList<Bill>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMoreBinding.inflate(layoutInflater)
        setStatusBarColor()
        // Inflate the layout for this fragment

        list = ArrayList()
        binding.allordersRecycler.layoutManager = LinearLayoutManager(requireContext())

        val preferences =
            requireContext().getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        val userId = preferences.getString("number", "")!!

        Firebase.firestore.collection("bills")
            .whereEqualTo("userPhoneNumber", userId)
            .get()
            .addOnSuccessListener {
                list.clear()
                for (doc in it) {
                    val data = doc.toObject(Bill::class.java)
                    list.add(data)
                }


                // Sort the list by the merged date and time
                list.sortByDescending { parseDateTime(mergeDateTime(it.orderDate, it.timestamp)) }
                binding.allordersRecycler.adapter = OrdersAdapter(list, requireContext())
            }
            .addOnFailureListener {
                Utils.showToast(requireContext(), "Something went wrong")
            }

        binding.BillsBtn.setOnClickListener {
            val intent = Intent(requireContext(), BillListActivity::class.java)
            startActivity(intent)

        }




        return binding.root
    }


    private fun mergeDateTime(date: String, time: String): String {
        return "$date $time"
    }
    private fun parseDateTime(dateTime: String): Date? {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return format.parse(dateTime)
    }


    //statusbar color
    private fun setStatusBarColor() {
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(requireContext(), R.color.yellow)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }


}