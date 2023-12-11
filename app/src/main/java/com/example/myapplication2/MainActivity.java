package com.example.myapplication2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        toolbar.setTitle("My Music Player");
        setSupportActionBar(toolbar);

        // Check and request permission if needed
        if (checkPermission()) {
            // Check if the activity was started from the player
            Intent intent = getIntent();
            if (intent != null && intent.getBooleanExtra("fromPlayer", false)) {
                // The activity was started from the player, retrieve the path from the intent
                String path = intent.getStringExtra("path");
                openFileListFragment(path);
            } else {
                // The activity was not started from the player, open the default fragment
                String path = Environment.getExternalStorageDirectory().getPath();
                openFileListFragment(path);
            }
        } else {
            // Permission not allowed, request permission
            requestPermission();
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity.this, "Storage permission is required, please allow from settings", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111);
        }
    }

    private void openFileListFragment(String path) {
        // Create a new instance of FileListFragment with the specified path
        FileListFragment fileListFragment = FileListFragment.newInstance(path);

        // Replace the current fragment with FileListFragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fileListFragment); // R.id.fragment_container is the ID of the container where the fragment will be placed
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
