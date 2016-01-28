package myxgpush.snscity.com.myapplication;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;


@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
@SuppressLint("WorldReadableFiles")
public class HttpApplication extends Application implements
        UncaughtExceptionHandler {
    public static final boolean isDebugServer = false;
    private UncaughtExceptionHandler mDefaultHandler;
    private static HttpApplication Instance;

    public static HttpApplication getInstance() {
        return Instance;
    }


    public static boolean isBackground() {
        Context context = HttpApplication.getInstance();

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {

                if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return false;
                }
                return true;
                // if (appProcess.importance ==
                // RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                // return true;
                // } else {
                // return true;
                // }
            }
        }
        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Instance = this;
        initThreadPool(this);

        // 获取系统默认的UncaughtException处理?
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置重写的uncaughtException为程序的默认处理?
        Thread.setDefaultUncaughtExceptionHandler(this);

    }

    /**
     * 获得当前进程号
     *
     * @param context
     * @return
     */
    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }


    protected static ThreadPoolExecutor threadPool;

    @SuppressWarnings("deprecation")
    public static void initThreadPool(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                "me.maodou.transaction", Context.MODE_WORLD_READABLE);

        int size = preferences.getInt("threadpool.size", 0);
        int maxsize = preferences.getInt("threadpool.maxsize", 5);
        int waitsize = preferences.getInt("threadpool.waitsize", 300);
        long alive = preferences.getInt("threadpool.alive_time", 1000);
        int policy = preferences.getInt("threadpool.policy", 3);

        ArrayBlockingQueue<Runnable> deques = new ArrayBlockingQueue<Runnable>(
                waitsize);
        RejectedExecutionHandler handler = null;

        switch (policy) {
            case 0:
                handler = new ThreadPoolExecutor.AbortPolicy();
                break;

            case 1:
                handler = new ThreadPoolExecutor.CallerRunsPolicy();
                break;

            case 2:
                handler = new ThreadPoolExecutor.DiscardOldestPolicy();
                break;

            default:
            case 3:
                handler = new ThreadPoolExecutor.DiscardPolicy();
                break;
        }

        threadPool = new ThreadPoolExecutor(size, maxsize, alive,
                TimeUnit.MILLISECONDS, deques, handler);
    }

    public static ThreadPoolExecutor getThreadPool() {
        return threadPool;
    }

    // 运用list来保存们每一个activity是关键
    public static List<Activity> mList = new LinkedList<Activity>();

    // add Activity
    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    // 关闭list内的一个activity
    public static void exitActivity() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 关闭每一个list内的activity
    public static void exit() {

        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // System.exit(0);
        }
    }


    /**
     * @作者： 马自强
     * @创建日期： 2013-12-17
     * <p/>
     * 异常监听
     */
    @SuppressWarnings("deprecation")
    @SuppressLint({"NewApi", "SimpleDateFormat"})
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处??
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw, true);
            // 获得异常信息
            ex.printStackTrace(pw);

            String sdk = Build.VERSION.SDK; // SDK号
            String Model = Build.MODEL; // 手机型号
            String S = Build.VERSION.RELEASE; // android系统版本号

            String message = "手机型号："
                    + Model
                    + ",Android系统版本号"
                    + S
                    + ",Sdk号："
                    + sdk
                    + ",项目版本号："
                    + getVerName(getApplicationContext())
                    + ",用户ID"
                    + sw.getBuffer().toString();
            System.out.println(message);


        }
    }

    /**
     * @param 线程弹出自定义提示
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        return true;
    }

    /**
     * 获得当前版本名称
     *
     * @param context
     * @return
     */
    private String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(
                    "me.maodou.model_client", 0).versionName;
        } catch (NameNotFoundException e) {
        }
        return verName;

    }

}
