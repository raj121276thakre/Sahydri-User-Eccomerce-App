package com.app.userecommerce.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.app.userecommerce.MainActivity
import com.app.userecommerce.R
import com.app.userecommerce.databinding.ActivitySearchProductsBinding
import com.app.userecommerce.model.AddProductModel
import com.app.userecommerce.paging.ProductAdapter
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class SearchProductsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchProductsBinding
    private val products = mutableListOf<AddProductModel>()
    private var currentPage = 0
    private val pageSize = 50
    private lateinit var adapter: ProductAdapter
    private val allProducts = mutableListOf<AddProductModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setStatusBarColor()
        binding.gobackBtn.setOnClickListener {
             goback()
        }

   //  setupRecyclerView()

        val gridLayoutManager = GridLayoutManager(this, 2)
        binding.productRecycler.layoutManager = gridLayoutManager

        adapter = ProductAdapter(this, products) {
            loadMoreProducts()
        }
        binding.productRecycler.adapter = adapter

        loadInitialProducts()


        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterProducts(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })


    }


    private fun filterProducts(query: String) {
        val filteredList = if (query.isEmpty()) {
            allProducts
        } else {
            allProducts.filter {
                it.productName!!.contains(query, ignoreCase = true)

            }
        }
        products.clear()
        products.addAll(filteredList)
        adapter.notifyDataSetChanged()
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









    private fun goback() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    //set status bar color of activity
    private fun setStatusBarColor() {
        window?.apply {
            val statusBarColors = ContextCompat.getColor(this@SearchProductsActivity, R.color.yellow)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }


}