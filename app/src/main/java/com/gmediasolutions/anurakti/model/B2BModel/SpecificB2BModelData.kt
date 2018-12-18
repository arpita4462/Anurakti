package com.gmediasolutions.anurakti.model.B2BModel

data class SpecificB2BModelData(
        var id: String,
        var userId: String,
        var productName: String? = null,
        var name: String,
        var contactNumber: String,
        var emailId: String,
        var address: String,
        var content: String,
        var areaDescription: String,
        var image: String,
        var quantity: String)