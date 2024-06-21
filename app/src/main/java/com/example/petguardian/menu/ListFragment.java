package com.example.petguardian.menu;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.petguardian.R;
import com.example.petguardian.list.DataClass;
import com.example.petguardian.list.MyAdapter;
import com.example.petguardian.list.RegisterActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {
    FloatingActionButton fab;
    MyAdapter adapter;
    SearchView searchView;
    FirebaseFirestore db;
    List<DataClass> dataList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        fab = view.findViewById(R.id.fab);
        searchView = view.findViewById(R.id.search);
        searchView.clearFocus();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userRef = db.collection("Usuarios").document(userId);
        CollectionReference taskRef = userRef.collection("Tarefas");
        adapter = new MyAdapter(requireContext(), dataList);
        recyclerView.setAdapter(adapter);
        taskRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("ListFragment", "Erro ao recuperar tarefas", e); // Registra a exceção
                    Snackbar snackbar = Snackbar.make(view, "Erro ao carregar tarefas", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                    return;
                }
                if (snapshot != null) {
                    dataList.clear(); // Limpa dados existentes para evitar duplicatas
                    for (DocumentSnapshot document : snapshot.getDocuments()) {
                        DataClass task = document.toObject(DataClass.class);
                        dataList.add(task);
                    }
                }
                adapter.notifyDataSetChanged();
            }

        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}