package com.example.petguardian.list;

import com.google.type.DateTime;

import java.util.Comparator;

public class TarefaComparator implements Comparator<DataClass> {
    @Override
    public int compare(DataClass tarefa1, DataClass tarefa2) {
        Long dataT1 = tarefa1.getData();
        Long dataT2 = tarefa2.getData();

        String tempoT1 = tarefa1.getTempo();
        String tempoT2 = tarefa2.getTempo();

        if(dataT1.compareTo(dataT2) == 0){
            return tempoT1.compareTo(tempoT2);
        }else{
            return dataT1.compareTo(dataT2);
        }
    }
}
