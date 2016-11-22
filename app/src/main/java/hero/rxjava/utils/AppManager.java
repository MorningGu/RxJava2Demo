package hero.rxjava.utils;

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.util.Stack;

public enum AppManager {
	INSTANCE;
	private Stack<WeakReference<Activity>> activityStack;

	AppManager() {
		if (activityStack == null) {
			activityStack = new Stack<WeakReference<Activity>>();
		}
	}

	/**
	 * 添加Activity到堆栈
	 */
	public synchronized void addActivity(WeakReference<Activity> activity) {
		activityStack.add(activity);
	}

	/**
	 * 将传入的Activity对象从栈中移除
	 * @param task
	 */
	public  void removeTask(WeakReference<Activity> task) {
		activityStack.remove(task);
	}
	/**
	 * 移除全部（用于整个应用退出）
	 */
	public void finishAllActivity() {
		//finish所有的Activity
		for (WeakReference<Activity> task : activityStack) {
			if (task.get()!=null && !task.get().isFinishing()) {
				task.get().finish();
			}
		}
		activityStack.removeAllElements();
	}

}