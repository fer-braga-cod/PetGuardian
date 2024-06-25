package com.example.petguardian.list;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.petguardian.R;
import com.example.petguardian.alarm.AlarmManagerUtil;
import com.example.petguardian.alarm.AlarmReceiver;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText edt_Nome, edt_Desc;
    private Button bt_Data, bt_Hora, bt_Salvar;
    private Date selectedDate;
    private int hora, minuto;
    private String tempo;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private AlarmManagerUtil alarmManagerUtil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        IniciarComponentes();
        createNotificationChannel();
        alarmManagerUtil = new AlarmManagerUtil(this);

        bt_Data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select Date")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        selectedDate = new Date(selection);
                        String date = new SimpleDateFormat("MM-dd-yyy", Locale.getDefault()).format(new Date(selection));
                        bt_Data.setText(MessageFormat.format("Selected Date: {0}", date));
                    }
                });
                materialDatePicker.show(getSupportFragmentManager(), "tag");
            }
        });

        bt_Hora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_12H)
                        .setHour(12)
                        .setMinute(0)
                        .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                        .setTitleText("Pick Time")
                        .build();
                timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hora = timePicker.getHour();
                        minuto = timePicker.getMinute();
                        bt_Hora.setText(MessageFormat.format("Selected Time: {0}:{1}",
                                String.format(Locale.getDefault(), "%02d", timePicker.getHour()),
                                String.format(Locale.getDefault(), "%02d", timePicker.getMinute())));
                        tempo = MessageFormat.format("{0}:{1}",
                                String.format(Locale.getDefault(), "%02d", timePicker.getHour()),
                                String.format(Locale.getDefault(), "%02d", timePicker.getMinute()));
                    }
                });
                timePicker.show(getSupportFragmentManager(), "tag");
            }
        });

        bt_Salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate(v))
                    createTask(v);
            }
        });
    }

    public boolean validate(View v) {
        if(edt_Nome.getText().toString().equalsIgnoreCase("")) {
            Snackbar snackbar = Snackbar.make(v, "Please enter a valid title", Snackbar.LENGTH_SHORT);
            snackbar.setBackgroundTint(Color.WHITE);
            snackbar.setTextColor(Color.BLACK);
            snackbar.show();
            return false;
        }
        else if(edt_Desc.getText().toString().equalsIgnoreCase("")) {
            Snackbar snackbar = Snackbar.make(v, "Please enter a valid description", Snackbar.LENGTH_SHORT);
            snackbar.setBackgroundTint(Color.WHITE);
            snackbar.setTextColor(Color.BLACK);
            snackbar.show();
            return false;
        }
        else if(selectedDate == null) {
            Snackbar snackbar = Snackbar.make(v, "Please enter date", Snackbar.LENGTH_SHORT);
            snackbar.setBackgroundTint(Color.WHITE);
            snackbar.setTextColor(Color.BLACK);
            snackbar.show();
            return false;
        }
        else if(hora == -1 || minuto == -1) {
            Snackbar snackbar = Snackbar.make(v, "Please enter time", Snackbar.LENGTH_SHORT);
            snackbar.setBackgroundTint(Color.WHITE);
            snackbar.setTextColor(Color.BLACK);
            snackbar.show();
            return false;
        }
        else {
            return true;
        }
    }

    private void IniciarComponentes(){
        edt_Nome = findViewById(R.id.uploadTopic);
        edt_Desc = findViewById(R.id.uploadDesc);
        bt_Data = findViewById(R.id.uploadDate);
        bt_Hora = findViewById(R.id.uploadTime);
        bt_Salvar = findViewById(R.id.saveButton);
    }

    private void createTask(View v) {
        String nome = edt_Nome.getText().toString();
        String desc = edt_Desc.getText().toString();
        // Assuming you have Firebase configured and user signed in
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();  // Get current user ID
        DocumentReference userRef = db.collection("Usuarios").document(userId);

        // Criar referência à subcoleção "tarefas" dentro do documento do usuário
        CollectionReference taskRef = userRef.collection("Tarefas");

        // Create a new task object with user ID
        //DataClass task = new DataClass(nome, desc, selectedDate.getTime(), hora, minuto);
        long timestamp = selectedDate.getTime();
        Map<String, Object> taskMap = new HashMap<>();
        taskMap.put("nome", nome);
        taskMap.put("descricao", desc);
        taskMap.put("data", timestamp); // Timestamp em milissegundos
        taskMap.put("tempo", tempo);        ;

        // Add task to Firestore under a collection named "tasks"
        taskRef.add(taskMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String taskId = documentReference.getId();
                        alarmManagerUtil.cancelAlarmForTask(taskId);
                        long timestamp2;
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(selectedDate); // Definir a data selecionada
                        calendar.set(Calendar.HOUR_OF_DAY, hora); // Definir a hora selecionada
                        calendar.set(Calendar.MINUTE, minuto); // Definir o minuto selecionado
                        calendar.set(Calendar.SECOND, 0); // Definir o segundo como 0
                        timestamp2 = calendar.getTimeInMillis();
                        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        Intent intent = new Intent(RegisterActivity.this, AlarmReceiver.class);
                        String taskID = documentReference.getId();
                        intent.putExtra("taskId", taskID);
                        pendingIntent = PendingIntent.getBroadcast(RegisterActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
                        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, timestamp2, AlarmManager.INTERVAL_DAY, pendingIntent);
                        Toast.makeText(RegisterActivity.this, "Alarm Set", Toast.LENGTH_SHORT).show();

                        Snackbar snackbar = Snackbar.make(v, "Task created successfully!", Snackbar.LENGTH_SHORT);
                        snackbar.setBackgroundTint(Color.GREEN);
                        snackbar.setTextColor(Color.WHITE);
                        snackbar.show();
                        // Clear input fields after successful save
                        edt_Nome.setText("");
                        edt_Desc.setText("");
                        bt_Data.setText("Select Date");
                        bt_Hora.setText("Select Time");
                        selectedDate = null;
                        hora = -1;
                        minuto = -1;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar snackbar = Snackbar.make(v, "Error creating task: " + e.getMessage(), Snackbar.LENGTH_SHORT);
                        snackbar.setBackgroundTint(Color.RED);
                        snackbar.setTextColor(Color.WHITE);
                        snackbar.show();
                    }
                });
    }

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "akchannel";
            String desc = "Channel for Alarm Manager";
            int imp = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("androidknowledge", name, imp);
            channel.setDescription(desc);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}