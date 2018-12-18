package com.gmediasolutions.anurakti.model.CandTModel

data class SpecificCandTModelData(var id: String,
                                  var userId: String,
                                  var profileFor: String?=null,
                                  var name: String,
                                  var fatherName: String,
                                  var mobileNumber: String,
                                  var emailId:String,
                                  var industryType:String,
                                  var contactPesron:String,
                                  var address:String,
                                  var hiringFor:String,
                                  var applyingFor:String,
                                  var keywords:String,
                                  var jobExperience:String,
                                  var previousCompany:String,
                                  var resume: String,
                                  var image: String)