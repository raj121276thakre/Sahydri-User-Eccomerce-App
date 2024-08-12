package com.app.userecommerce.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.app.userecommerce.MainActivity
import com.app.userecommerce.R
import com.app.userecommerce.Utils
import com.app.userecommerce.databinding.ActivityProductDetailsBinding
import com.app.userecommerce.roomdb.AppDatabase
import com.app.userecommerce.roomdb.ProductDao
import com.app.userecommerce.roomdb.ProductModel
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailsBinding
    private lateinit var firestore: FirebaseFirestore
    private var productId: String? = null

    private lateinit var list : ArrayList<String>
    private var quantity = 1
    private var totalPrice = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setStatusBarColor() // set status bar color

        //getProductDetails(intent.getStringExtra("id"))
        firestore = FirebaseFirestore.getInstance()
        productId = intent.getStringExtra("id")

        // Load product details and setup UI
        getProductDetails(productId)

        list = ArrayList()



    }


    private fun InitalToatlPrice(productSp: String?) {
        val productPrice = productSp!!.replace("₹", "").toDouble()
        totalPrice = (quantity * productPrice).toInt() // Convert total price to integer
        binding.tvTotalPrice.text = "Total Price: ₹$totalPrice"
    }

    private fun updateTotalPrice(productSp: String?) {
        val productPrice = productSp!!.replace("₹", "").toDouble()
        totalPrice = (quantity * productPrice).toInt() // Convert total price to integer
        binding.tvTotalPrice.text = "Total Price: ₹$totalPrice"
    }

//    private fun updateTotalPrice(productSp: String?) {
//
//           val productPrice = productSp!!.toInt()
//           totalPrice = quantity * productPrice // Calculate total price based on quantity and MRP
//          binding.tvTotalPrice.text = "Total Price: ₹$totalPrice"
//
//    }

    private fun getProductDetails(proId: String?) {

        Firebase.firestore.collection("products").document(proId!!)
            .get().addOnSuccessListener {

                val imagelist = it.get("productImages") as ArrayList<String>
                val name =  it.getString("productName")
                val productSp =  "₹" + it.getString("productSp")
                val prodDescription = it.getString("productDescription")

                val productQuantity = it.getString("productQuantity")
                val productDiscount = it.getString("productDiscount")
                val productMrp = it.getString("productMrp")

                // Format the product MRP with a strikethrough using HTML
                val formattedProductMrp = Utils.applyStrikethrough("₹$productMrp")

                binding.tvProductName.text = name
                binding.tvProductSp.text = productSp
                binding.tvProductDescription.text = prodDescription

              //  binding.tvQuantity.text = productQuantity
                binding.tvProductDiscount.text = productDiscount
                binding.tvProductMrp.text = formattedProductMrp
                binding.etQuantity.setText(quantity.toString())

                val slideList = ArrayList<SlideModel>()
                for (data in imagelist) {
                    slideList.add(
                        SlideModel(
                            data,
                            ScaleTypes.CENTER_INSIDE  // image scale type add here
                        )
                    )
                }

                InitalToatlPrice(it.getString("productSp") )

                binding.etQuantity.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        val input = s.toString()
                        if (input.isNotEmpty()) {
                            quantity = input.toIntOrNull() ?: 1
                            if (quantity < 1) {
                                quantity = 1
                                Toast.makeText(this@ProductDetailsActivity, "Quantity cannot be less than 1", Toast.LENGTH_SHORT).show()
                            }
                            updateTotalPrice(it.getString("productSp") )
                        }
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                })


                //updateTotalPrice(it.getString("productSp"))

                cartAction(proId, name , totalPrice.toString() , it.getString("productCoverImg"))




                binding.imageSlider.setImageList(slideList)

            }.addOnFailureListener {
                Utils.showToast(this, "Something went wrong")
            }

    }


    private fun cartAction(prodId: String, name: String?, producttotal: String?, coverImg: String?) {
        val productDao = AppDatabase.getInstance(this).productDao()

        if (productDao.isExit(prodId) != null) {
            binding.tvBtnAddToCart.text = "Go to cart"

        } else {
            binding.tvBtnAddToCart.text = "Add to cart"

        }

        binding.tvBtnAddToCart.setOnClickListener {
            if (productDao.isExit(prodId) != null) {
                openCart()

            } else {
                //addToCart(productDao, prodId, name, productSp, coverImg)
                addToCart(productDao, prodId, name, totalPrice.toString(), coverImg, quantity.toString())


            }
        }
        list.add(prodId)

        binding.tvBtnBuyNow.setOnClickListener {

            val intent = Intent(this, AddressActivity::class.java)
            val bundle = Bundle()
            bundle.putStringArrayList("productIds",list)
            bundle.putString("totalCost",totalPrice.toString())
            bundle.putString("quantity",quantity.toString())
            intent.putExtras(bundle)
//            intent.putExtra("totalCost",total)
//            intent.putExtra("productIds",list)

            startActivity(intent)
        }


    }

    private fun addToCart(
        productDao: ProductDao,
        prodId: String,
        name: String?,
        productTotal: String?,
        coverImg: String?,
        productQuantity: String?
    ) {

        val product = ProductModel(
            productId =prodId,
            productName = binding.tvProductName.text.toString(),
            productQuantity = binding.etQuantity.text.toString(),
            productTotal = productTotal,
            productImages = coverImg
        )

        //val data = ProductModel(prodId, name, coverImg,productTotal,productQuantity)
        lifecycleScope.launch (Dispatchers.IO){
            productDao.insertProduct(product)
            binding.tvBtnAddToCart.text = "Go to cart"

        }
    }

    private fun openCart() {
        val preference = this.getSharedPreferences("info", MODE_PRIVATE)
        val editor = preference.edit()
        editor.putBoolean("isCart", true)
        editor.apply()

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()

    }





//set status bar color of activity
    private fun setStatusBarColor() {
        window?.apply {
            val statusBarColors = ContextCompat.getColor(this@ProductDetailsActivity, R.color.yellow)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }



}



















