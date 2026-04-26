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
                "{\"nome\":\"Omelete de legumes\",\"periodo\":\"café da manhã\"," +
                "\"ingredientes\":[\"ovos\",\"abobrinha\",\"cenoura\",\"sal\",\"azeite\"]," +
                "\"semGluten\":true,\"semLactose\":true,\"semAcucar\":true}," +

                "{\"nome\":\"Panqueca de banana com aveia sem glúten\",\"periodo\":\"café da manhã\"," +
                "\"ingredientes\":[\"banana\",\"aveia sem glúten\",\"ovos\",\"canela\"]," +
                "\"semGluten\":true,\"semLactose\":true,\"semAcucar\":true}," +

                "{\"nome\":\"Iogurte de coco com frutas\",\"periodo\":\"café da manhã ou café da tarde\"," +
                "\"ingredientes\":[\"iogurte de coco\",\"morango\",\"mirtilo\",\"granola sem glúten\"]," +
                "\"semGluten\":true,\"semLactose\":true,\"semAcucar\":false}," +

                "{\"nome\":\"Tapioca com frango desfiado\",\"periodo\":\"café da manhã ou café da tarde\"," +
                "\"ingredientes\":[\"tapioca\",\"frango\",\"tomate\",\"sal\",\"azeite\"]," +
                "\"semGluten\":true,\"semLactose\":true,\"semAcucar\":true}," +

                "{\"nome\":\"Frango grelhado com quinoa e brócolis\",\"periodo\":\"almoço ou janta\"," +
                "\"ingredientes\":[\"frango\",\"quinoa\",\"brócolis\",\"alho\",\"azeite\",\"limão\"]," +
                "\"semGluten\":true,\"semLactose\":true,\"semAcucar\":true}," +

                "{\"nome\":\"Peixe assado com batata-doce\",\"periodo\":\"almoço ou janta\"," +
                "\"ingredientes\":[\"tilápia\",\"batata-doce\",\"ervas\",\"azeite\",\"sal\"]," +
                "\"semGluten\":true,\"semLactose\":true,\"semAcucar\":true}," +

                "{\"nome\":\"Arroz integral com lentilha e cenoura\",\"periodo\":\"almoço\"," +
                "\"ingredientes\":[\"arroz integral\",\"lentilha\",\"cenoura\",\"cebola\",\"alho\",\"cúrcuma\"]," +
                "\"semGluten\":true,\"semLactose\":true,\"semAcucar\":true}," +

                "{\"nome\":\"Salada de grão-de-bico com legumes\",\"periodo\":\"almoço ou janta\"," +
                "\"ingredientes\":[\"grão-de-bico\",\"pepino\",\"tomate\",\"cebola roxa\",\"salsinha\",\"limão\",\"azeite\"]," +
                "\"semGluten\":true,\"semLactose\":true,\"semAcucar\":true}," +

                "{\"nome\":\"Macarrão de arroz com molho de tomate e carne\",\"periodo\":\"almoço ou janta\"," +
                "\"ingredientes\":[\"macarrão de arroz\",\"carne moída\",\"tomate\",\"cebola\",\"alho\",\"manjericão\"]," +
                "\"semGluten\":true,\"semLactose\":true,\"semAcucar\":true}," +

                "{\"nome\":\"Sopa de abóbora com gengibre\",\"periodo\":\"janta\"," +
                "\"ingredientes\":[\"abóbora\",\"gengibre\",\"cebola\",\"alho\",\"caldo de legumes\"]," +
                "\"semGluten\":true,\"semLactose\":true,\"semAcucar\":true}," +

                "{\"nome\":\"Wrap de alface com atum e abacate\",\"periodo\":\"almoço ou janta\"," +
                "\"ingredientes\":[\"atum\",\"abacate\",\"alface\",\"tomate\",\"limão\"]," +
                "\"semGluten\":true,\"semLactose\":true,\"semAcucar\":true}," +

                "{\"nome\":\"Vitamina de morango com leite de amêndoas\",\"periodo\":\"café da manhã ou café da tarde\"," +
                "\"ingredientes\":[\"morango\",\"leite de amêndoas\",\"banana\",\"chia\"]," +
                "\"semGluten\":true,\"semLactose\":true,\"semAcucar\":false}," +

                "{\"nome\":\"Bolinho de couve-flor com queijo sem lactose\",\"periodo\":\"café da tarde\"," +
                "\"ingredientes\":[\"couve-flor\",\"queijo sem lactose\",\"ovos\",\"sal\"]," +
                "\"semGluten\":true,\"semLactose\":true,\"semAcucar\":true}," +

                "{\"nome\":\"Frango ao curry com arroz de couve-flor\",\"periodo\":\"almoço ou janta\"," +
                "\"ingredientes\":[\"frango\",\"couve-flor\",\"curry\",\"leite de coco\",\"cebola\",\"alho\"]," +
                "\"semGluten\":true,\"semLactose\":true,\"semAcucar\":true}" +
                "]";
    }
}