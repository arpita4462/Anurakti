package com.gmediasolutions.anurakti

import com.gmediasolutions.anurakti.model.AllUserModel
import com.gmediasolutions.anurakti.model.ApiReturn
import com.gmediasolutions.anurakti.model.B2BModel.B2BRequest
import com.gmediasolutions.anurakti.model.B2BModel.ProductModel
import com.gmediasolutions.anurakti.model.B2BModel.PropertyModel
import com.gmediasolutions.anurakti.model.B2BModel.SpecificB2BModel
import com.gmediasolutions.anurakti.model.BannerModel
import com.gmediasolutions.anurakti.model.CandTModel.CandTRequest
import com.gmediasolutions.anurakti.model.CandTModel.CompanyModel
import com.gmediasolutions.anurakti.model.CandTModel.JobModel
import com.gmediasolutions.anurakti.model.CandTModel.SpecificCandTModel
import com.gmediasolutions.anurakti.model.LoginModel.LoginRequest
import com.gmediasolutions.anurakti.model.LoginModel.LoginReturn
import com.gmediasolutions.anurakti.model.LoginModel.VerifyEmailModel
import com.gmediasolutions.anurakti.model.NewsModel.SpecificNewsModel
import com.gmediasolutions.anurakti.model.NewsModel.SpecificNewsModelData
import com.gmediasolutions.anurakti.model.NewsModel.UpcomingNE
import com.gmediasolutions.anurakti.model.RegisterModel
import com.gmediasolutions.anurakti.model.UserSocialModel.*
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    //    created api in spring-boot
    @Headers("Content-Type: application/json")
    @POST("/createUser")
    fun register(@Body data: HashMap<String, RegisterModel>): Call<RegisterModel>


    //    Api for testing of data
    @GET("/api/users")
    fun getdetail(@Query("page") page: String): Call<AllUserModel>

//    //    Api for testing -one user data
//    @GET("/api/users")
//    fun filter(@Query("page") page: String): Call<AllUserModel>


    /*main page api */
//    @Headers("Content-Type: application/json")
//    @POST("/api/1.0/registration")
//    fun registration(@Body data: HashMap<String, SignUpModel>): Call<ApiReturn>

    @Headers("Content-Type: application/json")
    @POST("/api/1.0/login")
    fun login(@Body data: HashMap<String, LoginRequest>): Call<LoginReturn>

//    @Headers("Content-Type: application/json")
//    @POST("/api/1.0/forgotPassword")
//    fun forgotPassword(@Body data: HashMap<String, ForgetPassRequest>): Call<ApiReturn>

    @GET("/api/1.0/banner/getBanners")
    fun getBanners(): Call<BannerModel>


    @GET("/api/1.0/verify/{userId}")
    fun verify(@Path("userId") userId: String): Call<VerifyEmailModel>

    @GET("/api/1.0/logout")
    fun logout(): Call<VerifyEmailModel>

    /*my profile apis*/

    @Headers("Content-Type: application/json")
    @PUT("/api/1.0/addUser/{userId}")
    fun addUser(@Path("userId") userId: String, @Body data: HashMap<String, UserProfileRequest>): Call<ApiReturn>


    @GET("/api/1.0/user/{userId}")
    fun user(@Path("userId") userId: String): Call<UserModel>

    @GET("/api/1.0/allUser")
    fun allUser(): Call<AllUserModel>


    /*Timeline apis*/
//    @Headers("Content-Type: application/json")
//    @POST("/api/1.0/addTimeline")
//    fun addTimeline(@Body data: HashMap<String, TimelineRequest>): Call<TimelineReturn>

    @GET("/api/1.0/getTimeline/{userId}")
    fun getTimeline(@Path("userId") userId: String): Call<TimeLineModel>

    @GET("/api/1.0/getNameOfTimelinePic/{timelneId}")
    fun getNameOfTimelinePic(@Path("timelneId") timelneId: String): Call<LikeNameModel>

    @GET("/api/1.0/timelineLikeStatus/{userId}/{timelneId}")
    fun timelineLikeStatus(@Path("userId") userId: String, @Path("timelneId") timelneId: String): Call<LikeStatusModel>

//    @Headers("Content-Type: application/json")
//    @POST("/api/1.0/shareTimeline/{timeineId}")
//    fun shareTimeline(@Path("timeineId") timelneId: String, @Body data: HashMap<String, ShareRequest>): Call<ApiReturn>
//
//    @Headers("Content-Type: application/json")
//    @POST("/api/1.0/shareTimelineInFriendProfile/{timeineId}")
//    fun shareTimelineInFriendProfile(@Path("timeineId") timelneId: String, @Body data: HashMap<String, ShareFrndRequest>): Call<ApiReturn>
//
//    @Headers("Content-Type: application/json")
//    @POST("/api/1.0/timelineLike")
//    fun timelineLike(@Body data: HashMap<String, LikeRequest>): Call<ApiReturn>
//
//    @Headers("Content-Type: application/json")
//    @POST("/api/1.0/timelineComment")
//    fun timelineComment(@Body data: HashMap<String, TimelineCommentRequest>): Call<ApiReturn>

    @Headers("Content-Type: application/json")
    @PUT("/api/1.0/editTimelineComment/{commentId}")
    fun editTimelineComment(@Path("commentId") commentId: String, @Body data: HashMap<String, EditTimelineRequest>): Call<ApiReturn>

    @GET("/api/1.0/countTimelineLike/{timelineId}")
    fun countTimelineLike(@Path("timelineId") timelneId: String): Call<CountLikeData>

    @GET("/api/1.0/getTimelineComment/{timelineId}")
    fun getTimelineComment(@Path("timelineId") timelneId: String): Call<TimelineCommentModel>

//    @DELETE("/api/1.0/deleteTimelineComment/{commentId}")
//    fun deleteTimelineComment(@Path("commentId") commentId: String): Call<ApiReturn>

//    @Headers("Content-Type: application/json")
//    @POST("/api/1.0/deleteTimeline/{timelineId}")
//    fun deleteTimeline(@Path("timelineId") timelineId: String): Call<ApiReturn>


    /*Profile Pic apis*/
//    @Headers("Content-Type: application/json")
//    @POST("/api/1.0/updateProfilePic")
//    fun updateProfilePic(@Body data: HashMap<String, ProfilePicRequest>): Call<ApiReturn>

    @GET("/api/1.0/getAllProfilePic/{userId}")
    fun getAllProfilePic(@Path("userId") userId: String): Call<AllProfilePicModel>

//    @Headers("Content-Type: application/json")
//    @POST("/api/1.0/profilePicComment")
//    fun profilePicComment(@Body data: HashMap<String, ProfilePicCommentRequest>): Call<ApiReturn>

    @Headers("Content-Type: application/json")
    @PUT("/api/1.0/editProfilePicComment/{commentId} ")
    fun editProfilePicComment(@Path("commentId") commentId: String, @Body data: HashMap<String, EditProfilePicCommentRequest>): Call<ApiReturn>

    @GET("/api/1.0/getPicComment/{picId}")
    fun getPicComment(@Path("picId") picId: String): Call<ProfilePicCommentModel>

//    @DELETE("/api/1.0/deleteProfilePicComment/{commentId}")
//    fun deleteProfilePicComment(@Path("commentId") commentId: String): Call<ApiReturn>

    /*Notifications apis*/
//    @Headers("Content-Type: application/json")
//    @POST("/api/1.0/notification")
//    fun notification(@Body data: HashMap<String, NotificationRequest>): Call<ApiReturn>


    /*Cover Pic apis*/
//    @Headers("Content-Type: application/json")
//    @POST("/api/1.0/updateCoverPic")
//    fun updateCoverPic(@Body data: HashMap<String, CoverPicRequest>): Call<ApiReturn>

    @GET("/api/1.0/getCoverPic/{userId}")
    fun getCoverPic(@Path("userId") userId: String): Call<AllCoverPicModel>

    @GET("/api/1.0/getNotification/{userId}")
    fun getNotification(@Path("userId") userId: String): Call<NotificationModel>

//    @Headers("Content-Type: application/json")
//    @POST("/api/1.0/coverPicComment")
//    fun coverPicComment(@Body data: HashMap<String, CoverPicCommentRequest>): Call<ApiReturn>

    @Headers("Content-Type: application/json")
    @PUT("/api/1.0/editCoverPicComment/{commentId} ")
    fun editCoverPicComment(@Path("commentId") commentId: String, @Body data: HashMap<String, EditProfilePicCommentRequest>): Call<ApiReturn>

    @GET("/api/1.0/getCoverPicComment/{picId}")
    fun getCoverPicComment(@Path("picId") picId: String): Call<CoverPicCommentModel>

//    @DELETE("/api/1.0/deleteCoverPicComment/{commentId}")
//    fun deleteCoverPicComment(@Path("commentId") commentId: String): Call<ApiReturn>


    /*Friends Apis*/
    @GET("/api/1.0/getFriends/{userId}")
    fun getFriends(@Path("userId") userId: String): Call<GetFriendModel>

//    @Headers("Content-Type: application/json")
//    @POST("/api/1.0/sendRequest")
//    fun sendRequest(@Body data: HashMap<String, AddFrndRequest>): Call<ApiReturn>

    @GET("/api/1.0/getFriendRequest/{friendId}")
    fun getFriendRequest(@Path("friendId") friendId: String): Call<GetFrndReqModel>

    @GET("/api/1.0/getFriendSuggestion/{userId}")
    fun getFriendSuggestion(@Path("userId") userId: String): Call<GetFrndSuggModel>

    @GET("/api/1.0/sentRequest/{userId}")
    fun sentRequest(@Path("userId") userId: String): Call<GetFrndReqModel>

    @GET("/api/1.0/getFriendStatus/{id}/{f_id} ")
    fun getFriendStatus(@Path("id") id: String, @Path("f_id") f_id: String): Call<FrndStatusModel>

//    @DELETE("/api/1.0/deleteFriend/{id}")
//    fun deleteFriend(@Path("id") id: String): Call<ApiReturn>

    /*news apis*/

    @GET("/api/1.0/getNews")
    fun getNews(): Call<UpcomingNE>

    @GET("/api/1.0/getNews/{id}")
    fun getNews(@Path("id") id: String): Call<SpecificNewsModel>

//    @Headers("Content-Type: application/json")
//    @POST("/api/1.0/addNews")
//    fun addNews(@Body data: HashMap<String, SpecificNewsModelData>): Call<ApiReturn>

    @Headers("Content-Type: application/json")
    @PUT("/api/1.0/deleteNews/{id} ")
    fun deleteNews(@Path("id") id: String): Call<ApiReturn>

    /*B2B apis*/
    @GET("/api/1.0/getProducts")
    fun getProducts(): Call<ProductModel>

    @GET("/api/1.0/getProperty")
    fun getProperty(): Call<PropertyModel>

    @GET("/api/1.0/getSpecificProduct/{id}")
    fun getSpecificProduct(@Path("id") id: String): Call<SpecificB2BModel>

//    @Headers("Content-Type: application/json")
//    @POST("/api/1.0/addProduct")
//    fun addProduct(@Body data: HashMap<String, B2BRequest>): Call<ApiReturn>

    @Headers("Content-Type: application/json")
    @PUT("/api/1.0/editProduct/{id}")
    fun editProduct(@Path("id") id: String, @Body data: HashMap<String, B2BRequest>): Call<ApiReturn>

    @Headers("Content-Type: application/json")
    @PUT("/api/1.0/deleteProduct/{id} ")
    fun deleteProduct(@Path("id") id: String): Call<ApiReturn>

    /*Career and Talent apis*/
    @GET("/api/1.0/getCompany")
    fun getCompany(): Call<CompanyModel>

    @GET("/api/1.0/getJobSeeker")
    fun getJobSeeker(): Call<JobModel>

    @GET("/api/1.0/getCareerDetails/{id}")
    fun getCareerDetails(@Path("id") id: String): Call<SpecificCandTModel>

//    @Headers("Content-Type: application/json")
//    @POST("/api/1.0/addProfile")
//    fun addProfile(@Body data: HashMap<String, CandTRequest>): Call<ApiReturn>

    @Headers("Content-Type: application/json")
    @PUT("/api/1.0/editProfile/{id}")
    fun editProfile(@Path("id") id: String, @Body data: HashMap<String, CandTRequest>): Call<ApiReturn>

    @Headers("Content-Type: application/json")
    @PUT("/api/1.0/deleteCarrerAndTalent/{id} ")
    fun deleteCarrerAndTalent(@Path("id") id: String): Call<ApiReturn>


}