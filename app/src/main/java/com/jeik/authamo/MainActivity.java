package com.jeik.authamo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private ListView lvAuthKeys;
    private String status;
    private List<File> authKeys = new ArrayList<>();
    private ArrayAdapter<File> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firstStartCheck();
        checkState();
        init();
        storageCheck(); // You can't find or touch AuthKeys without file read/write permissions, DUH

    }


    private void saveState(String input){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putString("status", input).commit();
    }

    private void checkState() {
        status=getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("status","");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.info:
                startActivity(new Intent(MainActivity.this, InitActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void init() {
        lvAuthKeys = findViewById(R.id.lvAuthKeys);
    }

    private void firstStartCheck() {
        loadKeys();
        checkKeyState();
        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);
        if (isFirstRun) {
            startActivity(new Intent(MainActivity.this, InitActivity.class));
        }
        else setContentView(R.layout.activity_main);
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", false).commit();
    }

    private void checkKeyState() {
        boolean altExists = false;
        for (File f:authKeys) {
            if (f.getName().equals("AuthKeyAlt")) {
                altExists = true;
            }
        }
        if (altExists){
            saveState("main");
        }
        else saveState("alt");
    }

    private void storageCheck() {
        if(Build.VERSION.SDK_INT >= 23)
        {
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.KILL_BACKGROUND_PROCESSES},
                        1);
            }
            else showItems();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode)
        {
            case 1:
                if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED) showItems();
                else closeNow();
                break;
        }
    }

    private void closeNow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) finishAffinity();
        else finish();
    }
    private void showItems() {
        //Get a list of all the files in the Housamo Data folder
        loadKeys();
        //Shove the "chosen few" into a ListView(simply put a list of buttons)
        adapter = new ItemAdapter(this, -1, authKeys);
        lvAuthKeys.setDividerHeight(0); // Removed the fugly divider between buttons
        lvAuthKeys.setDivider(null); // ^^^^
        lvAuthKeys.setAdapter(adapter);
        }

    private void loadKeys() {
        authKeys.clear();
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/Android/data/jp.co.lifewonders.housamo/files/Data/");
        File[] files = dir.listFiles();
        //Go through the list of all files and pick only those which have "AuthKey" in their name
        if(files!=null){
            for (File meh : files) {
                if (meh.getName().startsWith("AuthKey")) {
                    authKeys.add(meh);
                }
            }
        }
    }

    public void switcheroo(View view) {
        String dir = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/Android/data/jp.co.lifewonders.housamo/files/Data/";
        for (File f:authKeys) {
            if (f.getName().equals("AuthKey") && status.equals("main")){
                f.renameTo( new File(dir+"AuthKeyMain"));
                loadKeys();
                for (File x:authKeys) {
                    if (x.getName().equals("AuthKeyAlt")){
                        x.renameTo( new File(dir+"AuthKey"));
                    }
                }
                saveState("alt");
            }
            else if (f.getName().equals("AuthKey") && status.equals("alt")){
                f.renameTo( new File(dir+"AuthKeyAlt"));
                for (File x:authKeys) {
                    if (x.getName().equals("AuthKeyMain")){
                        x.renameTo( new File(dir+"AuthKey"));
                    }
                }
                saveState("main");
            }
        }
        ActivityManager am = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
        am.killBackgroundProcesses("jp.co.lifewonders.housamo");
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("jp.co.lifewonders.housamo");
        if (launchIntent!=null) startActivity(launchIntent);
        finish();
        System.exit(0);
    }
}
