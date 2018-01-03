package com.ylimielinen.projectstudentnote.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ylimielinen.projectstudentnote.R;
import com.ylimielinen.projectstudentnote.entity.StudentEntity;
import com.ylimielinen.projectstudentnote.util.Utils;

/**
 * Register fragment for a new student who connect to the app
 */
public class RegisterFragment extends Fragment {
    private static final String ARG_PARAM1 = "editMode";
    private static final String TAG = "RegisterFragment";

    private EditText etFirstName;
    private EditText etLastName;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private EditText etOldPassword;

    private Context context;

    private StudentEntity student = null;
    private boolean editMode;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference, studentReference;
    private FirebaseUser mUser;
    private String uuid;

    private ValueEventListener getStudentValueListener;

    public RegisterFragment() {
    }

    public static RegisterFragment newInstance(boolean editMode) {
        RegisterFragment fragment = new RegisterFragment();

        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM1, editMode);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get firebase database and reference
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();

        // Get current logged in student
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        if(getArguments() != null)
            editMode = getArguments().getBoolean(ARG_PARAM1);

        if(editMode){
            uuid = mUser.getUid();
            Log.d("User UID:", uuid);
            studentReference = mReference.child("students").child(uuid);
        }

        // create event listener for student
        getStudentValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get student
                if (dataSnapshot.exists()) {
                    // Get data
                    student = dataSnapshot.getValue(StudentEntity.class);

                    // Fill form
                    fillForm();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();

        // Get student if editmode
        if(editMode){
            getActivity().setTitle(R.string.modify_user);
            studentReference.addValueEventListener(getStudentValueListener);
        }else {
            getActivity().setTitle(R.string.register_user);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // remove event listener
        if(editMode)
            studentReference.removeEventListener(getStudentValueListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initForm();
        if(editMode) {
            // change button text
            ((Button)getActivity().findViewById(R.id.confirm_register_button)).setText(R.string.save);
        }
    }

    private void initForm(){
        // Get graphical elements
        etFirstName = getActivity().findViewById(R.id.firstname_register_edittext);
        etLastName = getActivity().findViewById(R.id.lastname_register_edittext);
        etEmail = getActivity().findViewById(R.id.email_register_edittext);
        etOldPassword = getActivity().findViewById(R.id.old_password_register_edittext);
        etPassword = getActivity().findViewById(R.id.password_register_edittext);
        etConfirmPassword = getActivity().findViewById(R.id.confirm_password_register_edittext);

        // if register, hide old password
        // if edit, hide email (as its primary key)
        if(!editMode)
            etOldPassword.setVisibility(View.GONE);
        else
            etEmail.setVisibility(View.GONE);

        Button btRegister = getActivity().findViewById(R.id.confirm_register_button);
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(registerUser()){
                    if(editMode)
                        updateUserName();
                    getActivity().onBackPressed();
                }
            }
        });
    }

    private void fillForm(){
        etEmail.setText(student.getEmail());
        etFirstName.setText(student.getFirstName());
        etLastName.setText(student.getLastName());
    }

    private void updateUserName(){
        TextView userName = getActivity().findViewById(R.id.userName);
        userName.setText(String.format("%s %s", student.getFirstName(), student.getLastName()));
    }

    private boolean registerUser(){
        // Reset errors
        etEmail.setError(null);
        etPassword.setError(null);
        etConfirmPassword.setError(null);
        // Store values
        String firstName = etFirstName.getText().toString();
        String lastName = etLastName.getText().toString();
        String email = etEmail.getText().toString();
        String oldPass = "";
        if(editMode)
            oldPass = etOldPassword.getText().toString();
        String pass1 = etPassword.getText().toString();
        String pass2 = etConfirmPassword.getText().toString();

        // Check for errors
        if(TextUtils.isEmpty(firstName)) {
            // First name cannot be empty
            etFirstName.setError((getString(R.string.error_field_required)));
            etFirstName.requestFocus();
            return false;
        }else if(TextUtils.isEmpty(lastName)) {
            // Last name cannot be empty
            etLastName.setError((getString(R.string.error_field_required)));
            etLastName.requestFocus();
            return false;
        }else if(TextUtils.isEmpty(email)){
            // Email cannot be empty
            etEmail.setError((getString(R.string.error_field_required)));
            etEmail.requestFocus();
            return false;
        }else if(!isEmailValid(email)){
            // email must be valid
            etEmail.setError((getString(R.string.error_field_required)));
            etEmail.requestFocus();
            return false;
        }

        // Edit user profile
        if(editMode){
            if(oldPass.isEmpty()){
                if(!pass1.isEmpty() || !pass2.isEmpty()){
                    // Old password cannot be empty
                    etOldPassword.setError((getString(R.string.error_field_required)));
                    etOldPassword.requestFocus();
                    return false;
                }else{
                    // Save updated student
                    student.setFirstName(firstName);
                    student.setLastName(lastName);
                    updateStudent();
                    return true;
                }
            }else{
                if (checkPassword(pass1, pass2)) {
                    // Save updated student
                    student.setFirstName(firstName);
                    student.setLastName(lastName);
                    student.setPassword(pass1);
                    updateStudent();
                    updatePassword();
                    return true;
                }else {
                    // Old password cannot be empty
                    etOldPassword.setError((getString(R.string.error_incorrect_password)));
                    etOldPassword.requestFocus();
                    return false;
                }
            }
        }

        // Creation of a new user
        // Check if password matches and create it
        if(checkPassword(pass1, pass2)){
            StudentEntity student = new StudentEntity();
            student.setEmail(email);
            student.setPassword(pass1);
            student.setFirstName(firstName);
            student.setLastName(lastName);

            addStudent(student);

            return true;
        }else
            return false;
    }

    private void updateStudent() {
        // Change student values
        studentReference.child("firstName").setValue(student.getFirstName());
        studentReference.child("lastName").setValue(student.getLastName());
    }

    private void updatePassword(){
        mUser.updatePassword(student.getPassword().trim())
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(isAdded()){
                            if (task.isSuccessful()) {
                                Toast.makeText(context, getString(R.string.password_update_completion), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, getString(R.string.password_update_failure), Toast.LENGTH_SHORT).show();
                            }
                        }

                        Utils.hideKeyboard(context);
                    }
                });
    }

    private boolean checkPassword(String pass1, String pass2){
        // Password length
        if(pass1.length() < 6){
            etPassword.setError(getString(R.string.error_password_too_short));
            etPassword.requestFocus();
            etPassword.setText("");
            etConfirmPassword.setText("");
            return false;
        }

        if(TextUtils.isEmpty(pass1) || TextUtils.isEmpty(pass2)){
            // Passwords cannot be empty
            etPassword.setError((getString(R.string.error_field_required)));
            etPassword.requestFocus();
            return false;
        }

        if(!pass1.equals(pass2)){
            // Check if pass 1 and 2 match
            etPassword.setError(getString(R.string.error_password_mismatch));
            etPassword.requestFocus();
            etPassword.setText("");
            etConfirmPassword.setText("");
            return false;
        }

        return true;
    }

    private void addStudent(final StudentEntity student){
        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(student.getEmail(), student.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "Student created");
                            addStudentInFirebase(student);
                        }else{
                            // TODO: show errors
                            FirebaseAuthException e = (FirebaseAuthException )task.getException();
                            Log.d(TAG, "Student not created " + e.getMessage());
                        }
                    }
                });
    }

    private void addStudentInFirebase(StudentEntity student){
        mDatabase.getReference("students")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(student, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if(databaseError != null){
                            //TODO: show errors

                            FirebaseAuth.getInstance().getCurrentUser().delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                                Log.d(TAG, "ROLLBACK: Student deleted");
                                            else
                                                Log.d(TAG, "ROLLBACK: failure", task.getException());
                                        }
                                    });
                        }else{
                            //TODO: show errors
                        }
                    }
                });
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void getStudent() {
        studentReference.addValueEventListener(getStudentValueListener);
    }
}