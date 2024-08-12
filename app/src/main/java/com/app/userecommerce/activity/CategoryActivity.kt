package com.app.userecommerce.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.userecommerce.MainActivity
import com.app.userecommerce.R
import com.app.userecommerce.Utils
import com.app.userecommerce.adapter.CategoryProductAdapter
import com.app.userecommerce.databinding.ActivityCategoryBinding
import com.app.userecommerce.model.AddProductModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class CategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryBinding
    private val products = arrayListOf<AddProductModel>()
    private val allProducts = mutableListOf<AddProductModel>()
    private lateinit var adapter: CategoryProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setStatusBarColor()
        binding.gobackBtn.setOnClickListener {
            goback()
        }

        val category = intent.getStringExtra("cat")
        setToolbarText(category)

        adapter = CategoryProductAdapter(this, products)
        binding.categoryProductRV.adapter = adapter

        getCategoryProducts(intent.getStringExtra("cat"))

        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterProducts(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

    }

    private fun setToolbarText(category: String?) {
        binding.toolbarTitle.text = category

    }

    private fun getCategoryProducts(category: String?) {
        FirebaseFirestore.getInstance().collection("products").whereEqualTo("productCategory", category)
            .get().addOnSuccessListener {
                allProducts.clear()
                for (doc in it.documents) {
                    val data = doc.toObject(AddProductModel::class.java)
                    if (data != null) {
                        allProducts.add(data)
                    }
                }
                filterProducts("") // Initially load all products
            }.addOnFailureListener {
                Utils.showToast(this, "Something went wrong")
            }
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

   /* private fun getCategoryProducts(category: String?) {
        val list = ArrayList<AddProductModel>()
        Firebase.firestore.collection("products").whereEqualTo("productCategory", category)
            .get().addOnSuccessListener {

                list.clear()
                for (doc in it.documents) {
                    val data = doc.toObject(AddProductModel::class.java)
                    list.add(data!!)
                }
                val recyclerView = findViewById<RecyclerView>(R.id.categoryProductRV)
                recyclerView.adapter = CategoryProductAdapter(this, list)

            }.addOnFailureListener {
                Utils.showToast(this, "Something went wrong")
            }
    } */


    //set status bar color of activity
    private fun setStatusBarColor() {
        window?.apply {
            val statusBarColors = ContextCompat.getColor(this@CategoryActivity, R.color.yellow)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }


    private fun goback() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}