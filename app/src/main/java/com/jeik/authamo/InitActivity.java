package com.jeik.authamo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class InitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        //firstStartCheck();

    }

    private void firstStartCheck() {
        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);
        if (!isFirstRun) {
            startActivity(new Intent(InitActivity.this, MainActivity.class));
        }
        else
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", false).commit();
    }

    public void discordInvite(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://discord.gg/guVeuQr")));
    }

    public void yeetOn(View view) {
        startActivity(new Intent(InitActivity.this, MainActivity.class));
    }
}
