package com.example.projeto.Feature.Compras;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface ListaComprasApiService {

    @GET("lista-compras/{usuarioId}")
    Call<List<ListaComprasResponse>> gerar(
            @Header("Authorization") String authorization,
            @Path("usuarioId") long usuarioId
    );
}
