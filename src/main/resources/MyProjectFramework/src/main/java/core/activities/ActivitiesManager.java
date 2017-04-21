package $Package.core.activities;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;

import java.util.Stack;

/**
 * 管理Activity
 * Created by Vincent on $Time.
 */
public class ActivitiesManager {
	private static Stack<Object> activityStack;
	private static volatile ActivitiesManager instance;

	private ActivitiesManager() {
		activityStack = new Stack<Object>();
	}

	public static ActivitiesManager getInstance() {
		if (instance == null) {
			synchronized (ActivitiesManager.class) {
				if (instance == null) {
					instance = new ActivitiesManager();
				}
			}
		}
		return instance;
	}

	
	/**
	 * 移出最后一个Activity
	 * @param isFinish 是否结束Activity
	 */
	public void popActivity(boolean isFinish) {
		Object activity= activityStack.lastElement();
		if (activity != null) {
			if (isFinish) {
				if (activity instanceof Activity) {
					((Activity) activity).finish();
				} else if (activity instanceof FragmentActivity) {
					((FragmentActivity) activity).finish();
				}
			}
			activityStack.remove(activity);
			activity = null;
		}	
	}

    /**
     * 移出所有的Activity
     */
    public void popAllActivity() {
        for (Object activity : activityStack) {
            if (activity instanceof Activity) {
                ((Activity) activity).finish();
            } else if (activity instanceof FragmentActivity) {
                ((FragmentActivity) activity).finish();
            }
            activityStack.remove(activity);
            activity = null;
        }
    }

	/**
	 * 移出指定的Activity
	 * @param activity
	 * @param isFinish 是否结束Activity
	 */
	public void popActivity(Object activity, boolean isFinish) {
		if (activity != null) {
			if (isFinish) {
				if (activity instanceof Activity) {
					((Activity) activity).finish();
				} else if (activity instanceof FragmentActivity) {
					((FragmentActivity) activity).finish();
				}
			}			
			activityStack.remove(activity);
			activity = null;
		}
	}

	/**
	 * 取得最后一个Activity
	 * @return 
	 */
	public Object getLastActivity() {
        if (activityStack.size() > 0) {
            return activityStack.lastElement();
        } else {
            return null;
        }
	}

	/**
	 * 获取特定Activity
	 * @param cls
	 * @return
	 */
	public Object getActivity(Class cls) {
		for (Object activity : activityStack) {
			if (activity == null) {
				return null;
			}
			if (activity instanceof Activity) {
				if (((Activity) activity).getClass().equals(cls)) {
					return activity;
				}
			} else if (activity instanceof FragmentActivity) {
				if (((FragmentActivity) activity).getClass().equals(cls)) {
					return activity;
				}
			}
		}
		return null;
	}

	/**
	 * 放入一个Activity
	 * @param activity
	 */
	public void pushActivity(Object activity) {
		if (activityStack == null) {
			activityStack = new Stack<Object>();
		}
		activityStack.add(activity);
	}

	/**
	 * 移出所有Activity除了cls这个Activity并关闭
	 * @param cls
	 * @param isFinish 是否结束Activity
	 */
	public void popAllActivityExceptOne(Class cls, boolean isFinish) {
		while (true) {
			Object activity = getLastActivity();
			if (activity == null) {
				break;
			}
			if (activity instanceof Activity) {
				if (((Activity) activity).getClass().equals(cls)) {
					break;
				}
			} else if (activity instanceof FragmentActivity) {
				if (((FragmentActivity) activity).getClass().equals(cls)) {
					break;
				}
			}
			popActivity(activity, isFinish);
		}
	}

    /**
     * 检查activity是否存在
     * @param cls
     * @return
     */
    public boolean checkActivityExist(Class cls) {
        for (Object activity : activityStack) {
            if (activity instanceof Activity) {
                if (((Activity) activity).getClass().equals(cls)) {
                    return true;
                }
            }
        }
        return false;
    }

	/**
	 * 移除所有
	 */
	public void removeAllElements() {
		activityStack.removeAllElements();
	}

	/**
	 * 关闭
	 */
	public void close() {
		activityStack.removeAllElements();
		activityStack = null;
		instance = null;
	}
}