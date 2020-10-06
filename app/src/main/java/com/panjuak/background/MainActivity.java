package com.panjuak.background;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.Calendar;

import me.itangqi.waveloadingview.WaveLoadingView;

public class MainActivity extends AppCompatActivity {
    private PendingIntent pendingIntent;
    int kranStatus = 1;
    String kranText = "Buka Kran";
    TextView txtTinggi;
    TextView txtVolume;
    ImageView imgAlert;
    TextView txtTitleAlert;
    TextView txtSubTitleAlert;
    Button keluar, tombolKran;
    private static final int ALARM_REQUEST_CODE = 134;
    private int interval_seconds = 10;
    private int NOTIFICATION_ID = 1;
    WaveLoadingView mWaveLoadingView;
    DatabaseReference refMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startAlarmManager();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("tinggi");
        refMode = database.getReference("mode");
        mWaveLoadingView = findViewById(R.id.waveLoadingView);
        txtTinggi = findViewById(R.id.txt_tinggi);
        txtVolume = findViewById(R.id.txt_volume);
        imgAlert = findViewById(R.id.img_alert);
        txtTitleAlert = findViewById(R.id.title_alert);
        txtSubTitleAlert = findViewById(R.id.subtitle_alert);
        keluar = findViewById(R.id.tombolkeluar);
        tombolKran = findViewById(R.id.tombolKran);

        keluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });
        tombolKran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeValue();
            }
        });
        mWaveLoadingView.setShapeType(WaveLoadingView.ShapeType.RECTANGLE);
        mWaveLoadingView.setProgressValue(30);
        mWaveLoadingView.setAmplitudeRatio(20);
        mWaveLoadingView.setTopTitleStrokeWidth(3);
        mWaveLoadingView.setAnimDuration(3000);
        mWaveLoadingView.pauseAnimation();
        mWaveLoadingView.resumeAnimation();
        mWaveLoadingView.cancelAnimation();
        mWaveLoadingView.startAnimation();

        refMode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                kranStatus = Integer.parseInt(snapshot.getValue().toString());
                kranText  = (kranStatus == 2)? "Buka kran" : "Tutup kran";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("error : ", "gagal");
                int tinggi = Integer.parseInt(snapshot.getValue().toString());
                int persen = tinggi * 100/20;
                txtTinggi.setText(tinggi+" cm");

                double phi = 3.14;
                int jariJari = 14;
                double volume = phi * jariJari * jariJari * tinggi/100;
                DecimalFormat df = new DecimalFormat("#.##");
                mWaveLoadingView.setProgressValue((int)persen);
                txtVolume.setText(df.format(volume)+" L");
                if(tinggi >= 17){
                    imgAlert.setImageDrawable(getDrawable(R.drawable.ic_baseline_arrow_upward_24));
                    txtTitleAlert.setText("Air hampir penuh.");
                    txtSubTitleAlert.setText("Segera untuk mematikan air");
                    tombolKran.setText("Buka Kran");
                }else if(tinggi <= 3){
                    imgAlert.setImageDrawable(getDrawable(R.drawable.ic_baseline_arrow_downward_24));
                    txtTitleAlert.setText("Air hampir habis.");
                    txtSubTitleAlert.setText("Segera untuk menghidupkan air");
                    tombolKran.setText("Tutup Kran");
                }else{
                    imgAlert.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_24));
                    txtTitleAlert.setText("Air dalam keadaan normal.");
                    txtSubTitleAlert.setText("Tidak perlu ada tindakan.");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Intent alarmIntent = new Intent(MainActivity.this, AppReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, ALARM_REQUEST_CODE, alarmIntent, 0);
        startAlarmManager();
    }
    public void startAlarmManager() {
        //set waktu sekarang berdasarkan interval
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, interval_seconds);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //set alarm manager dengan memasukkan waktu yang telah dikonversi menjadi milliseconds
        manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
    }
    //Stop/Cancel alarm manager
    public void stopAlarmManager(View v) {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        //close existing/current notifications
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
        //jika app ini mempunyai banyak notifikasi bisa di cancelAll()
        //notificationManager.cancelAll();
        Toast.makeText(this, "AlarmManager Stopped by User.", Toast.LENGTH_SHORT).show();
    }
    public void changeValue(){
        if(kranStatus == 2){
            refMode.setValue(1);
        }else{
            refMode.setValue(2);
        }
        tombolKran.setText(kranText);
    }
}