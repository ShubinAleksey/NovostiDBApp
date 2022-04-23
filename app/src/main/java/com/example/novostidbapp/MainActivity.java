package com.example.novostidbapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    EditText login,password;
    Button avtoriz,registr,btnScan;
    DataBaseHelperUsers databaseHelper;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        databaseHelper = new DataBaseHelperUsers(this);
        avtoriz = findViewById(R.id.button);
        registr = findViewById(R.id.button2);
        login = findViewById(R.id.login);
        password = findViewById(R.id.password);
        btnScan = findViewById(R.id.btnScan);
        executor = ContextCompat.getMainExecutor(this);
        avtoriz.setOnClickListener(view ->{
                    Cursor res = databaseHelper.getdataUsers();
                    if(res.getCount() == 0)
                    {
                        Toast.makeText(getApplicationContext(),"Нет пользователей",Toast.LENGTH_LONG).show();
                        return;
                    }
                    while (res.moveToNext()){
                        //начинается отсчёт с 0
                        String loginIn = login.getText().toString();
                        String loginOut = res.getString(1);
                        String paswIn = password.getText().toString();
                        String pasOut = res.getString(2);
                        if(loginIn.equals(loginOut))
                        {
                            if(paswIn.equals(pasOut))
                            {
                                String role = res.getString(3).toString();
                                if(role.equals("1"))
                                {
                                    Intent intent = new Intent(this,Admin.class);
                                    startActivity(intent);
                                }
                                else
                                {
                                    Intent intent = new Intent(this,User.class);
                                    startActivity(intent);
                                }
                            }
                            else
                                Toast.makeText(getApplicationContext(),"Не верный пароль",Toast.LENGTH_LONG).show();
                        }
                        else
                            Toast.makeText(getApplicationContext(),"Такого пользователя нет",Toast.LENGTH_LONG).show();
                    }
                });
        registr.setOnClickListener(view ->{
                    Intent back = new Intent(this,Registration.class);
                    startActivity(back);
                }
        );
        biometricPrompt = new BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Log.e("ErrorAUTH", errString.toString());
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(MainActivity.this, "Успешно вход в админа!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this,Admin.class);
                startActivity(intent);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Log.e("FailedAUTH", "Fail!");
            }
        });
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Авторизация")
                .setSubtitle("Прислонить палец")
                .setNegativeButtonText("Отмена")
                .build();


        btnScan.setOnClickListener(view -> {
            biometricPrompt.authenticate(promptInfo);
        });
    }
}