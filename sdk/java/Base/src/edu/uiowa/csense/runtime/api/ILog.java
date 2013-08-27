package edu.uiowa.csense.runtime.api;

import java.util.logging.Level;

public interface ILog {
    public final int OFF = 256;
    public final int ALL = 0;
    public final int VERBOSE = 1;
    public final int DEBUG = 2;
    public final int WARN = 3;
    public final int INFO = 4;
    public final int ERROR = 5;

    public void d(String tag, Object... args);
    public void e(String tag, Object... args);
    public void i(String tag, Object... args);
    public void w(String tag, Object... args);
    public void v(String tag, Object... args);
    public void setLevel(Level level);
}
