package com.app.userecommerce.fragment

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.app.userecommerce.R
import com.app.userecommerce.Utils
import com.app.userecommerce.adapter.CategoryAdapter
import com.app.userecommerce.paging.ProductAdapter
import com.app.userecommerce.databinding.FragmentHomeBinding
import com.app.userecommerce.model.AddProductModel
import com.app.userecommerce.model.CategoryModel
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val products = mutableListOf<AddProductModel>()
    private var currentPage = 0
    private val pageSize = 30
    private lateinit var adapter: ProductAdapter
    private val allProducts = mutableListOf<AddProductModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        setStatusBarColor()
        // Inflate the layout for this fragment

        getCategoryData()
        getSliderImage()

        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        binding.productRecycler.layoutManager = gridLayoutManager

        adapter = ProductAdapter(requireContext(), products) {
            loadMoreProducts()
        }
        binding.productRecycler.adapter = adapter

        loadInitialProducts()

        val preference = requireContext().getSharedPreferences("info", AppCompatActivity.MODE_PRIVATE)
        if (preference.getBoolean("isCart", false)) {
            findNavController().navigate(R.id.action_homeFragment_to_cartFragment)
        }

        return binding.root
    }

    private fun getSliderImage() {
        Firebase.firestore.collection("slider").document("item")
            .get().addOnSuccessListener {
                Glide.with(requireContext()).load(it.get("img")).into(binding.SliderImage)
            }
            .addOnFailureListener {
                Utils.showToast(requireContext(), "Something went wrong")
            }
    }

    private fun loadInitialProducts() {
        Firebase.firestore.collection("products")
            .get().addOnSuccessListener { result ->
                allProducts.addAll(result.documents.mapNotNull { it.toObject(AddProductModel::class.java) })
                loadMoreProducts()

            }
    }

    private fun loadMoreProducts() {
        val startIndex = currentPage * pageSize
        val endIndex = minOf(startIndex + pageSize, allProducts.size)

        if (startIndex < endIndex) {
            products.addAll(allProducts.subList(startIndex, endIndex))
            adapter.notifyDataSetChanged()
            currentPage++
        }
    }

    private fun getCategoryData() {
        val categoryList = ArrayList<CategoryModel>()
        Firebase.firestore.collection("categories")
            .get().addOnSuccessListener {
                categoryList.clear()
                for (doc in it.documents) {
                    val data = doc.toObject(CategoryModel::class.java)
                    categoryList.add(data!!)
                }
                binding.categoryRecycler.adapter = CategoryAdapter(requireContext(), categoryList)
            }
    }

    //statusbar color
    private fun setStatusBarColor() {
        activity?.window?.apply {
            var statusBarColors = ContextCompat.getColor(requireContext(), R.color.yellow)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }
}