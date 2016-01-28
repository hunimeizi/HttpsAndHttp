package myxgpush.snscity.com.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;

/**
 * Created by lyb .
 * Data on 2015/10/20
 * Class
 */

public class BaseActivity extends Activity {

    protected Context mContext;

    public static String EVENT_NOTIFYCATION = "event_notifycation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
        this.mContext = this;
    }

    public void onDestroy(){

        super.onDestroy();
    }
}