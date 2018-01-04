package com.ylimielinen.projectstudentnote.ui.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ylimielinen.projectstudentnote.R;
import com.ylimielinen.projectstudentnote.entity.StudentEntity;
import com.ylimielinen.projectstudentnote.ui.activity.LoginActivity;
import com.ylimielinen.projectstudentnote.ui.activity.MainActivity;
import com.ylimielinen.projectstudentnote.util.Utils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

/**
 * Login fragment to display the field nessecary to log in
 */
public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";
    private AutoCompleteTextView mEmailView;

    private FragmentManager fragmentManager;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    private EditText mPasswordView;
    private ProgressDialog progressDialog;
    private Context context;
    private Activity activity;

    public LoginFragment() {
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().setTitle(R.string.action_sign_in);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        activity = (Activity)context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the application context
//        context = ((LoginActivity)container).getContext();

        // get firebase authentication manager
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        // Create the fragment view
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // get the field to be filled by the User
        mEmailView = view.findViewById(R.id.email);
        mPasswordView = view.findViewById(R.id.password);

        Button btEmailSignIn = view.findViewById(R.id.email_sign_in_button);
        btEmailSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.hideKeyboard(context);
                attemptLogin();
             }
        });

        TextView btRegister = view.findViewById(R.id.register_button);
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = RegisterFragment.newInstance(false);
                fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContentLogin, fragment).addToBackStack(null).commit();
            }
        });

        return view;
    }

    private void attemptLogin(){
        // Reset errors
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
        }else if(TextUtils.isEmpty(password)){
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
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
            // Dialog for login
            progressDialog = new ProgressDialog(activity,
                    R.style.Theme_AppCompat_Light_Dialog);
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.action_sign_in));
            progressDialog.show();


            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        mDatabase.getReference("students")
                                .child(mAuth.getCurrentUser().getUid())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()){
                                            // Open the main activity and close this activity
                                            Intent intent = new Intent(getActivity(), MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            getActivity().finish();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // manage failures inspired by http://www.techotopia.com/index.php/Handling_Firebase_Authentication_Errors_and_Failures#FirebaseAuth_Exception_Types
                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        mPasswordView.setError(getString(R.string.error_incorrect_password));
                        mPasswordView.setText("");
                        mPasswordView.requestFocus();
                    } else if (e instanceof FirebaseAuthInvalidUserException) {
                        mEmailView.setError(getString(R.string.error_invalid_email));
                        mPasswordView.setText("");
                        mEmailView.requestFocus();
                    }
                }
            });

            progressDialog.dismiss();
        }
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}