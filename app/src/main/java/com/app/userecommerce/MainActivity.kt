package com.app.userecommerce

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.app.userecommerce.activity.BillListActivity
import com.app.userecommerce.activity.SearchProductsActivity
import com.app.userecommerce.auth.LoginActivity
import com.app.userecommerce.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var toolbarTitle: TextView

    var i = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setStatusBarColor()

        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.logoutIcon.setOnClickListener {
            showLogoutDialog()

        }

        binding.userProfile.setOnClickListener {
          // showUserDetailsBottomSheet()

        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        val navController = navHostFragment!!.findNavController()

        val popupMenu = PopupMenu(this, null)
        popupMenu.inflate(R.menu.bottom_nav)
        binding.bottomBar.setupWithNavController(popupMenu.menu, navController)

        binding.bottomBar.onItemSelected = {
            when (it) {
                0 -> {
                    i = 0;
                    navController.navigate(R.id.homeFragment)
                }

                1 -> i = 1
                2 -> i = 2
            }
        }

        binding.seachBtn.setOnClickListener {
            val intent = Intent(this, SearchProductsActivity::class.java)
            startActivity(intent)
        }

        binding.billIcon.setOnClickListener {
            val intent = Intent(this, BillListActivity::class.java)
            startActivity(intent)

        }



        changeToolbarTitle(navController)


    }

    private fun changeToolbarTitle(navController: NavController) {
        // Initialize toolbarTitle
        toolbarTitle = findViewById(R.id.toolbar_title)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> toolbarTitle.text = "Home"
                R.id.cartFragment -> toolbarTitle.text = "Cart"
                // Add more cases as needed
                R.id.ordersFragment -> toolbarTitle.text = "Orders"



                else -> toolbarTitle.text = getString(R.string.app_name).toString() // Default title
            }
        }

    }


    override fun onBackPressed() {
        super.onBackPressed()
        if (i == 0) {
            finish()
        }
    }


    //set status bar color of activity
    private fun setStatusBarColor() {
        window?.apply {
            val statusBarColors = ContextCompat.getColor(this@MainActivity, R.color.yellow)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }



    private fun showUserDetailsBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_user_details, null)

        // Assuming you have user details in shared preferences or another source
        val preferences = getSharedPreferences("user", MODE_PRIVATE)
        val userName = preferences.getString("name", "Unknown User")
        val userPhone = preferences.getString("number", "Unknown Phone")
        val userAddress = preferences.getString("address", "Unknown Address")

        bottomSheetView.findViewById<TextView>(R.id.tv_user_name).text = userName
        bottomSheetView.findViewById<TextView>(R.id.tv_user_phone).text = userPhone
        bottomSheetView.findViewById<TextView>(R.id.tv_user_address).text = userAddress

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }


    private fun showLogoutDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_logout, null)

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.btn_yes).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        dialogView.findViewById<Button>(R.id.btn_no).setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }


}

















