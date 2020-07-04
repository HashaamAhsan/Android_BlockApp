package com.karwash.blockapp;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class BlockService extends Service {
    SharedPreferences sharedPreferences;
    private boolean isRunning  = false;
    String killiapp;
    public Handler handler = null;
    public static Runnable runnable = null;

    @Override
    public void onCreate() {
        handler=new Handler();
    }

    public int onStartCommand (Intent intent, int flags, int startId) {
        killiapp=(String) intent.getExtras().get("AppName");
        System.out.println("Service Started");
        Context c=this;
        final ReentrantLock lock = new ReentrantLock();
        lock.lock();
        killapp(c,killiapp);
        return START_NOT_STICKY;
    }

    public void killapp(final Context c, final String killingapp){
        synchronized(this){
            final Thread x=new Thread(new Runnable() {

                @Override
                public void run() {
                    while (true){
                        String s= getTopAppName(c);
                        //System.out.print("hey "+s+" hey\n");
                        //String killingapp = sharedPreferences.getString("name", null);
                        if (s.indexOf(killingapp)>0){
                            Intent notificationIntent = new Intent(c, Blockmsg.class);
                            PendingIntent notificationPendingIntent = PendingIntent.getActivity
                                    (c, 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            try {
                                notificationPendingIntent.send();
                            } catch (PendingIntent.CanceledException e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });
            x.start();
        }
    }
    public static String getTopAppName(Context context) {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String strName = "";
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                strName = getLollipopFGAppPackageName(context);
            } else {
                strName = mActivityManager.getRunningTasks(1).get(0).topActivity.getClassName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strName;
    }
    private static String getLollipopFGAppPackageName(Context ctx) {

        try {
            UsageStatsManager usageStatsManager = (UsageStatsManager) ctx.getSystemService("usagestats");
            long milliSecs = 60 * 1000;
            Date date = new Date();
            List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, date.getTime() - milliSecs, date.getTime());
            if (queryUsageStats.size() > 0) {
                //Log.i("LPU", "queryUsageStats size: " + queryUsageStats.size());
            }
            long recentTime = 0;
            String recentPkg = "";
            for (int i = 0; i < queryUsageStats.size(); i++) {
                UsageStats stats = queryUsageStats.get(i);
                if (i == 0 && !"com.karwash.blockapp".equals(stats.getPackageName())) {
                    //Log.i("LPU", "PackageName: " + stats.getPackageName() + " " + stats.getLastTimeStamp());
                }
                if (stats.getLastTimeStamp() > recentTime) {
                    recentTime = stats.getLastTimeStamp();
                    recentPkg = stats.getPackageName();
                }
            }
            return recentPkg;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
