package com.example.petguardian.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;

import java.util.HashMap;
import java.util.Map;

public class AlarmManagerUtil {

    private Context context;
    private Map<String, PendingIntent> alarmMap = new HashMap<>();

    public AlarmManagerUtil(Context context) {
        this.context = context;
    }


    public void cancelAlarmForTask(String taskId) {
        if (alarmMap.containsKey(taskId)) {
            PendingIntent pendingIntent = alarmMap.get(taskId);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            alarmMap.remove(taskId);
        }
    }
}

