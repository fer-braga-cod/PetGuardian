package com.example.petguardian.list;

import com.google.firebase.firestore.DocumentId;


public class DataClass {
    @DocumentId
    private String id;
    private String nome;
    private String descricao;
    private Long data;
    private String tempo;


    public DataClass(Long data, String descricao, String nome, String tempo) {
        this.nome = nome;
        this.descricao = descricao;
        this.data = data;
        this.tempo = tempo;
    }

    public DataClass(){}

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public Long getData() {
        return data;
    }

    public String getTempo(){return tempo;}

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setData(Long data) {
        this.data = data;
    }

    public void setTempo(String tempo) {
        this.tempo = tempo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
