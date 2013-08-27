package edu.uiowa.csense.compiler.model.project;

public class Activity {
    protected String _className;
    protected String _layoutName;
    protected boolean _main;

    public Activity(String className, String layoutName, boolean main) {
        _className = className;
        _layoutName = layoutName;
        _main = main;
    }

    public void setMainActivity(boolean main) {
        _main = main;
    }

    public String getClassName() {
        return _className;
    }

    public String getLayoutName() {
        return _layoutName;
    }

    public boolean isMain() {
        return _main;
    }
}
