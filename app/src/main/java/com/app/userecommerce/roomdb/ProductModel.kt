package com.app.userecommerce.roomdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import javax.annotation.Nonnull
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
//
//@Entity(tableName = "products")
//data class ProductModel(
//    @PrimaryKey
//    @Nonnull
//    val productId : String,
//    @ColumnInfo(name = "productName") val productName: String?,
//    @ColumnInfo(name = "productImages") val productImages: String?,
//   // @ColumnInfo(name = "productSp") val productSp: String?,
//    @ColumnInfo(name = "productTotal") val productTotal: String?,
//    @ColumnInfo(name = "productQuantity") val productQuantity: String?,
//)




@Parcelize
@Entity(tableName = "products")
data class ProductModel(
    @PrimaryKey
    @Nonnull
    val productId: String,
    @ColumnInfo(name = "productName") val productName: String?,
    @ColumnInfo(name = "productImages") val productImages: String?,
    @ColumnInfo(name = "productTotal") val productTotal: String?,
    @ColumnInfo(name = "productQuantity") val productQuantity: String?
) : Parcelable
