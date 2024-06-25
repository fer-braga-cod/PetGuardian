package com.example.petguardian.list;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;

import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;


import com.example.petguardian.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private Context context;
    private List<DataClass> dataList;
    public SimpleDateFormat dateFormat = new SimpleDateFormat("EE dd MMM yyyy", Locale.US);
    String outputDateString = null;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public MyAdapter(Context context, List<DataClass> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.nome.setText(dataList.get(position).getNome());
        holder.desc.setText(dataList.get(position).getDescricao());
        holder.tempo.setText(dataList.get(position).getTempo());
        long timestamp = dataList.get(position).getData();
        holder.options.setOnClickListener(view -> showPopUpMenu(view, position));

        try {
            outputDateString = dateFormat.format(new Date(timestamp));

            String[] items1 = outputDateString.split(" ");
            String day = items1[0];
            String dd = items1[1];
            String month = items1[2];

            holder.day.setText(altDia(day));
            holder.date.setText(dd);
            holder.month.setText(altMes(month));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showPopUpMenu(View view, int position) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.item_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menuDelete) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.AppTheme_Dialog);
                alertDialogBuilder.setTitle(R.string.delete_confirmation).setMessage(R.string.sureToDelete).
                        setPositiveButton(R.string.yes, (dialog, which) -> {
                            deleteTask(position);
                        })
                        .setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel()).show();
            }
            return false;
        });
        popupMenu.show();
    }

    private void deleteTask(int position) {
        DataClass taskToDelete = dataList.get(position);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();  // Get current user ID
        DocumentReference userRef = db.collection("Usuarios").document(userId);
        CollectionReference taskRef = userRef.collection("Tarefas");

        taskRef.document(taskToDelete.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Task deleted successfully
                    int actualPosition = dataList.indexOf(taskToDelete); // Find actual position after update
                    if (actualPosition > -1) {  // Check if task exists in the current list
                        dataList.remove(actualPosition);  // Remove from list at actual position
                        notifyItemRemoved(actualPosition);  // Update UI
                    } else {
                        // Handle scenario where task might be removed from list by listener before this callback
                        Log.d("MyAdapter", "Task not found in local list for deletion");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle deletion failure
                        Toast.makeText(context, "Error deleting task!", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void searchDataList(ArrayList<DataClass> searchList){
        dataList = searchList;
        notifyDataSetChanged();
    }

    public void ordenarTarefas() {
        Collections.sort(dataList, new TarefaComparator());
        notifyDataSetChanged();
    }

    public String altDia(String day){
        switch (day) {
            case "Sun":
                return "Dom";
            case "Mon":
                return "Seg";
            case "Tue":
                return "Ter";
            case "Wed":
                return "Qua";
            case "Thu":
                return "Qui";
            case "Fri":
                return "Sex";
            case "Sat":
                return "Sáb";
            default:
                return "Inv";
        }
    }
    public String altMes(String month){
        switch (month) {
            case "Jan":
                return "Jan";
            case "Feb":
                return "Fev";
            case "Mar":
                return "Mar";
            case "Apr":
                return "Abr";
            case "May":
                return "Mai";
            case "Jun":
                return "Jun";
            case "Jul":
                return "Jul";
            case "Aug":
                return "Ago";
            case "Sep":
                return "Set";
            case "Oct":
                return "Out";
            case "Nov":
                return "Nov";
            case "Dec":
                return "Dez";
            default:
                return "Inv";
        }
    }
}

class MyViewHolder extends RecyclerView.ViewHolder{
    TextView nome, desc, tempo, day, month, date;
    CardView card;
    ImageView options;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        card = itemView.findViewById(R.id.task);
        desc = itemView.findViewById(R.id.description);
        nome = itemView.findViewById(R.id.title);
        tempo = itemView.findViewById(R.id.time);

        day = itemView.findViewById(R.id.day);
        date = itemView.findViewById(R.id.date);
        month =  itemView.findViewById(R.id.month);

        options = itemView.findViewById(R.id.options);
    }
}