package com.example.echoofyou02;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddCapsuleActivity extends AppCompatActivity {

    private EditText capsuleTitle, capsuleDescription, addUsers;
    private TextView chooseTime;
    private ImageButton addCapsuleButton, backButton, creatButton;
    private Uri fileUri;
    private FirebaseFirestore firestore;
    private Calendar selectedDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_capsule);

        // Initialize views
        capsuleTitle = findViewById(R.id.capsuleTitle);
        capsuleDescription = findViewById(R.id.capsuleDescription);
        addUsers = findViewById(R.id.addUsers);
        chooseTime = findViewById(R.id.chooseTime);
        addCapsuleButton = findViewById(R.id.addCapsuleButton);
        backButton = findViewById(R.id.backButton);
        creatButton = findViewById(R.id.creatButton);

        // Initialize Firestore instance
        firestore = FirebaseFirestore.getInstance();

        // Initialize calendar instance
        selectedDateTime = Calendar.getInstance();

        // Set up listeners
        backButton.setOnClickListener(view -> finish());
        addCapsuleButton.setOnClickListener(view -> selectFile());
        chooseTime.setOnClickListener(view -> pickDateTime());
        creatButton.setOnClickListener(view -> createCapsule());
    }

    private void selectFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            fileUri = data.getData();
            Toast.makeText(this, "File selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void pickDateTime() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            selectedDateTime.set(Calendar.YEAR, year);
            selectedDateTime.set(Calendar.MONTH, month);
            selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (timeView, hourOfDay, minute) -> {
                selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectedDateTime.set(Calendar.MINUTE, minute);
                chooseTime.setText(selectedDateTime.getTime().toString());
            }, selectedDateTime.get(Calendar.HOUR_OF_DAY), selectedDateTime.get(Calendar.MINUTE), true);

            timePickerDialog.show();
        }, selectedDateTime.get(Calendar.YEAR), selectedDateTime.get(Calendar.MONTH), selectedDateTime.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void createCapsule() {
        String title = capsuleTitle.getText().toString().trim();
        String description = capsuleDescription.getText().toString().trim();
        String users = addUsers.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(users) || fileUri == null) {
            Toast.makeText(this, "Please fill in all fields and select a file", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Check if file is an image and encode as Base64 if needed
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), fileUri);
            String base64Image = encodeImageToBase64(bitmap);

            // Prepare capsule data
            Map<String, Object> capsule = new HashMap<>();
            capsule.put("title", title);
            capsule.put("description", description);
            capsule.put("users", users);
            capsule.put("fileBase64", base64Image); // Save Base64 string
            capsule.put("dateTime", selectedDateTime.getTime());

            // Log capsule data for debugging
            Log.d("CapsuleData", "Title: " + title + ", Description: " + description + ", Users: " + users + ", DateTime: " + selectedDateTime.getTime());

            // Save to Firestore
            firestore.collection("capsules").add(capsule).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Capsule created successfully", Toast.LENGTH_SHORT).show();
                    // Schedule notification if permission is granted
                    checkNotificationPermissionAndNotify(title);
                    // Navigate to DisplayCapsuleActivity after successful creation
                    Intent intent = new Intent(AddCapsuleActivity.this, DisplayCapsuleActivity.class);
                    startActivity(intent);
                    finish(); // Close the AddCapsuleActivity
                } else {
                    String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                    Toast.makeText(this, "Failed to create capsule: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error processing file", Toast.LENGTH_SHORT).show();
        }
    }

     // encoding the image
    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream); // Compress image
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT); // Convert to Base64
    }

    // Function to check if the app has permission to send notifications (for Android 13+)
    private void checkNotificationPermissionAndNotify(String title) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                sendNotification(title); // Permission granted, send notification
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        } else {
            sendNotification(title); // No permission needed for lower versions
        }
    }

    // Function to send notification after permission is granted
    private void sendNotification(String title) {
        // Create notification channel for Android 8.0 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "default",  // Channel ID
                    "Default",  // Channel name
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Build the notification using NotificationCompat
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Capsule Available")
                .setContentText("Your capsule is now ready!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Get NotificationManager and send the notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(0, builder.build());
        }
    }

    // Handle result of notification permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            sendNotification(capsuleTitle.getText().toString()); // Permission granted, send notification
        } else {
            Toast.makeText(this, "Permission to send notifications is required", Toast.LENGTH_SHORT).show();
        }
    }

}
