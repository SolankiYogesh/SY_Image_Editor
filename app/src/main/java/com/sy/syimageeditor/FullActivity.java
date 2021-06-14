package com.sy.syimageeditor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FullActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full);

        String FIle_Path = getIntent().getStringExtra("filepath");
        Toast.makeText(getApplicationContext(),FIle_Path,Toast.LENGTH_SHORT).show();
        Uri uri = Uri.parse(getIntent().getStringExtra("uri"));
        ImageView image = findViewById(R.id.image);
        image.setImageURI(uri);
        Button  exit = findViewById(R.id.exit);
        exit.setOnClickListener(v -> onBackPressed());

    }
}