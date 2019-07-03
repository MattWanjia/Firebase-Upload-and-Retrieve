package com.example.firebasestorage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String UPLOAD = "UPLOAD";

    private ImageView imageView;
    private EditText nameInput, countryInput;
    private Button uploadButton, showUploadsButton;
    private ProgressBar progressBar;

    private Uri imageUri;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.picImageView);
        imageView.setOnClickListener(this);
        nameInput = findViewById(R.id.nameET);
        countryInput = findViewById(R.id.countryET);
        uploadButton = findViewById(R.id.uploadBtn);
        uploadButton.setOnClickListener(this);
        showUploadsButton = findViewById(R.id.showUploadsBtn);
        showUploadsButton.setOnClickListener(this);
        progressBar = findViewById(R.id.progressBar);

        storageReference = FirebaseStorage.getInstance().getReference().child(UPLOAD);
        databaseReference = FirebaseDatabase.getInstance().getReference().child(UPLOAD);
    }

    @Override
    public void onClick(View view) {
        if (view == imageView){
            openFileChooser();
        }
        if (view == uploadButton){
            uploadFile();
        }
        if (view == showUploadsButton){
            openImagesActivity();
        }
    }

    public void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && data.getData() != null){
            imageUri = data.getData();

            //Picasso.with(this).load(imageUri).into(imageView);
            //imageView.setImageURI(imageUri);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void uploadFile(){
        if (imageUri != null){
            final StorageReference fileRefence = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            final String urll = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri)).getDownloadUrl().toString();
            fileRefence.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(0);
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            }, 5000);

                            fileRefence.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String fileName = nameInput.getText().toString().trim();
                                    String fileCountry = countryInput.getText().toString().trim();

                                    Upload upload2 = new Upload(uri.toString(), fileName, fileCountry);
                                    String uniqueId = databaseReference.push().getKey();
                                    databaseReference.child(uniqueId).setValue(upload2);
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            progressBar.setProgress((int) progress);
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    });
        }else {
            Toast.makeText(getApplicationContext(), "SELECT FILE", Toast.LENGTH_SHORT).show();
        }
    }

    public void openImagesActivity(){
        startActivity(new Intent(getApplicationContext(), ImageActivity.class));
    }
}
