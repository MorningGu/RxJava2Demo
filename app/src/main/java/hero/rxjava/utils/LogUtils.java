package hero.rxjava.utils;

import android.util.Log;

/**
 * Created by hero on 2016/8/30 0030.
 */

public class LogUtils {
    private static boolean isDebug = false;
    public static void isDebug(boolean debug){
        isDebug = debug;
    }
    public static int v(String tag, String msg) {
        if(isDebug){
            return Log.v(tag, msg);
        }
        return 0;
    }

    public static int d(String tag, String msg) {
        if(isDebug){
            return Log.d(tag, msg);
        }
        return 0;
    }


    public static int i(String tag, String msg) {
        if(isDebug){
            return Log.i(tag, msg);
        }
        return 0;
    }


    public static int w(String tag, String msg) {
        if(isDebug){
            return Log.w(tag, msg);
        }
        return 0;
    }


    public static int e(String tag, String msg) {
        if(isDebug){
            return Log.e(tag, msg);
        }
        return 0;
    }
}
