package com.goudarziha.bigpicture;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class Signup extends Activity {

    EditText etUsername, etPassword, etEmail;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        etUsername = (EditText) findViewById(R.id.usernameSignup);
        etPassword = (EditText) findViewById(R.id.passwordSignup);
        etEmail = (EditText) findViewById(R.id.emailSignup);
        submit = (Button) findViewById(R.id.btnSignUp);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                String email = etEmail.getText().toString();

                if(username.equals("") && password.equals("") && email.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please complete sign up form", Toast.LENGTH_LONG).show();
                } else {
                    ParseUser user = new ParseUser();
                    user.setUsername(username);
                    user.setPassword(password);
                    user.setEmail(email);
                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(getApplicationContext(), "Successfully signed up!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), LoginSignupActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), "Sign up error", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
