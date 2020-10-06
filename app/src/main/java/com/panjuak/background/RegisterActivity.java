package com.panjuak.background;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText username, email, password, ulangPassword;
    Button daftar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        ulangPassword = findViewById(R.id.password);
        daftar = findViewById(R.id.daftar);
        daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validasi();
            }
        });

    }
    void validasi(){
        if(username.getText().equals("") || email.getText().equals("") || password.getText().equals("") || ulangPassword.getText().equals("")){
            Toast.makeText(this, "Data harus diisi semua", Toast.LENGTH_SHORT).show();
        }else{
            if(!password.getText().toString().equals(ulangPassword.getText().toString())){
                //Toast.makeText(this, password.getText()+" "+ulangPassword.getText(), Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Password harus sama", Toast.LENGTH_SHORT).show();
            }else{
                register();
            }
        }
    }

    void register(){
        mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this, "Pendaftaran berhasil", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                }
            }
        });
    }
}