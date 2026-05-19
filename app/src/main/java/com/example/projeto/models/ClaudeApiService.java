package com.example.projeto.models;

import com.example.projeto.BuildConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ClaudeApiService {

    private static final String API_URL = "https://api.anthropic.com/v1/messages";
    private static final String API_KEY = BuildConfig.CLAUDE_API_KEY;
    private static final String MODEL = "claude-sonnet-4-5";

    private static final String SYSTEM_PROMPT = "Você é um assistente de pratos para pessoas com diabetes, " +
            "doença celíaca e intolerância à lactose. " +
            "Ajude o usuário a encontrar pratos COM ou SEM ingredientes específicos. " +
            "Responda sempre em português, de forma curta e direta. " +
            "Ao listar pratos, mostre o nome, período e ingredientes principais. " +
            "Use apenas os pratos disponíveis abaixo, não invente outros. " +
            "Para cada prato listado, explique brevemente por que ele foi selecionado " +
            "com base na solicitação do usuário. Por exemplo: se o usuário pediu frango sem lactose, " +
            "explique se o prato foi incluído por ter frango, por ser sem lactose, ou por atender ambos os critérios. " +
            "Se o usuário perguntar algo que não seja relacionado a busca de pratos ou alimentação, " +
            "responda apenas: 'Só posso ajudar com a busca de pratos do cardápio. Como posso ajudar?' " +
            "Aqui estão os pratos disponíveis: " + getPratosJson();
    public interface Callback {
        void onSuccess(String resposta);
        void onError(String erro);
    }

    public static void enviarMensagem(List<JSONObject> historico, String novaMensagem, Callback callback) {
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();

                historico.add(new JSONObject()
                        .put("role", "user")
                        .put("content", novaMensagem));

                JSONObject body = new JSONObject()
                        .put("model", MODEL)
                        .put("max_tokens", 1000)
                        .put("system", SYSTEM_PROMPT)
                        .put("messages", new JSONArray(historico));

                Request request = new Request.Builder()
                        .url(API_URL)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("x-api-key", API_KEY)
                        .addHeader("anthropic-version", "2023-06-01")
                        .post(RequestBody.create(body.toString(),
                                MediaType.get("application/json")))
                        .build();

                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();

                android.util.Log.d("CLAUDE_API", "Resposta: " + responseBody);

                JSONObject json = new JSONObject(responseBody);
                String resposta = json.getJSONArray("content")
                        .getJSONObject(0)
                        .getString("text");

                historico.add(new JSONObject()
                        .put("role", "assistant")
                        .put("content", resposta));

                callback.onSuccess(resposta);

            } catch (Exception e) {
                android.util.Log.e("CLAUDE_API", "Erro: " + e.getMessage());
                callback.onError("Erro: " + e.getMessage());
            }
        }).start();
    }

    private static String getPratosJson() {
        return "[" +
                "{\"nome\":\"Omelete com Legumes\",\"periodo\":\"café da manhã\"," +
                "\"ingredientes\":[\"ovos\",\"tomate\",\"cebola\",\"pimentão\",\"azeite\",\"sal\"]," +
                "\"semGluten\":true,\"semLactose\":true,\"semAcucar\":true}," +

                "{\"nome\":\"Iogurte com Frutas\",\"periodo\":\"café da manhã\"," +
                "\"ingredientes\":[\"iogurte\",\"banana\",\"chia\"]," +
                "\"semGluten\":true,\"semLactose\":false,\"semAcucar\":false}," +

                "{\"nome\":\"Tapioca com Queijo\",\"periodo\":\"café da manhã\"," +
                "\"ingredientes\":[\"tapioca\",\"queijo\"]," +
                "\"semGluten\":true,\"semLactose\":false,\"semAcucar\":true}," +

                "{\"nome\":\"Vitamina de Banana\",\"periodo\":\"café da manhã\"," +
                "\"ingredientes\":[\"banana\",\"leite\",\"aveia\"]," +
                "\"semGluten\":true,\"semLactose\":false,\"semAcucar\":false}," +

                "{\"nome\":\"Frango com Arroz\",\"periodo\":\"almoço\"," +
                "\"ingredientes\":[\"frango\",\"arroz\",\"salada\",\"azeite\"]," +
                "\"semGluten\":true,\"semLactose\":true,\"semAcucar\":true}," +

                "{\"nome\":\"Carne com Batata Doce\",\"periodo\":\"almoço\"," +
                "\"ingredientes\":[\"carne\",\"batata doce\"]," +
                "\"semGluten\":true,\"semLactose\":true,\"semAcucar\":true}," +

                "{\"nome\":\"Peixe com Legumes\",\"periodo\":\"almoço\"," +
                "\"ingredientes\":[\"filé de peixe\",\"legumes\",\"azeite\"]," +
                "\"semGluten\":true,\"semLactose\":true,\"semAcucar\":true}," +

                "{\"nome\":\"Omelete Completo\",\"periodo\":\"almoço\"," +
                "\"ingredientes\":[\"ovos\",\"legumes\",\"azeite\"]," +
                "\"semGluten\":true,\"semLactose\":true,\"semAcucar\":true}," +

                "{\"nome\":\"Mix de Castanhas\",\"periodo\":\"lanche da tarde\"," +
                "\"ingredientes\":[\"castanhas\"]," +
                "\"semGluten\":true,\"semLactose\":true,\"semAcucar\":true}," +

                "{\"nome\":\"Maçã com Pasta\",\"periodo\":\"lanche da tarde\"," +
                "\"ingredientes\":[\"maçã\",\"pasta de amendoim\"]," +
                "\"semGluten\":true,\"semLactose\":true,\"semAcucar\":false}," +

                "{\"nome\":\"Smoothie\",\"periodo\":\"lanche da tarde\"," +
                "\"ingredientes\":[\"frutas\",\"líquido\"]," +
                "\"semGluten\":true,\"semLactose\":true,\"semAcucar\":false}," +

                "{\"nome\":\"Iogurte com Granola\",\"periodo\":\"lanche da tarde\"," +
                "\"ingredientes\":[\"iogurte\",\"granola\"]," +
                "\"semGluten\":true,\"semLactose\":false,\"semAcucar\":false}," +

                "{\"nome\":\"Sopa\",\"periodo\":\"janta\"," +
                "\"ingredientes\":[\"frango\",\"legumes\",\"água\",\"sal\"]," +
                "\"semGluten\":true,\"semLactose\":true,\"semAcucar\":true}," +

                "{\"nome\":\"Salada Proteica\",\"periodo\":\"janta\"," +
                "\"ingredientes\":[\"folhas verdes\",\"frango\",\"ovos\",\"azeite\"]," +
                "\"semGluten\":true,\"semLactose\":true,\"semAcucar\":true}," +

                "{\"nome\":\"Omelete Leve\",\"periodo\":\"janta\"," +
                "\"ingredientes\":[\"ovos\",\"sal\"]," +
                "\"semGluten\":true,\"semLactose\":true,\"semAcucar\":true}," +

                "{\"nome\":\"Tapioca com Recheio\",\"periodo\":\"janta\"," +
                "\"ingredientes\":[\"tapioca\",\"recheio a gosto\"]," +
                "\"semGluten\":true,\"semLactose\":false,\"semAcucar\":true}" +
                "]";
    }
}