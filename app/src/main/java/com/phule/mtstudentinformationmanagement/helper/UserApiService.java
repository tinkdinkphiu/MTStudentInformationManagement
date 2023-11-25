package com.phule.mtstudentinformationmanagement.helper;

import retrofit2.Call;
import retrofit2.http.Body;

import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserApiService {

    @POST("/deleteUser") // Use Path parameter for email
    Call<ApiResponse> deleteUser(@Body UserRespone userRespone);

    class UserRespone {
        String email;

        public UserRespone(String email) {
            this.email = email;
        }
    }

    class ApiResponse {
        public boolean success;
        public String message;
    }
}
