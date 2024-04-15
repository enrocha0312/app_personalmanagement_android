package com.example.personaltrainermanagement.persistencia;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.personaltrainermanagement.entities.Aluno;

@Database(entities = {Aluno.class}, version = 1, exportSchema = false)
public abstract class AlunosDatabase extends RoomDatabase {
    public abstract AlunoDao getAlunoDao();
    private static AlunosDatabase instance;
    public static AlunosDatabase getDatabase(final Context context){
        if(instance == null){
            synchronized (AlunosDatabase.class){
                if(instance == null){
                    instance = Room.databaseBuilder(context,
                            AlunosDatabase.class,
                            "alunos_personal_trainer.db")
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return instance;
    }
}
