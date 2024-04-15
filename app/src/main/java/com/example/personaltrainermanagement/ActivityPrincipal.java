package com.example.personaltrainermanagement;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.personaltrainermanagement.entities.Aluno;
import com.example.personaltrainermanagement.persistencia.AlunosDatabase;
import com.example.personaltrainermanagement.utils.UtilsGUI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActivityPrincipal extends AppCompatActivity {

    private ListView listViewAlunos;
    private List<Aluno> alunos;
    private AlunoAdapter alunoAdapter;
    private ActionMode actionMode;
    private View viewSelecionada;
    private int posicaoSelecionada = -1;
    public static final String ARQUIVO = "com.example.personaltrainermanagement.PREFERENCIAS";
    public static final String ORDENACAO_ASCENDENTE = "ORDENACAO_ASCENDENTE";
    private boolean ordenacaoAscendente = true;

    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.aluno_selecionado, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int idMenuItem = item.getItemId();
            if(idMenuItem == R.id.menuItemEditar){
                editarAluno();
                mode.finish();
                return true;
            }else if(idMenuItem == R.id.menuItemExcluir){
                excluirAluno(mode);
                return true;
            }else{
                return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if (viewSelecionada != null) {
                viewSelecionada.setBackgroundColor(Color.TRANSPARENT);
            }

            actionMode =  null;
            viewSelecionada = null;

            listViewAlunos.setEnabled(true);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_principal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setTitle(getString(R.string.controle_de_alunos));
        listViewAlunos = findViewById(R.id.listViewAlunos);
        listViewAlunos.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listViewAlunos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(actionMode != null){
                    return false;
                }
                posicaoSelecionada = position;
                view.setBackgroundColor(Color.LTGRAY);
                viewSelecionada = view;
                listViewAlunos.setEnabled(false);
                actionMode = startSupportActionMode(actionModeCallback);
                return  false;
            }
        });
        listViewAlunos.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                posicaoSelecionada = position;
                editarAluno();
            }
        });
        lerPreferenciaOrdenacaoAscendente();
        popularLista();
    }

    ActivityResultLauncher<Intent> launcherNovoAluno = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if(o.getResultCode() == Activity.RESULT_OK){
                        Intent intent = o.getData();
                        Bundle bundle = intent.getExtras();
                        if(bundle != null){
                            long id = bundle.getLong(ActivityAluno.ID);
                            AlunosDatabase database = AlunosDatabase.getDatabase(ActivityPrincipal.this);
                            Aluno alunoCadastrado = database.getAlunoDao().findById(id);
                            alunos.add(alunoCadastrado);
                            ordenarLista();
                        }
                    }
                }
            });

    ActivityResultLauncher<Intent> launcherEditarAluno = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if(o.getResultCode() == Activity.RESULT_OK){
                        Intent intent = o.getData();
                        Bundle bundle = intent.getExtras();
                        if(bundle != null){
                            long id = bundle.getLong(ActivityAluno.ID);
                            AlunosDatabase database = AlunosDatabase.getDatabase(ActivityPrincipal.this);
                            Aluno alunoAlterado = database.getAlunoDao().findById(id);
                            alunos.set(posicaoSelecionada, alunoAlterado);
                            posicaoSelecionada = -1;
                            ordenarLista();
                        }
                    }
                }
            });



    private void popularLista(){
        AlunosDatabase alunosDatabase = AlunosDatabase.getDatabase(this);
        if(ordenacaoAscendente){
            alunos = alunosDatabase.getAlunoDao().findAllAscending();
        }else{
            alunos = alunosDatabase.getAlunoDao().findAllDescending();
        }
        alunoAdapter = new AlunoAdapter(this, alunos);
        listViewAlunos.setAdapter(alunoAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.principal_acoes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int idMenuItem = item.getItemId();
        if(idMenuItem == R.id.menuItemSobre){
            ActivitySobre.nova(this);
            return true;
        }else if(idMenuItem == R.id.menuItemAdicionar){
            ActivityAluno.novoAluno(this, launcherNovoAluno);
            return true;
        }else if(idMenuItem == R.id.menuItemOrdenacao){
            salvarPreferenciaOrdenacaoAscendente(!ordenacaoAscendente);
            atualizarIconeOrdenacao(item);
            ordenarLista();
            return true;
        }
        else{
            return super.onOptionsItemSelected(item);
        }
    }

    private void excluirAluno(final ActionMode mode){
        final Aluno aluno = alunos.get(posicaoSelecionada);
        StringBuilder message = new StringBuilder();
        message.append(getString(R.string.do_you_really_wish_to_delete_it));
        message.append(getString(R.string.trained_person)).append(aluno.getNome());
        message.append("\n" + getString(R.string.email)).append(" "+ aluno.getEmail());
        DialogInterface.OnClickListener listener= new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == DialogInterface.BUTTON_POSITIVE){
                    AlunosDatabase database = AlunosDatabase.getDatabase(ActivityPrincipal.this);
                    int qdteLinhasAlteradas = database.getAlunoDao().delete(aluno);
                    if(qdteLinhasAlteradas > 0){
                    alunos.remove(posicaoSelecionada);
                    alunoAdapter.notifyDataSetChanged();
                    actionMode.finish();
                    }else{
                        UtilsGUI.avisar(ActivityPrincipal.this, R.string.error_ao_tentar_apagar);
                    }
                }
                if(which == DialogInterface.BUTTON_NEGATIVE){
                    Toast.makeText(ActivityPrincipal.this, getString(R.string.nao_apagou), Toast.LENGTH_SHORT).show();
                }
            }
        };
        UtilsGUI.perguntarAoUsuarioSobreAcao(this, message.toString(), listener);
    }

    private void editarAluno(){
        Aluno aluno = alunos.get(posicaoSelecionada);
        ActivityAluno.editarAluno(this, launcherEditarAluno, aluno);
    }
    private void ordenarLista(){
        if(ordenacaoAscendente){
            Collections.sort(alunos, Aluno.comparatorCrescente);
        }else{
            Collections.sort(alunos, Aluno.comparatorDecrescente);
        }
        alunoAdapter.notifyDataSetChanged();
    }
    private void lerPreferenciaOrdenacaoAscendente(){
        SharedPreferences shared = getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE);
        ordenacaoAscendente = shared.getBoolean(ORDENACAO_ASCENDENTE, ordenacaoAscendente);
    }

    private void salvarPreferenciaOrdenacaoAscendente(boolean novoValor){
        SharedPreferences shared = getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putBoolean(ORDENACAO_ASCENDENTE, novoValor);
        editor.commit();
        ordenacaoAscendente = novoValor;
    }

    private void atualizarIconeOrdenacao(MenuItem menuItemOrdenacao){
        if(ordenacaoAscendente){
            menuItemOrdenacao.setIcon(R.drawable.ic_action_ordenacao_ascendente);
        }else{
            menuItemOrdenacao.setIcon(R.drawable.ic_action_ordenacao_descendente);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItemOrdenacao = menu.findItem(R.id.menuItemOrdenacao);
        atualizarIconeOrdenacao(menuItemOrdenacao);
        return true;
    }
}