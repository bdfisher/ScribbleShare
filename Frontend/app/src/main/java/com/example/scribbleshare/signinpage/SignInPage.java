package com.example.scribbleshare.signinpage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.scribbleshare.SplashScreen;
import com.example.scribbleshare.R;

/**
 * Handles the xml and button actions
 */
public class SignInPage extends AppCompatActivity implements SignInView {


    private SignInPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        presenter = new SignInPresenter(this, getApplicationContext());

        ImageButton back_button = (ImageButton) findViewById(R.id.back_button_sign_in);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), SplashScreen.class));
            }
        });

        Button sign_in_button = (Button) findViewById(R.id.sign_in_button);
        sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usernameText = ((EditText) findViewById(R.id.si_username)).getText().toString();
                String passwordText = ((EditText)findViewById(R.id.si_password_text)).getText().toString();
                if(usernameText.equals("") || passwordText.equals("")){
                    //dont bother with empty credentials
                    return;
                }
                presenter.signInRequest(usernameText, passwordText);
            }
        });

    }

    @Override
    public void makeToast(String message) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }

    @Override
    public void switchView(Class c) {
        startActivity(new Intent(this, c));
    }
}