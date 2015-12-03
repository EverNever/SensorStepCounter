package com.chao.stepcounter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Created by chao on 2015/11/25.
 */
public class SyncUtil {
    /**
     * 启动Sync定时任务
     * @param context 上下文
     */
    public static void startSyncTask(Context context){
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent dataIntent = new Intent(context, SyncService.class);
        PendingIntent sender = PendingIntent.getService(context,
                1, dataIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                StepCounterCore.SYNC_INTERVAL, sender);
    }

}
