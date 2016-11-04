package hero.rxjava.utils;

import android.app.Activity;
import android.content.Context;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public enum AppManager {
	INSTANCE;
	private  Stack<Activity> activityStack;
	private static AppManager instance;

	AppManager() {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
	}

	/**
	 * 添加Activity到堆栈
	 */
	public synchronized void addActivity(Activity activity) {
		
		activityStack.add(activity);
	}

	/**
	 * 获取当前Activity（堆栈中最后一次压入的）
	 */
	public Activity currentActivity() {
		Activity activity = activityStack.lastElement();
		return activity;
	}

	/**
	 * 结束当前Activity（堆栈中最后一次压入的）
	 */
	public void finishActivity() {
		try {
			Activity activity = activityStack.lastElement();
			if (activity != null) {
				activityStack.remove(activityStack.size()-1);
				activity.finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 结束指定的Activity
	 */
	public void finishActivity(Activity activity) {
		if (activity != null && activityStack.contains(activity)) {
			activityStack.remove(activity);
			activity.finish();
		}
	}

	/**
	 * 结束指定类名的Activity
	 */
	public void finishActivity(Class<?> cls) {
		try {
			Iterator itr = activityStack.iterator();
		    while (itr.hasNext()) {  
		    	Activity activity = (Activity) itr.next();
		    	if (activity.getClass().equals(cls)) {
		    		activity.finish();
		    		activityStack.remove(activity);
				}
		    }
		    itr = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 结束除开指定类名Activity外的所有Activity
	 */
	public void finishAllExceptActivity(Class<?> cls) {
		try {
			Iterator itr = activityStack.iterator();
			List<Activity> activityRemoves = new ArrayList<Activity>();
		    while (itr.hasNext()) {  
		    	Activity activity = (Activity) itr.next();
		    	if (!activity.getClass().equals(cls)) {
		    		activity.finish();
					activityRemoves.add(activity);
				}
		    }
		    activityStack.removeAll(activityRemoves);
		    activityRemoves.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 结束所有堆栈Activity
	 */
	public void finishAllActivity() {
		try {
			for (int i = 0, size = activityStack.size(); i < size; i++) {
				if (null != activityStack.get(i)) {
					activityStack.get(i).finish();
				}
			}
			activityStack.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 退出应用程序
	 */
	public void AppExit(Context context) {
		try {
			finishAllActivity();
		} catch (Exception e) {
		}
	}
	/**
	 * 查询指定的Activity是否已经存在
	 */
	public boolean findAppointActivity(Class<?> cls) {
		try{
			Iterator itr = activityStack.iterator();
		    while (itr.hasNext()) {  
		    	Activity activity = (Activity) itr.next();
		    	if (activity.getClass().equals(cls)) {
					return true;
				}
		    }
			return false; 
		}catch(Exception e){
			return false;
		}

	}
	/**
	 * 结束到activity，不包括该activity
	 * @param cls
	 */
	public void finishEndToActivity(Class<?> cls) {
		try{
			while(activityStack.size() > 0){
				Activity activity = activityStack.lastElement();
				if (activity != null) {
					if(activity.getClass().equals(cls)){
						break;
					}else{
						activityStack.remove(activityStack.size()-1);
						activity.finish();
					}
				}
			}
		}catch(Exception e){
			
		}
	}
	/**
	 * 结束到activity，包括该activity
	 * @param cls
	 */
	public void finishToActivity(Class<?> cls) {
		try{
			while(activityStack.size() > 0){
				Activity activity = activityStack.lastElement();
				if (activity != null) {
					if(activity.getClass().equals(cls)){
						activityStack.remove(activityStack.size()-1);
						activity.finish();
						break;
					}else{
						activityStack.remove(activityStack.size()-1);
						activity.finish();
					}
				}
			}
		}catch(Exception e){
			
		}
	}
}