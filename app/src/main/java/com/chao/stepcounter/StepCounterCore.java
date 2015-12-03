package com.chao.stepcounter;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.chao.stepcounter.ui.MainActivity;

/**
 * Created by chao on 2015/11/25.
 */
public class StepCounterCore {
    private ACache aCache;
    private SensorManager sensorManager;
    private Sensor countSensor;

    private static StepCounterCore instance;

    private MainActivity.UICallback callback;

    // cache key totalStep
    public static final String KEY_CACHE_TOTAL_STEP = "key_cache_total_step";
    // cache key incrementStep，只是base，使用时加上当天0点时间戳
    public static final String KEY_CACHE_INCREMENT_STEP_BASE = "key_cache_increment_step";

    // Config
    // 同步间隔时长
    public static final long SYNC_INTERVAL = 10000;//10s
    // incrementStep缓存时间
    public static final int TIME_CACHE_INCREMENT_STEP = 3 * ACache.TIME_DAY;

    // 从上次计步到这次计步步数的增量
    private int incrementStep;
    // 从注册listener到现在所有的步数
    private int totalStep;

    private StepCounterCore(Context context) {
        aCache = ACache.get(context);
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    }

    /**
     * 单例获取实例
     *
     * @param context
     * @return StepCounterCore实例
     */
    public static StepCounterCore getInstance(Context context) {
        if (instance == null) {
            instance = new StepCounterCore(context);
        }
        return instance;
    }

    /**
     * 检查计步Sensor是否存在
     *
     * @param context
     * @return Sensor是否为空
     */
    public static boolean checkStepSensor(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        return sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null;
    }

    public void registerStepCounterListener() {
        sensorManager.registerListener(sensorEventListener, countSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void unregisterStepCounterListener() {
        sensorManager.unregisterListener(sensorEventListener);
    }

    SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            // 读取totalStep
            String totalStepString = aCache.getAsString(KEY_CACHE_TOTAL_STEP);
            if (totalStepString == null || "".equals(totalStepString)) {
                totalStep = 0;
            } else {
                totalStep = Integer.parseInt(totalStepString);
            }
            // 读取缓存的incrementStep
            String incrementStepString = aCache.getAsString(KEY_CACHE_INCREMENT_STEP_BASE + Util.getTimesMorning());
            if (incrementStepString == null || "".equals(incrementStepString)) {
                incrementStep = 0;
            } else {
                incrementStep = Integer.parseInt(incrementStepString);
            }

            // 获取Sensor的步数
            int eventValue = (int) event.values[0];
            if (totalStep < 1) {
                // 之所以要在这里初始化total是为了防止第一次注册Sensor时Sensor的步数不为0，导致误判
                // 下面的incrementStep = eventValue - totalStep也是为了这个
                totalStep = eventValue;
            }
            // 说明中间重启过，eventValue归零，所以totalStep大于eventValue
            if (totalStep > eventValue) {
                // eventValue即从重启到现在增加的步数
                incrementStep += eventValue;// 注意是+=
            } else {
                // 正常情况下计算增长的步数
                incrementStep += (eventValue - totalStep);
            }

            // totalStep重新初始化
            totalStep = eventValue;

            Log.e("hah", "eventValue" + eventValue);
            Log.e("hah", "totalStep" + totalStep);
            Log.e("hah", "incrementStep" + incrementStep);

            // 保存totalStep到本地
            aCache.put(KEY_CACHE_TOTAL_STEP, totalStep + "");
            // 保存incrementStep到本地
            aCache.put(KEY_CACHE_INCREMENT_STEP_BASE + Util.getTimesMorning(), incrementStep + "", TIME_CACHE_INCREMENT_STEP);

            // 弃用 立即解除注册，防止一直有数据过来
//            unregisterStepCounterListener();

            //debug update UI
            if (callback != null) {
                callback.onStepUpdate(incrementStepString, totalStepString, eventValue + "");
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public void syncServerData() {
        String incrementStepString = aCache.getAsString(KEY_CACHE_INCREMENT_STEP_BASE + Util.getTimesMorning());
        if (incrementStepString == null || "".equals(incrementStepString)) {
            incrementStep = 0;
        } else {
            incrementStep = Integer.parseInt(incrementStepString);
        }

        //TODO 上传incrementStep到服务器
        // 如果上传成功清除本地保存incrementStep
//            if(上传成功){
//                //清除本地incrementStep，置为0
//                aCache.put(KEY_CACHE_INCREMENT_STEP_BASE + Util.getTimesMorning(), 0, TIME_CACHE_INCREMENT_STEP);
//            }
    }

    public void registerUICallback(MainActivity.UICallback callback) {
        this.callback = callback;
    }

}
