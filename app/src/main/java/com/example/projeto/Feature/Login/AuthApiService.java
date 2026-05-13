package com.example.projeto.Feature.Login;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("usuarios")
    Call<Void> cadastrar(@Body CadastroRequest request);
}