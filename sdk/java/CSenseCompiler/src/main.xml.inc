<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical" >

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_weight="1"
        android:orientation="horizontal" >

        <Button
            android:id="@+id/start"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:onClick="start"
            android:text="Start" />
            
      <Button
            android:id="@+id/pause"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:onClick="pause"
            android:text="Pause" />
            

    <Button
            android:id="@+id/stop"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:onClick="stop"
            android:text="Stop" />
            
  
    </LinearLayout>

    <TextView
        android:id="@+id/egoapp"
        android:layout_width="match_parent"
        android:layout_height="36dp"
        android:ems="10"
        android:text="AppName" />

</LinearLayout>
