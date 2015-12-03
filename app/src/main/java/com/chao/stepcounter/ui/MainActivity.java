package com.chao.stepcounter.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.chao.stepcounter.StepCounterCore;
import com.chao.stepcounter.SyncUtil;
import com.chao.stepcounter.R;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    public final String TAG = getClass().getSimpleName();

    private TextView tv_step;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        // Sensor为空，退出
        if (!StepCounterCore.checkStepSensor(this)) {
            finish();
            return;
        }
        //初始化值
        initValue();

        // 启动同步任务 放到initValue后面
        SyncUtil.startSyncTask(this);
    }

    private void initView() {
        tv_step = (TextView) findViewById(R.id.tv_step);
    }

    private void initValue() {
        StepCounterCore.getInstance(this).registerUICallback(new UICallback() {
            @Override
            public void onStepUpdate(String incrementStep, String totalStep, String eventValue) {
                tv_step.setText(Calendar.getInstance().getTime().getDate() + "日\n"
                                + "incrementStep:" + incrementStep + "\n"
                                + "totalStep:" + totalStep + "\n"
                                + "eventValue:" + eventValue
                );
            }
        });
    }

    public interface UICallback {
        void onStepUpdate(String incrementStep, String totalStep, String eventValue);
    }
}
