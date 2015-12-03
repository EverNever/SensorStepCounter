package com.chao.stepcounter;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by chao on 2015/11/25.
 */
public class SyncService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public SyncService() {
        super("SyncService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        StepCounterCore counterCore = StepCounterCore.getInstance(this);
        // 注册sensor的监听器
        counterCore.registerStepCounterListener();
        // 同步服务器数据
        counterCore.syncServerData();
    }
}
