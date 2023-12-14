package com.example.myapplication2;

import android.content.Context;
        import android.content.Intent;
        import android.os.Bundle;
        import android.os.Environment;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.widget.Toast;
        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.File;
        import java.util.Objects;

public class FileListFragment extends Fragment {

    public static FileListFragment newInstance(String path) {
        FileListFragment fragment = new FileListFragment();
        Bundle args = new Bundle();
        args.putString("path", path);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filelist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView listView = view.findViewById(R.id.fileListView);
        ArrayAdapter<String> adapter = null;
        listView.setAdapter(null);

        assert getArguments() != null;
        String path = getArguments().getString("path");
        assert path != null;
        File root = new File(path);
        File[] filesAndFolders = root.listFiles();

        TextView textView = view.findViewById(R.id.currentPathTextView);
        textView.setText(path);

        String[] fileNames;
        if (filesAndFolders != null) {
            fileNames = new String[filesAndFolders.length];
            for (int i = 0; i < filesAndFolders.length; i++) {
                fileNames[i] = filesAndFolders[i].getName();
            }
            adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, fileNames);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener((parent, view1, position, id) -> {
                String selectedItem = (String) parent.getItemAtPosition(position);
                File selectedFile = new File(requireArguments().getString("path"), selectedItem);
                if (isMusicFile(selectedItem)) {
                    openMusicFile(selectedFile.getPath());
                } else if (selectedFile.isDirectory()) {
                    openFolder(selectedFile.getAbsolutePath());
                } else {
                    System.out.println("Selected item is not a music file: " + selectedItem);
                }
                System.out.println("Selected item: " + selectedItem);
            });
        } else {
            Toast.makeText(requireContext(), "Error reading files and folders", Toast.LENGTH_SHORT).show();
        }

        Button backBtn = view.findViewById(R.id.backButton);
        Button exitBtn = view.findViewById(R.id.exitButton);
        Button pickBtn = view.findViewById(R.id.openButton);
        backBtn.setOnClickListener(v -> returnFolder(path));
        exitBtn.setOnClickListener(v -> requireActivity().finish());
        pickBtn.setOnClickListener(v -> openMusicFile(path));
    }

    private void returnFolder(String folderPath) {
        File currentFolder = new File(folderPath);
        String parentFolderPath = currentFolder.getParent();
        String rootPath = Environment.getExternalStorageDirectory().getPath();

        if (!Objects.equals(folderPath, rootPath)) {
            // Replace the current fragment with a new instance of FileListFragment
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, FileListFragment.newInstance(parentFolderPath));
            transaction.addToBackStack(null);
            transaction.commit();
        } else {
            Toast.makeText(requireContext(), "Already at the root directory", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean isMusicFile(String fileName) {
        String[] musicExtensions = {".mp3", ".wav", ".ogg"};
        for (String extension : musicExtensions) {
            if (fileName.toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }


    private void openMusicFile(String path) {
        Intent intent = new Intent(requireContext(), MusicPlayerActivity.class);
        intent.putExtra("file", path);
        startActivity(intent);
    }

    private void openFolder(String folderPath) {
        // Update the current fragment with a new instance of FileListFragment
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, FileListFragment.newInstance(folderPath));
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
