package com.example.registration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SplashScreen extends AppCompatActivity {
    public static int SPLASH_TIMEOUT = 3000;
    static ExecutorService executorService;
    boolean entryFound;
    GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();
        try (InputStream inputStream = getApplicationContext().openFileInput("gharseva.txt");) {
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                if(bufferedReader!=null) {
                    String email = bufferedReader.readLine().split(" : ")[1].trim();
                    String password = bufferedReader.readLine().split(" : ")[1].trim();
                    check(email, password);
                }
            }
        }
        catch (IOException e) {
            new Handler().postDelayed(new Runnable() {
                @Override public void run() {
                    Intent i = new Intent(SplashScreen.this, MainActivity.class); startActivity(i);
                    finish(); } }, 3000);
        }
    }

    public void check(String email,String password){
        //check this entry
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(SplashScreen.this, gso);
        if(checkLogIn(email,password)){
            Toast.makeText(SplashScreen.this,"Welcome back "+email+"!",Toast.LENGTH_LONG).show();
            startNextActivity(email,password);
        }else{
            Toast.makeText(SplashScreen.this,"Invalid Credentials!",Toast.LENGTH_LONG).show();
        }
    }


    private boolean checkLogIn(String email,String password) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        Thread thread =new Thread(new Runnable() {
            public void run() {
                entryFound=false;
                executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
                Future<String> futureDataset = executorService.submit(new SignInThread(email, password));
                try {
                    String jsonData = futureDataset.get();
                    if(jsonData.contains("true")){
                        entryFound=true;
                    }

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return entryFound;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(SplashScreen.this);
            if (acct != null) {
                String personlEmail = acct.getEmail();
                String id = acct.getId();
                if(checkLogIn(personlEmail,id)){
                    startNextActivity(personlEmail,personlEmail);
                }
            }
        } catch (ApiException e) {
        }
    }

    private void startNextActivity(String personalEmail,String password){
        try(OutputStreamWriter outputStreamWriter = new OutputStreamWriter(SplashScreen.this.openFileOutput("gharseva.txt", Context.MODE_PRIVATE));){
            outputStreamWriter.write("Email : "+personalEmail);
            outputStreamWriter.write("\nPassword : "+password);
        } catch (IOException e) {
        }
        Intent intent = new Intent(SplashScreen.this,ServicesOfferedActivity.class);
        intent.putExtra("email",personalEmail);
        startActivity(intent);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1);
    }

}
