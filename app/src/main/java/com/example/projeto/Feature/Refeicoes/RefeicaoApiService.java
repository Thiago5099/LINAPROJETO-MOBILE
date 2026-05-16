package com.example.projeto.Feature.Refeicoes;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface RefeicaoApiService {

    @GET("refeicoes")
    Call<List<RefeicaoResponse>> listar(
            @Header("Authorization") String authorization,
            @Query("periodo") String periodo,
            @Query("usuarioId") long usuarioId
    );
}
