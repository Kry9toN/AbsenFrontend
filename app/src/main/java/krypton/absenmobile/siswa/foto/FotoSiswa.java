package krypton.absenmobile.siswa.foto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;

import java.util.List;

import krypton.absenmobile.R;
import krypton.absenmobile.siswa.dashboard.DashboardSiswa;
import krypton.absenmobile.util.security.Permission;

public class FotoSiswa extends AppCompatActivity {
    private static final String TAG = "FotoSiswa";

    private final int CODE_CAMERA_REQ = 101;
    private ImageView imageView;
    private CardView cardView;
    private Button btnSend;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_foto_activity);

        getSupportActionBar().setElevation(0);
        Permission.checkCamera(this);

        // Initial btn and imgview
        imageView = findViewById(R.id.cameralocation);
        btnSend = findViewById(R.id.send);
        cardView = findViewById(R.id.card_button_send);
        TextView textHeader = findViewById(R.id.text_header_foto);

        String jam = DashboardSiswa.getJam();

        if (jam.equals("datang")) {
            textHeader.setText(R.string.dialog_header_foto_datang);
        }
        if (jam.equals("pulang")) {
            textHeader.setText(R.string.dialog_header_foto_pulang);
        }

        imageView.setOnClickListener(v -> {
            Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(camera, CODE_CAMERA_REQ);
        });
        cardView.setVisibility(View.INVISIBLE);
        btnSend.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_CAMERA_REQ) {
            // Get image from camera
            Bitmap imageCapture = (Bitmap) data.getExtras().get("data");

            // Initial AI for detection Face
            Log.d(TAG, "Initial AI face detection");
            InputImage image = InputImage.fromBitmap(imageCapture, 0);
            FaceDetector detector = FaceDetection.getClient();

            // Run detection Face
            Log.d(TAG, "Running face detection");
            Task<List<Face>> result =
                    detector.process(image)
                            .addOnSuccessListener(
                                    faces -> {
                                        // Task completed successfully
                                        // Check if face detected
                                        if (!faces.isEmpty()) {
                                            imageView.setImageBitmap(imageCapture);

                                            btnSend.setVisibility(View.VISIBLE);
                                            cardView.setVisibility(View.VISIBLE);
                                            // In btn clicked
                                            btnSend.setOnClickListener(v -> {
                                                Toast.makeText(FotoSiswa.this, "Absen berhasil.....", Toast.LENGTH_SHORT).show();
                                                finish();
                                            });
                                        } else {
                                            Toast.makeText(FotoSiswa.this, "Wajah tidak terdeteksi", Toast.LENGTH_SHORT).show();
                                            // Hide send btn
                                            btnSend.setVisibility(View.INVISIBLE);
                                            cardView.setVisibility(View.INVISIBLE);
                                        }
                                    })
                            .addOnFailureListener(
                                    e -> {
                                        // Task failed with an exception
                                        Log.d(TAG, "Task Image Detector ERROR");
                                    });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Permission.checkCamera(this);
    }
}
