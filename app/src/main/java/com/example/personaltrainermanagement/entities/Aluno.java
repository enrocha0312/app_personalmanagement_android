package com.example.personaltrainermanagement.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Comparator;
@Entity
public class Aluno {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private Integer idade;
    private String nome;

    private String email;
    private String objetivo;

    public static Comparator comparatorCrescente = new Comparator<Aluno>() {
        @Override
        public int compare(Aluno o1, Aluno o2) {
            return o1.getNome().compareToIgnoreCase(o2.getNome());
        }
    };

    public static Comparator comparatorDecrescente = new Comparator<Aluno>() {
        @Override
        public int compare(Aluno o1, Aluno o2) {
            return (-1) * o1.getNome().compareToIgnoreCase(o2.getNome());
        }
    };

    public Aluno() {
    }

    public Aluno(Integer idade, String nome, String email, String objetivo) {
        this.idade = idade;
        this.nome = nome;
        this.email = email;
        this.objetivo = objetivo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getIdade() {
        return idade;
    }

    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(String objetivo) {
        this.objetivo = objetivo;
    }
}
