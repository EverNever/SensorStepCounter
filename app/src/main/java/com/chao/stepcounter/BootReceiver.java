package com.chao.stepcounter;

/**
 * Created by chao on 2015/11/25.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 开机自启同时启动定时任务
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SyncUtil.startSyncTask(context);
    }
}
