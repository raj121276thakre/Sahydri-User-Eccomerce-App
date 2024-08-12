package com.app.userecommerce.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.userecommerce.MainActivity
import com.app.userecommerce.R
import com.app.userecommerce.adapter.BillAdapter
import com.app.userecommerce.databinding.ActivityBillListBinding
import java.io.File

class BillListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBillListBinding
    private lateinit var billAdapter: BillAdapter
    private lateinit var billFiles: List<File>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBillListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setStatusBarColor()

        binding.gobackBtn.setOnClickListener {
            goback()
        }

        setupRecyclerView()
        loadBillFiles()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        billAdapter = BillAdapter { file -> openPdf(file) }
        binding.recyclerView.adapter = billAdapter
    }

    private fun loadBillFiles() {
        val directory = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        billFiles =
            directory?.listFiles { _, name -> name.endsWith(".pdf") }?.toList() ?: emptyList()
        billAdapter.submitList(billFiles)
    }

    private fun openPdf(file: File) {
        val uri = FileProvider.getUriForFile(this, "${applicationContext.packageName}.provider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        startActivity(intent)
    }



    //set status bar color of activity
    private fun setStatusBarColor() {
        window?.apply {
            val statusBarColors = ContextCompat.getColor(this@BillListActivity, R.color.yellow)
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


















