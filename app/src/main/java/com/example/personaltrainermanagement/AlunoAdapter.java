package com.example.personaltrainermanagement;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.personaltrainermanagement.entities.Aluno;

import java.util.List;

public class AlunoAdapter extends BaseAdapter {

    private Context context;
    private List<Aluno> alunos;
    public static class AlunoHolder{
        public TextView textViewValorNome;
        public TextView textViewValorEmail;
        public TextView textViewValorIdade;
        public TextView textViewValorObjetivo;
    }

    public AlunoAdapter(Context context, List<Aluno> alunos) {
        this.context = context;
        this.alunos = alunos;
    }

    @Override
    public int getCount() {
        return alunos.size();
    }

    @Override
    public Object getItem(int position) {
        return alunos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AlunoHolder alunoHolder;

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.lista_alunos, parent, false);
            alunoHolder = new AlunoHolder();
            alunoHolder.textViewValorEmail = convertView.findViewById(R.id.textViewValorEmail);
            alunoHolder.textViewValorIdade = convertView.findViewById(R.id.textViewValorIdade);
            alunoHolder.textViewValorNome = convertView.findViewById(R.id.textViewValorNome);
            alunoHolder.textViewValorObjetivo = convertView.findViewById(R.id.textViewValorObjetivo);

            convertView.setTag(alunoHolder);
        }else{
            alunoHolder = (AlunoHolder) convertView.getTag();
        }
        alunoHolder.textViewValorNome.setText(alunos.get(position).getNome());
        alunoHolder.textViewValorIdade.setText(Integer.toString(alunos.get(position).getIdade()));
        alunoHolder.textViewValorEmail.setText(alunos.get(position).getEmail());
        alunoHolder.textViewValorObjetivo.setText(alunos.get(position).getObjetivo());
        return convertView;
    }
}
