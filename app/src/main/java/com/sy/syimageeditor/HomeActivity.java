package com.sy.syimageeditor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 104;
    String currentPhotoPath;
    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ImageView pic = findViewById(R.id.image_pic);
        imageView = findViewById(R.id.image_view);
        ImageView camera = findViewById(R.id.cameeera);

        camera.setOnClickListener(v -> checkCameraPermtion());
        pic.setOnClickListener(v -> checkPermission());
    }

    private void checkCameraPermtion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        } else {
            dispatchTakePictureIntent();
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void dispatchTakePictureIntent() {
        ImagePicker.with(this).cameraOnly().start(CAMERA_REQUEST_CODE);
    }

    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void checkPermission() {
        int permition = ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            pickImage();
        } else {
            if (permition != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            } else {
                pickImage();
            }
        }
    }


    private void pickImage() {
        Intent intent = new Intent(
                Intent.ACTION_PICK
        );
        intent.setType("image/*");
        startActivityForResult(intent, 100);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickImage();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                Uri contentUri = data.getData();
                Intent intent = new Intent(HomeActivity.this, DsPhotoEditorActivity.class);
                intent.setData(contentUri);
                intent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY, "SY IMAGE EDITOR");
                intent.putExtra(DsPhotoEditorConstants.DS_TOOL_BAR_BACKGROUND_COLOR, Color.parseColor("#FF6200EE"));
                intent.putExtra(DsPhotoEditorConstants.DS_MAIN_BACKGROUND_COLOR, Color.parseColor("#FFFFFF"));
                intent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_TOOLS_TO_HIDE, new int[]{DsPhotoEditorActivity.TOOL_WARMTH, DsPhotoEditorActivity.TOOL_PIXELATE});
                startActivityForResult(intent, 102);
            }

        }
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                Uri uri = data.getData();
                Intent intent = new Intent(HomeActivity.this, DsPhotoEditorActivity.class);
                intent.setData(uri);
                intent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY, "SY IMAGE EDITOR");
                intent.putExtra(DsPhotoEditorConstants.DS_TOOL_BAR_BACKGROUND_COLOR, Color.parseColor("#FF6200EE"));
                intent.putExtra(DsPhotoEditorConstants.DS_MAIN_BACKGROUND_COLOR, Color.parseColor("#FFFFFF"));
                intent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_TOOLS_TO_HIDE, new int[]{DsPhotoEditorActivity.TOOL_WARMTH, DsPhotoEditorActivity.TOOL_PIXELATE});
                startActivityForResult(intent, 101);
            }
        }
        if (requestCode == 101) {
            assert data != null;
            Uri uri = data.getData();
            String FilePath = getRealPathFromURI(uri);
            imageView.setImageURI(uri);

            startActivity(new Intent(getApplicationContext(), FullActivity.class).putExtra("uri", uri.toString()).putExtra("filepath", FilePath));
            Toast.makeText(getApplicationContext(), "Image Saved !", Toast.LENGTH_SHORT).show();

        }
        if (requestCode == 102) {
            assert data != null;
            Uri contentUri = data.getData();
            String FilePath = getRealPathFromURI(contentUri);
            imageView.setImageURI(contentUri);
            startActivity(new Intent(getApplicationContext(), FullActivity.class).putExtra("uri", contentUri.toString()).putExtra("filepath", FilePath));
            Toast.makeText(getApplicationContext(), "Image Saved !", Toast.LENGTH_SHORT).show();

        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        @SuppressLint("Recycle") Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }
}