package com.app.userecommerce.activity

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.app.userecommerce.MainActivity
import com.app.userecommerce.R
import com.app.userecommerce.Utils
import com.app.userecommerce.databinding.ActivityAddressBinding
import com.app.userecommerce.model.Bill
import com.app.userecommerce.model.ProductDetail
import com.app.userecommerce.roomdb.AppDatabase
import com.app.userecommerce.roomdb.ProductModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddressActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddressBinding

    private lateinit var preferences: SharedPreferences //
    private lateinit var totalCost: String //
    private lateinit var quantity: String //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setStatusBarColor()

        preferences = this.getSharedPreferences("user", MODE_PRIVATE) //

        totalCost = intent.getStringExtra("totalCost")!!
        quantity = intent.getStringExtra("quantity")!!

        loadUserInfo()

        binding.proceedBtn.setOnClickListener {
            validateData(
                binding.userNumber.text.toString(),
                binding.userName.text.toString(),
                binding.userPincode.text.toString(),
                binding.userCity.text.toString(),
                binding.userState.text.toString(),
                binding.userVillage.text.toString(),
            )
        }


    }

    private fun validateData(
        number: String,
        name: String,
        pinCode: String,
        city: String,
        state: String,
        village: String
    ) {

        if (number.isEmpty() || name.isEmpty() || pinCode.isEmpty() || city.isEmpty() || state.isEmpty() || village.isEmpty()) {

            Utils.showToast(this@AddressActivity, "Please fill all fields")
        } else {

            Utils.showDialog(this@AddressActivity, "Loading please wait")
            storeData(
                pinCode,
                city,
                state,
                village
            )
        }

    }

    private fun storeData(pinCode: String, city: String, state: String, village: String) {

        val map = hashMapOf<String, Any>()
        map["pinCode"] = pinCode
        map["city"] = city
        map["state"] = state
        map["village"] = village

        Firebase.firestore.collection("users")
            .document(preferences.getString("number", "")!!)
            .update(map)
            .addOnSuccessListener {
                Utils.hideDialog()
                // Utils.showToast(this@AddressActivity, "Address saved")
                // Show Payment Options Dialog
                showPaymentOptionsDialog()
                //goto checkoutActivity
//                val bundle = Bundle()
//                bundle.putStringArrayList("productIds",intent.getStringArrayListExtra("productIds"))
//                bundle.putString("totalCost",totalCost)
//
//                val intent = Intent(this, CheckoutActivity::class.java)
//                intent.putExtras(bundle)
//               startActivity(intent)

            }.addOnFailureListener {
                Utils.hideDialog()
                Utils.showToast(this@AddressActivity, "Something went wrong")
            }
    }


    private fun loadUserInfo() {


        Firebase.firestore.collection("users")
            .document(preferences.getString("number", "9786567545")!!)
            .get().addOnSuccessListener {

                val userName = it.getString("userName")
                val userPhoneNumber = it.getString("userPhoneNumber")
                val village = it.getString("village")
                val city = it.getString("city")
                val pinCode = it.getString("pinCode")
                val state = it.getString("state")

                binding.userName.setText(userName)
                binding.userNumber.setText(userPhoneNumber)
                binding.userVillage.setText(village)
                binding.userCity.setText(city)
                binding.userPincode.setText(pinCode)
                binding.userState.setText(state)

                // Save full address in SharedPreferences
                val fullAddress = "$village, $city, $state, $pinCode"
                preferences.edit().putString("userName", userName).apply()
                preferences.edit().putString("fullAddress", fullAddress).apply()
                preferences.edit().putString("userPhoneNumber", userPhoneNumber).apply()

            }.addOnFailureListener {

                Utils.showToast(this@AddressActivity, "Something went wrong")
            }
    }


    private fun showPaymentOptionsDialog() {
        // Example dialog implementation (customize as needed)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Select Payment Method")
            .setItems(arrayOf("Cash on Delivery")) { dialog, which ->
                when (which) {
                    0 -> {
                        // Proceed with Cash on Delivery
                        processCashOnDelivery()
                    }

//                    1 -> {
//                        // Implement UPI Payment using Razorpay (your implementation here)
//                        val bundle = Bundle()
//                        bundle.putStringArrayList(
//                            "productIds",
//                            intent.getStringArrayListExtra("productIds")
//                        )
//                        bundle.putString("totalCost", totalCost)
//
//                        val intent = Intent(this, CheckoutActivity::class.java)
//                        intent.putExtras(bundle)
//                        startActivity(intent)
//
//                    }
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }

    private fun processCashOnDelivery() {
        Utils.showToast(this@AddressActivity, "order successful")
        uploadData()
    }

    private fun uploadData() {
        Utils.showDialog(this, "Placing your orders...")
        val id = intent.getStringArrayListExtra("productIds")
        for (currentId in id!!) {
            fetchData(currentId)
        }
    }

    private fun fetchData(productId: String?) {

        val dao = AppDatabase.getInstance(this).productDao()

        Firebase.firestore.collection("products")
            .document(productId!!).get()
            .addOnSuccessListener {

                lifecycleScope.launch(Dispatchers.IO) {
                    dao.deleteProduct(ProductModel(productId, "", "", "", ""))
                }

                saveData(it.getString("productName"), quantity,it.getString("productSp"), productId)

            }.addOnFailureListener {
                Utils.hideDialog()
                Utils.showToast(this@AddressActivity, "Something went wrong")
            }

    }

    private fun saveData(name: String?,quantity:String?, price: String?, productId: String) {
        val preferences = this.getSharedPreferences("user", MODE_PRIVATE) //
        val data = hashMapOf<String, Any>()
        data["name"] = name!!
        data["quantity"] = quantity!!
        data["price"] = price!!
        data["productId"] = productId!!
        data["userId"] = preferences.getString("number", "")!!
        data["status"] = "Ordered"

        val firestore = Firebase.firestore.collection("allOrders")
        val key = firestore.document().id
        data["orderId"] = key

        firestore.document(key).set(data).addOnSuccessListener {
            Utils.hideDialog()
            Utils.showToast(this@AddressActivity, "Order placed")

            // Generate the bill.....
            generateBill(name,quantity, price, productId, key)


            // Go to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
            .addOnFailureListener {
                Utils.hideDialog()
                Utils.showToast(this@AddressActivity, "Something went wrong")
            }
    }


    private fun generateBill(
        productName: String?,
        productQuantity: String,
        productPrice: String?,
        productId: String,
        orderId: String
    ) {
        val preferences = this.getSharedPreferences("user", MODE_PRIVATE)
        val userName = preferences.getString("userName", "")!!
        val userPhoneNumber = preferences.getString("userPhoneNumber", "")!!
        val userAddress = "${preferences.getString("fullAddress", "")!!}"
        val orderDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())


        val productDetails = listOf(ProductDetail(productName!!, productPrice!!, productQuantity.toInt(), productId) ) //add quantity

        // Get Firestore instance
        val firestore = FirebaseFirestore.getInstance()
        val userOrdersRef = firestore.collection("bills").whereEqualTo("userPhoneNumber", userPhoneNumber)

        userOrdersRef.get().addOnSuccessListener { querySnapshot ->
            val totalOrders = querySnapshot.size()
            val newOrderNumber = totalOrders + 1

            val orderNumber = newOrderNumber


        val bill = Bill(
            userName,
            userPhoneNumber,
            userAddress,
            productDetails,
            totalCost,
            orderId,
            orderDate,
            totalCost.toDouble().toString(),
            "Cash on Delivery",
            "0.00",
            totalCost,
            "Ordered",          // New field: initial status
            timestamp,          // New field: timestamp
            orderNumber         // New field: order number
        )

        saveBillToFirestore(bill)


        }.addOnFailureListener { exception ->
            Utils.showToast(this, "Failed to generate bill")
        }
    }


    private fun saveBillToFirestore(bill: Bill) {
//        val firestore = FirebaseFirestore.getInstance().collection("bills")
//        val key = firestore.document().id
//
//        firestore.document(key).set(bill).addOnSuccessListener {
//            Utils.showToast(this@AddressActivity, "Bill generated")
//            generateBillPdf(bill)
//        }
//            .addOnFailureListener { exception ->
//                Utils.showToast(
//                    this@AddressActivity,
//                    "Failed to generate bill: ${exception.message}"
//                )
//                Log.e("FirestoreError", "Error generating bill", exception)
//            }

        val firestore = FirebaseFirestore.getInstance().collection("bills")

        firestore.document(bill.orderId).set(bill).addOnSuccessListener {
            Utils.showToast(this, "Bill generated")
            generateBillPdf(bill) // Function to generate the PDF bill
        }.addOnFailureListener { exception ->
            Utils.showToast(this, "Failed to generate bill")
        }
    }







    private fun generateBillPdf(bill: Bill) {

        // Get user name and full address from SharedPreferences
        val userName = preferences.getString("userName", "")
        val fullAddress = preferences.getString("fullAddress", "")

        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)

        val canvas = page.canvas
        val textPaint = Paint().apply {
            textSize = 14f
            color = Color.BLACK
        }
        val lineHeight = textPaint.descent() - textPaint.ascent()

        val boldTextPaint = Paint(textPaint).apply {
            isFakeBoldText = true
        }

        val titlePaint = Paint().apply {
            textSize = 16f
            isFakeBoldText = true
        }

        val headerPaint = Paint().apply {
            textSize = 14f
            isFakeBoldText = true
        }

        val borderPaint = Paint().apply {
            style = Paint.Style.STROKE
            color = Color.BLACK
        }

        val backgroundPaint = Paint().apply {
            color = Color.parseColor("#ADD8E6") // Light blue
        }

        var startX = 10f
        var startY = 20f

        // Draw company name and address with background color
        canvas.drawRect(startX, startY - lineHeight, 575f, startY + lineHeight * 3, backgroundPaint)
        canvas.drawText("                              Shreenath Cosmetic And Agency Shevgaon", startX, startY, titlePaint)
        startY += lineHeight
        canvas.drawText("                                             Archana Tower Aakhegaon Road Shevgaon", startX, startY, textPaint)
        startY += lineHeight
        canvas.drawText(
            "          Contact no: 9689311664                   Email: shreenathcosmeticandagency@gmail.com",
            startX,
            startY,
            textPaint
        )
        startY += lineHeight + 20

        // Draw the bill details
        canvas.drawText("\nBill To:  $userName", startX, startY, headerPaint)
        startY += lineHeight
        canvas.drawText("\nAddress:  $fullAddress", startX, startY, headerPaint)
        startY += lineHeight
        canvas.drawText("\nDate:  ${bill.orderDate}", startX, startY, headerPaint)
        startY += lineHeight + 20

        // Define column widths and starting positions
        val itemNameColWidth = 150f
        val mrpColWidth = 100f
        val quantityColWidth = 100f
        val pricePerUnitColWidth = 100f
        val amountColWidth = 100f
        val tableWidth =
            itemNameColWidth + mrpColWidth + quantityColWidth + pricePerUnitColWidth + amountColWidth

// Draw table header
        canvas.drawRect(startX, startY, startX + tableWidth, startY + lineHeight + 10, borderPaint)
        canvas.drawText("Item Name", startX + 5, startY + lineHeight, headerPaint)
        canvas.drawText("MRP", startX + itemNameColWidth + 10, startY + lineHeight, headerPaint)
        canvas.drawText(
            "Quantity",
            startX + itemNameColWidth + mrpColWidth + 10,
            startY + lineHeight,
            headerPaint
        )
        canvas.drawText(
            "Price/Unit",
            startX + itemNameColWidth + mrpColWidth + quantityColWidth + 10,
            startY + lineHeight,
            headerPaint
        )
        canvas.drawText(
            "Amount",
            startX + itemNameColWidth + mrpColWidth + quantityColWidth + pricePerUnitColWidth + 10,
            startY + lineHeight,
            headerPaint
        )
        startY += lineHeight + 10

// Draw table content
        for (product in bill.productDetails) {
            canvas.drawRect(
                startX,
                startY,
                startX + tableWidth,
                startY + lineHeight + 10,
                borderPaint
            )
            canvas.drawText(product.productName, startX + 5, startY + lineHeight, textPaint)
            canvas.drawText(
                product.productPrice,
                startX + itemNameColWidth + 10,
                startY + lineHeight,
                textPaint
            )
            canvas.drawText(
                product.productQuantity.toString(),
                startX + itemNameColWidth + mrpColWidth + 10,
                startY + lineHeight,
                textPaint
            )  // units
            canvas.drawText(
                product.productPrice,
                startX + itemNameColWidth + mrpColWidth + quantityColWidth + 10,
                startY + lineHeight,
                textPaint
            )
            canvas.drawText(
                totalCost,
                startX + itemNameColWidth + mrpColWidth + quantityColWidth + pricePerUnitColWidth + 10,
                startY + lineHeight,
                textPaint
            )
            startY += lineHeight + 10
        }

        // Draw total cost and other details
        val rightColumnStartX = 300f
        val totalCostY = startY + 20
        val paymentDetailsY = totalCostY + lineHeight + 10
        val balanceY = paymentDetailsY + lineHeight + 10

        canvas.drawText("Total Cost: ${bill.totalCost}", rightColumnStartX, totalCostY, headerPaint)
        canvas.drawText(
            "Payment Method: ${bill.paymentMode}",
            rightColumnStartX,
            paymentDetailsY,
            headerPaint
        )
        canvas.drawText("Balance: ${bill.balance}", rightColumnStartX, balanceY, headerPaint)

        // Close the page and finish the document
        document.finishPage(page)


        // Save the document
        val productName = bill.productDetails.joinToString("_") { it.productName }
        val fileName = "Bill_${productName}_${bill.orderId}_${bill.orderDate}.pdf"
        val filePath =
            getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath + File.separator + fileName
        val file = File(filePath)

        try {
            document.writeTo(FileOutputStream(file))
            Utils.showToast(this, "PDF saved to $filePath")
        } catch (e: IOException) {
            Utils.showToast(this, "Error saving PDF: ${e.message}")
            e.printStackTrace()
        }

        document.close()
    }


    //set status bar color of activity
    private fun setStatusBarColor() {
        window?.apply {
            val statusBarColors = ContextCompat.getColor(this@AddressActivity, R.color.yellow)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

}









