package com.example.projeto.Feature.Refeicoes;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RefeicaoApiService {

    @GET("refeicoes")
    Call<List<RefeicaoResponse>> listar(
            @Header("Authorization") String authorization,
            @Query("periodo") String periodo,
            @Query("usuarioId") long usuarioId
    );

    @GET("refeicoes/{id}")
    Call<RefeicaoResponse> buscarPorId(
            @Header("Authorization") String authorization,
            @Path("id") long id,
            @Query("usuarioId") Long usuarioId,
            @Query("periodo") String periodo
    );
}
