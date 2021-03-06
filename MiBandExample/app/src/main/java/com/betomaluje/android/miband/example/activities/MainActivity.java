package com.betomaluje.android.miband.example.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.betomaluje.android.miband.example.MiBandApplication;
import com.betomaluje.android.miband.example.R;
import com.betomaluje.android.miband.example.wizard.UserWizardActivity;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

/**
 * Created by betomaluje on 7/20/15.
 */
public class MainActivity extends BaseActivity {

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    protected boolean getDisplayHomeAsUpEnabled() {
        return false;
    }

    void check(){
        //Ask for permission to intercept notifications on first run.
        final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        if (sharedPrefs.getBoolean("firstrun", true)) {
            //sharedPrefs.edit().putBoolean("firstrun", false).apply();

            startActivity(new Intent(MainActivity.this, UserWizardActivity.class));
            finish();
        } else {
            if (sharedPrefs.getBoolean("notification_permission", true)) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        MainActivity.this);

                alertDialogBuilder.setTitle("Need Permission");

                alertDialogBuilder
                        .setMessage("In order to notify you of any new notification, we need to configure the settings in your phone. Would yo like to go now?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                sharedPrefs.edit().putBoolean("notification_permission", false).apply();
                                Intent enableIntent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                                startActivity(enableIntent);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }

            Button btn_normal = (Button) findViewById(R.id.btn_normal);
            Button btn_service = (Button) findViewById(R.id.btn_service);

            View.OnClickListener btnListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.btn_normal:
                            MiBandApplication.setFrom(MiBandApplication.from.ACTIVITY);
                            startActivity(new Intent(MainActivity.this, MainNormalActivity.class));
                            break;
                        case R.id.btn_service:
                            MiBandApplication.setFrom(MiBandApplication.from.SERVICE);
                            startActivity(new Intent(MainActivity.this, MainServiceActivity.class));
                            break;
                    }
                }
            };

            btn_normal.setOnClickListener(btnListener);
            btn_service.setOnClickListener(btnListener);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                check();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                finish();
            }


        };


        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .check();


    }
}
