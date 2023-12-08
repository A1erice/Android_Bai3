package com.example.myapplication2;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.Objects;

public class FileListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filelist);
        //Create listview
        ListView listView = findViewById(R.id.fileListView);
        ArrayAdapter<String> adapter = null;
        listView.setAdapter(null);
        //Set path
        String path = getIntent().getStringExtra("path");
        assert path != null;
        File root = new File(path);
        File[] filesAndFolders = root.listFiles();
        //Set current path
        TextView textView = findViewById(R.id.currentPathTextView);
        textView.setText(path);
        //Set subfiles
        String[] fileNames = new String[0];
        if (filesAndFolders != null) {
            // Create an array of file names for the ArrayAdapter
            fileNames = new String[filesAndFolders.length];
            for (int i = 0; i < filesAndFolders.length; i++) {
                fileNames[i] = filesAndFolders[i].getName();
            }
            // Create an ArrayAdapter to bind the data to the ListView
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fileNames);
            // Set the adapter for the ListView
            listView.setAdapter(adapter);
        } else {
            // Handle the case where filesAndFolders is null
            Toast.makeText(this, "Error reading files and folders", Toast.LENGTH_SHORT).show();
        }

        Button backBtn = findViewById(R.id.backButton);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnFolder(path);
            }
        });

        // Create an ArrayAdapter to bind the data to the ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fileNames);

        // Set the adapter for the ListView
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = (String) parent.getItemAtPosition(position);
            File selectedFile = new File(getIntent().getStringExtra("path"), selectedItem);
            if (isMusicFile(selectedItem)) {
                // It's a music file, you can open it or perform any other action
                openMusicFile(selectedFile.getPath());
            } else if (selectedFile.isDirectory()) {
                // It's a folder, you can open it or perform any other action
                openFolder(this,selectedFile.getAbsolutePath());
            } else {
                // It's not a music file, you can handle other file types or show a message
                System.out.println("Selected item is not a music file: " + selectedItem);
            }
            System.out.println("Selected item: " + selectedItem);
        });
    }
    private void returnFolder(String folderPath) {
        Intent intent = new Intent(this, FileListActivity.class);

        // Extract the parent directory path
        File currentFolder = new File(folderPath);
        String parentFolderPath = currentFolder.getParent();
        String rootPath = Environment.getExternalStorageDirectory().getPath();
        if (!Objects.equals(folderPath, rootPath)) {
            // If there's a parent directory, set it as the new path
            intent.putExtra("path", parentFolderPath);
            startActivity(intent);
        } else {
            // If there's no parent directory, you are already at the root, handle accordingly
            Toast.makeText(this, "Already at the root directory", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean isMusicFile(String fileName) {
        // Assuming music files have extensions like .mp3, .wav, etc.
        String[] musicExtensions = {".mp3", ".wav", ".ogg"}; // Add more extensions as needed
        for (String extension : musicExtensions) {
            if (fileName.toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
    private void openMusicFile(String path) {
        Intent intent = new Intent(FileListActivity.this, MusicPlayerActivity.class);
        intent.putExtra("file", path);  // Use "file" as the key
        startActivity(intent);
    }

    private void openFolder(Context context, String folderPath) {
        Intent intent = new Intent(context, FileListActivity.class);
        intent.putExtra("path", folderPath);
        context.startActivity(intent);
    }
}