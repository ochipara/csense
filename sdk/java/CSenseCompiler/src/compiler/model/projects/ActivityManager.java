package compiler.model.projects;

import java.util.ArrayList;
import java.util.List;

import compiler.CompilerException;




public class ActivityManager {
    public final List<Activity> _activities = new ArrayList<Activity>();

    public ActivityManager() {
        // TODO:
//        _activities.add(new Activity("edu.uiowa.csense.EgoDeployActivity", "main.xml", true));
    }

    public void addActivity(Activity activity) {
        _activities.add(activity);
    }

    public Activity getMainActivity() throws CompilerException {
        Activity main = null;
        int count = 0;
        for (Activity activity : _activities) {
            if(activity.isMain()) {
        	main = activity;
        	count += 1;
            }
        }

        if (count > 1)
            throw new CompilerException("Multiple main activities");

        return main;
    }

    public List<Activity> getActivities() {
        return _activities;
    }
}
