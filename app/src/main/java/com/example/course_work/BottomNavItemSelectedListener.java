package com.example.course_work;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {
    private OnActivitySwitchListener activitySwitchListener;
    private Context context;

    public BottomNavItemSelectedListener(Context context) {
        this.context = context;
    }

    public void setOnActivitySwitchListener(OnActivitySwitchListener listener) {
        this.activitySwitchListener = listener;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_rules) {
            if (activitySwitchListener != null) {
                activitySwitchListener.onActivitySwitch();
            }
            Intent intentRules = new Intent(context, MainActivity2.class);
            context.startActivity(intentRules);
            return true;
        } else if (itemId == R.id.action_play) {
            Intent intentPlay = new Intent(context, MainActivity.class);
            context.startActivity(intentPlay);
            return true;
        } else if (itemId == R.id.action_stats) {
            if (activitySwitchListener != null) {
                activitySwitchListener.onActivitySwitch();
            }
            Intent intentStats = new Intent(context, MainActivity3.class);
            context.startActivity(intentStats);
            return true;
        }
        return false;
    }
}


