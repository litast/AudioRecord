package lv.lita.stauvere.audiorecord;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;
import java.io.File;

public class MainActivity extends AppCompatActivity {

    //deklare mainigos
    Button btnRecord, btnStopRecord, btnPlay, btnStoPlay;
    String pathSave = "";
    MediaRecorder mediaRecorder = new MediaRecorder();
    MediaPlayer mediaPlayer = new MediaPlayer();

    final int REQUEST_PERMISSION_CODE = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //request runtime permissions
        if (!checkPermissionFromDevice())
            requestPermissions();

        //inicialize skatu
        btnPlay = findViewById(R.id.btnPlay);
        btnStoPlay = findViewById(R.id.btnStopPlay);
        btnRecord = findViewById(R.id.btnRecord);
        btnStopRecord = findViewById(R.id.btnStopRecord);


        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkPermissionFromDevice()) {
                    pathSave = "/Audio/litarec_" + System.currentTimeMillis() + ".m4a";
                    //pathSave = "/sdcard/Music/litarec_" + System.currentTimeMillis() + ".m4a";
                    Toast.makeText(MainActivity.this, "Path: " + pathSave, Toast.LENGTH_SHORT).show();
                    String state = android.os.Environment.getExternalStorageState();

                    if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
                        Toast.makeText(MainActivity.this, "SD Card is not mounted " + state, Toast.LENGTH_SHORT).show();
                    }

                    setupMediaRecorder();
                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    btnStopRecord.setEnabled(true);
                    btnPlay.setEnabled(false);
                    btnStoPlay.setEnabled(false);

                    Toast.makeText(MainActivity.this, "Recording...", Toast.LENGTH_SHORT).show();
                } else {
                    requestPermissions();
                }
            }
        });


         btnStopRecord.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 try {
                     mediaRecorder.stop();
                 } catch (Exception e) { e.printStackTrace(); }
                 btnStopRecord.setEnabled(false);
                 btnPlay.setEnabled(true);
                 btnRecord.setEnabled(true);
                 btnStoPlay.setEnabled(false);
             }
         });


         btnPlay.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 btnStoPlay.setEnabled(true);
                 btnStopRecord.setEnabled(false);
                 btnRecord.setEnabled(false);

                 mediaPlayer = new MediaPlayer();
                 try {
                     mediaPlayer.setDataSource(pathSave);
                 } catch (Exception e) { e.printStackTrace(); }
                 try {
                     mediaPlayer.prepare();
                 } catch (Exception e) { e.printStackTrace(); }
                 mediaPlayer.start();
                 Toast.makeText(MainActivity.this, "Playing...", Toast.LENGTH_SHORT).show();
             }
         });


         btnStoPlay.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 btnStopRecord.setEnabled(false);
                 btnRecord.setEnabled(true);
                 btnStoPlay.setEnabled(false);
                 btnPlay.setEnabled(true);

                 if (mediaPlayer != null) {
                     mediaPlayer.stop();
                     mediaPlayer.release();
                     setupMediaRecorder();
                 }
             }
         });


    }


    private void setupMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(pathSave);
    }


    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        },REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
        }

    }

    private boolean checkPermissionFromDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED && record_audio_result == PackageManager.PERMISSION_GRANTED;

    }

}
