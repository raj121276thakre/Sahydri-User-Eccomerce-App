package com.app.userecommerce.model

//data class AddProductModel(
//    val productName : String? = "",
//    val productDescription : String? = "",
//    val productCoverImg : String? = "",
//    val productCategory : String? = "",
//    val productId : String? = "",
//    val productMrp : String? = "",
//    val productSp : String? = "",
//    val productImages : ArrayList<String> = ArrayList(),
//
//    val productQuantity : String? = "",
//    val productDiscount : String? = "",
//){
//
//    constructor() : this("", "", "", "", "", "", "", arrayListOf() )
//}

data class AddProductModel(
    val productName: String? = "",
    val productDescription: String? = "",
    val productCoverImg: String? = "",
    val productCategory: String? = "",
    val productId: String? = "",
    val productMrp: String? = "",
    val productSp: String? = "",
    val productImages: ArrayList<String> = ArrayList(),
    val productQuantity: String? = "",
    val productDiscount: String? = "",
    val stability: Long = 0L // Adjust type to Long if stability is a Long in Firestore
) {
    constructor() : this("", "", "", "", "", "", "", arrayListOf(), "", "",0L)
}


