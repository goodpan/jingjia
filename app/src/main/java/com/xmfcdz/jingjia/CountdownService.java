package com.xmfcdz.jingjia;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class CountdownService extends Service {
    public CountdownService() {

    }

    /**
     * 在 Local Service 中我们直接继承 Binder 而不是 IBinder,因为 Binder 实现了 IBinder 接口，这样我们可以少做很多工作。
     * @author newcj
     */
    public class SimpleBinder extends Binder {
        /**
         * 获取 Service 实例
         * @return
         */
        public CountdownService getService(){
            return CountdownService.this;
        }

        public void startCountDown(){
            Log.e("startCount","is started");
        }

    }
    public SimpleBinder sBinder;

    @Override
    public IBinder onBind(Intent intent) {
        // 返回 SimpleBinder 对象
        Log.e("bind","service is binded");

        return sBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 创建 SimpleBinder
        sBinder = new SimpleBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int retVal = super.onStartCommand(intent, flags, startId);
        return retVal;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
