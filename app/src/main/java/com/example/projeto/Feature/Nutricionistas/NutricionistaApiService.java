package com.example.projeto.Feature.Nutricionistas;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface NutricionistaApiService {

    @GET("nutricionistas")
    Call<List<Nutricionista>> listar(
            @Header("Authorization") String token
    );
}