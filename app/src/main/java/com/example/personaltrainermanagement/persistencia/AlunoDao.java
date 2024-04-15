package com.example.personaltrainermanagement.persistencia;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.personaltrainermanagement.entities.Aluno;

import java.util.List;

@Dao
public interface AlunoDao {
    @Insert
    long insert (Aluno aluno);
    @Delete
    int delete(Aluno aluno);
    @Update
    int update(Aluno aluno);
    @Query("Select * from Aluno where id = :id")
    Aluno findById(long id);
    @Query("Select * from Aluno order by nome asc")
    List<Aluno> findAllAscending();
    @Query("Select * from Aluno order by nome desc")
    List<Aluno> findAllDescending();
}
