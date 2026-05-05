package com.example.projeto;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import com.example.projeto.models.ClaudeApiService;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText editText;
    private ImageButton btnEnviar;
    private LinearLayout layoutBoasVindas;
    private List<Mensagem> mensagens = new ArrayList<>();
    private MensagemAdapter adapter;
    private List<JSONObject> historico = new ArrayList<>();
    private boolean primeiraInteracao = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerViewMensagens);
        editText = findViewById(R.id.editTextMensagem);
        btnEnviar = findViewById(R.id.btnEnviar);
        layoutBoasVindas = findViewById(R.id.layoutBoasVindas);

        adapter = new MensagemAdapter(mensagens);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnEnviar.setOnClickListener(v -> {
            String texto = editText.getText().toString().trim();
            if (texto.isEmpty()) return;

            // Esconde a tela de boas-vindas e mostra o chat
            if (primeiraInteracao) {
                layoutBoasVindas.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                primeiraInteracao = false;
            }

            adicionarMensagem(texto, true);
            editText.setText("");
            btnEnviar.setEnabled(false);

            ClaudeApiService.enviarMensagem(historico, texto, new ClaudeApiService.Callback() {
                @Override
                public void onSuccess(String resposta) {
                    runOnUiThread(() -> {
                        adicionarMensagem(resposta, false);
                        btnEnviar.setEnabled(true);
                    });
                }

                @Override
                public void onError(String erro) {
                    runOnUiThread(() -> {
                        adicionarMensagem("Ocorreu um erro. Tente novamente.", false);
                        btnEnviar.setEnabled(true);
                    });
                }
            });
        });
    }

    private void adicionarMensagem(String texto, boolean isUsuario) {
        mensagens.add(new Mensagem(texto, isUsuario));
        adapter.notifyItemInserted(mensagens.size() - 1);
        recyclerView.scrollToPosition(mensagens.size() - 1);
    }

    // Modelo de mensagem
    static class Mensagem {
        String texto;
        boolean isUsuario;
        Mensagem(String texto, boolean isUsuario) {
            this.texto = texto;
            this.isUsuario = isUsuario;
        }
    }

    // Adapter do RecyclerView
    static class MensagemAdapter extends RecyclerView.Adapter<MensagemAdapter.ViewHolder> {
        private List<Mensagem> lista;
        MensagemAdapter(List<Mensagem> lista) { this.lista = lista; }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(viewType == 1 ? R.layout.item_mensagem_usuario : R.layout.item_mensagem_bot, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public int getItemViewType(int position) {
            return lista.get(position).isUsuario ? 1 : 0;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.textView.setText(lista.get(position).texto);
        }

        @Override
        public int getItemCount() { return lista.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;
            ViewHolder(View view) {
                super(view);
                textView = view.findViewById(R.id.textMensagem);
            }
        }
    }
}