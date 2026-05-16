package com.example.projeto.Feature.Perfil;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PerfilApiService {

    @GET("usuarios/{id}")
    Call<UsuarioResponse> buscarPerfil(
            @Header("Authorization") String token,
            @Path("id") Long id
    );

    @PUT("usuarios/{id}")
    Call<UsuarioResponse> atualizarPerfil(
            @Header("Authorization") String token,
            @Path("id") Long id,
            @Body UsuarioUpdateRequest body
    );
}