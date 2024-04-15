package com.example.personaltrainermanagement;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.personaltrainermanagement.entities.Aluno;
import com.example.personaltrainermanagement.persistencia.AlunosDatabase;
import com.example.personaltrainermanagement.utils.UtilsGUI;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ActivityAluno extends AppCompatActivity {
    private EditText editTextNome, editTextTelefone1, editTextTelefone2, editTextIdade,
    editTextEmail, editTextRua, editTextNumero, editTextCep, editTextBairro, editTextCidade;
    private RadioGroup objetivo;
    private Aluno alunoOriginal;
    public static final String ID = "ID";

    public static final String MODO = "MODO";
    private static final int NOVO = 1;
    private static final int EDITAR = 2;
    private int modo;

    public static final String SUGERIR_OBJETIVO = "SUGERIR_OBJETIVO";
    public static final String ULTIMO_OBJETIVO = "ULTIMO_OBJETIVO";
    private boolean sugerirObjetivo = false;
    private int ultimoObjetivo = 0;




    public static void novoAluno(AppCompatActivity activity, ActivityResultLauncher<Intent> launcher){
        Intent intent = new Intent(activity, ActivityAluno.class);
        intent.putExtra(MODO, NOVO);
        launcher.launch(intent);
    }
    public static void editarAluno(AppCompatActivity activity, ActivityResultLauncher<Intent> launcher, Aluno aluno){
        Intent intent = new Intent(activity, ActivityAluno.class);

        intent.putExtra(MODO, EDITAR);
        intent.putExtra(ID, aluno.getId());
        launcher.launch(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_aluno);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        editTextNome = findViewById(R.id.editTextNome);
        editTextTelefone1 = findViewById(R.id.editTextTelefone1);
        editTextTelefone2 = findViewById(R.id.editTextTelefone2);
        editTextIdade = findViewById(R.id.editTextIdade);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextRua = findViewById(R.id.editTextRua);
        editTextNumero = findViewById(R.id.editTextNumero);
        editTextCep = findViewById(R.id.editTextCEP);
        editTextBairro = findViewById(R.id.editTextBairro);
        editTextCidade = findViewById(R.id.editTextTextCidade);
        objetivo = findViewById(R.id.radioGroupObjetivo);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        lerSugerirObjetivo();
        lerUltimoObjetivo();
        if(bundle != null){
            modo = bundle.getInt(MODO, NOVO);

            if(modo == NOVO){
                setTitle(getString(R.string.cadastrar_novo_aluno));
                if(sugerirObjetivo && ultimoObjetivo!=0){
                    objetivo.check(ultimoObjetivo);
                }
            }else if(modo == EDITAR){
                setTitle(getString(R.string.editar_aluno));
                long id = bundle.getLong(ID);
                AlunosDatabase database = AlunosDatabase.getDatabase(this);
                alunoOriginal = database.getAlunoDao().findById(id);
                editTextNome.setText(alunoOriginal.getNome());
                editTextNome.setSelection(editTextNome.getText().length());
                editTextIdade.setText(alunoOriginal.getIdade().toString());
                editTextIdade.setSelection(editTextIdade.getText().length());
                editTextEmail.setText(alunoOriginal.getEmail());
                editTextEmail.setSelection(editTextEmail.getText().length());
                if(alunoOriginal.getObjetivo().equals(R.string.hipertrofia))objetivo.check(R.id.radioButtonHipetrofia);
                if(alunoOriginal.getObjetivo().equals(R.string.saude))objetivo.check(R.id.radioButtonSaude);
                if(alunoOriginal.getObjetivo().equals(R.string.preparo))objetivo.check(R.id.radioButtonPreparo);
            }
        }

    }
    public void limparCampos (){
        editTextCidade.setText(null);
        editTextNumero.setText(null);
        editTextCep.setText(null);
        editTextRua.setText(null);
        editTextNome.setText(null);
        editTextTelefone1.setText(null);
        editTextTelefone2.setText(null);
        editTextIdade.setText(null);
        editTextEmail.setText(null);
        editTextBairro.setText(null);
        objetivo.setActivated(false);
        Toast.makeText(this, R.string.campos_limpos, Toast.LENGTH_SHORT).show();
        editTextNome.requestFocus();
    }
    private void cancelar(){
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void salvarDados(){
        List<String> camposVaziosOuNulos = Stream.of(
                editTextNome.getText().toString(),
                editTextTelefone1.getText().toString(),
                editTextEmail.getText().toString(),
                editTextIdade.getText().toString(),
                editTextRua.getText().toString(),
                editTextNumero.getText().toString(),
                editTextCep.getText().toString(),
                editTextBairro.getText().toString(),
                editTextCidade.getText().toString()
        ).filter(s->s.trim().isEmpty() || s==null).collect(Collectors.toList());
        if(!camposVaziosOuNulos.isEmpty()){
            UtilsGUI.avisar(this, R.string.only_phone_number_to_can_be_empty);
            return;
        }

        if((objetivo.getCheckedRadioButtonId() != R.id.radioButtonHipetrofia)
                && (objetivo.getCheckedRadioButtonId() != R.id.radioButtonSaude)
                && (objetivo.getCheckedRadioButtonId() != R.id.radioButtonPreparo)){
            objetivo.requestFocus();
            UtilsGUI.avisar(this, R.string.selecione_um_objetivo);
            return;
        }
        RadioButton radioButtonEscolhido = (RadioButton) findViewById(objetivo.getCheckedRadioButtonId());
        if(!(editTextNumero.getText().toString().trim().isEmpty()) && !(editTextNumero.getText().toString() == null)){
            try {
                int numero = Integer.parseInt(editTextNumero.getText().toString());
                if(numero<0) {
                    UtilsGUI.avisar(this, R.string.digite_um_numero_valido);
                    editTextNumero.requestFocus();
                    return;
                }
            }catch (Exception e){
                UtilsGUI.avisar(this, R.string.nao_digitou_numero);
                editTextNumero.requestFocus();
                return;
            }
        }

        if(!editTextTelefone1.getText().toString().matches("[0-9]+")){
            UtilsGUI.avisar(this, R.string.telefone_deve_conter_so_numeros);
            editTextTelefone1.requestFocus();
            return;
        }
        if(editTextTelefone1!=null && !editTextTelefone1.getText().toString().trim().isEmpty()){
            if(!editTextTelefone2.getText().toString().trim().isEmpty() &&
                    !editTextTelefone2.getText().toString().matches("[0-9]+")){
                UtilsGUI.avisar(this,R.string.deixe_o_telefone_2_vazio_ou_preencha_o_corretamente_com_n_meros );
                editTextTelefone2.requestFocus();
                return;
            }
        }

        if(!editTextCep.getText().toString().matches("\\d{5}-\\d{3}")){
            UtilsGUI.avisar(this,R.string.cep_invalido );
            editTextCep.requestFocus();
            return;
        }
        String nome = editTextNome.getText().toString();
        String email = editTextEmail.getText().toString();
        Integer idade = Integer.parseInt(editTextIdade.getText().toString());
        String objetivoEscolhido = radioButtonEscolhido.getText().toString();
        if((modo == EDITAR) &&
                (editTextEmail.getText().toString().equals(alunoOriginal.getEmail()))
        && (editTextIdade.getText().toString().equals(alunoOriginal.getIdade()))
        && (editTextNome.getText().toString().equals(alunoOriginal.getNome()))
        && (radioButtonEscolhido.getText().toString().equals(alunoOriginal.getObjetivo()))){
            UtilsGUI.avisar(this, R.string.erro_ao_editar_mesmos_campos);
            return;
        }

        salvarUltimoObjetivo(radioButtonEscolhido.getId());

        Intent intent = new Intent();

        AlunosDatabase database = AlunosDatabase.getDatabase(this);
        if(modo == NOVO){
            Aluno alunoNovo = new Aluno(idade, nome, email, objetivoEscolhido);
            long novoId = database.getAlunoDao().insert(alunoNovo);
            if(novoId <= 0){
                UtilsGUI.avisar(this, R.string.problema_de_banco);
                return;
            }
            alunoNovo.setId(novoId);
            intent.putExtra(ID, alunoNovo.getId());
        }else {
            Aluno alunoAlterado = new Aluno(idade, nome, email, objetivoEscolhido);
            alunoAlterado.setId(alunoOriginal.getId());
            int linhasAlteradas = database.getAlunoDao().update(alunoAlterado);
            if(linhasAlteradas == 0){
                UtilsGUI.avisar(this, R.string.error_ao_alterar);
                return;
            }
            intent.putExtra(ID, alunoAlterado.getId());
        }

        setResult(Activity.RESULT_OK, intent);
        Toast.makeText(this, R.string.dados_cadastrados_com_sucesso, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.aluno_acoes, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.menuItemSugerirObjetivo);
        menuItem.setChecked(sugerirObjetivo);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int idMenuItem = item.getItemId();

        if(idMenuItem == R.id.menuItemSalvar){
            salvarDados();
            return true;
        } else if (idMenuItem == R.id.menuItemLimpar) {
            limparCampos();
            return true;
        }else if(idMenuItem == android.R.id.home){
            cancelar();
            return true;
        }else if(idMenuItem == R.id.menuItemSugerirObjetivo){
            boolean valor = !item.isChecked();
            salvarSugerirObjetivo(valor);
            item.setChecked(valor);
            if(sugerirObjetivo){
                objetivo.check(ultimoObjetivo);
            }
            return true;
        } else{
            return super.onOptionsItemSelected(item);
        }
    }

    private void lerSugerirObjetivo(){
        SharedPreferences shared = getSharedPreferences(ActivityPrincipal.ARQUIVO, Context.MODE_PRIVATE);
        sugerirObjetivo = shared.getBoolean(SUGERIR_OBJETIVO, sugerirObjetivo);
    }

    private void salvarSugerirObjetivo(boolean novoValor){
        SharedPreferences shared = getSharedPreferences(ActivityPrincipal.ARQUIVO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putBoolean(SUGERIR_OBJETIVO, novoValor);
        editor.commit();
        sugerirObjetivo = novoValor;
    }

    private void lerUltimoObjetivo(){
        SharedPreferences shared = getSharedPreferences(ActivityPrincipal.ARQUIVO, Context.MODE_PRIVATE);
        ultimoObjetivo = shared.getInt(ULTIMO_OBJETIVO, ultimoObjetivo);
    }

    private void salvarUltimoObjetivo(int novoValor){
        SharedPreferences shared = getSharedPreferences(ActivityPrincipal.ARQUIVO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt(ULTIMO_OBJETIVO, novoValor);
        editor.commit();
        ultimoObjetivo = novoValor;
    }
}