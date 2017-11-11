package com.ylimielinen.projectstudentnote.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import com.ylimielinen.projectstudentnote.R;
import com.ylimielinen.projectstudentnote.db.DatabaseCreator;
import com.ylimielinen.projectstudentnote.db.async.student.GetStudent;
import com.ylimielinen.projectstudentnote.db.entity.StudentEntity;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private DatabaseCreator db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final DatabaseCreator databaseCreator = DatabaseCreator.getInstance(this.getApplication());
        databaseCreator.createDb(this.getApplication());

        db = databaseCreator;

        // get the field to be filled by the User
        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);


        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

    }

    private void attemptLogin(){
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store the values of the field when the button Sign In is pressed
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            StudentEntity student = null;
            try {
                student = new GetStudent(getApplicationContext()).execute(email).get();
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Student not found" + e.getMessage(), e);
            }
            if (student != null) {
                if (student.getPassword().equals(password)) {
                    // We need an Editor object to make preference changes.
                    // All objects are from android.context.Context
                    SharedPreferences.Editor editor = getSharedPreferences(MainActivity.PREFS_NAME, 0).edit();
                    editor.putString(MainActivity.PREFS_USER, student.getEmail());
                    editor.apply();

                    // Open the main activity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    mEmailView.setText("");
                    mPasswordView.setText("");
                } else {
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                    mPasswordView.setText("");
                }
            } else {
                mEmailView.setError(getString(R.string.error_invalid_email));
                mEmailView.requestFocus();
                mPasswordView.setText("");
            }
        }

        // Open the main activity
//        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//        startActivity(intent);
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
