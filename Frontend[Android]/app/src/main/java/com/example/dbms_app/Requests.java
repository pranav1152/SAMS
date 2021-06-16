package com.example.dbms_app;

import android.graphics.Bitmap;
import android.graphics.Camera;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

interface Requests {
    @Multipart
    @POST("post_image")
    Call<String> postimage(@Part MultipartBody.Part image_file);

    @GET("get_a")
    Call<List<Subjects>> get_attendance(
            @Query("uid") String uid
    );

    @POST("post_info")
    Call<String> postinfo(@Body Student student);

    @GET("verify")
    Call<String> verify(
            @Query("username") String username,
            @Query("password") String password
    );
    @GET("student_image")
    Call<Bitmap> getStudentImage();
}
