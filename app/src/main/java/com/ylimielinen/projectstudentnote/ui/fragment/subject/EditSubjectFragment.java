package com.ylimielinen.projectstudentnote.ui.fragment.subject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ylimielinen.projectstudentnote.R;
import com.ylimielinen.projectstudentnote.entity.StudentEntity;
import com.ylimielinen.projectstudentnote.entity.SubjectEntity;
import com.ylimielinen.projectstudentnote.ui.activity.MainActivity;
import com.ylimielinen.projectstudentnote.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditSubjectFragment extends Fragment {
    private final String TAG = "EditMarkFragment";
    private static final String ARG_PARAM1 = "subjectUuid";

    private String studentUuid;
    private boolean editMode;
    private String subjectUuid;

    private SubjectEntity subject;
    private EditText etName;
    private EditText etDescription;
    private Toast mToast;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference, subjectReference, studentSubjectsReference, markReference, subjectMarksReference;
    private FirebaseUser mUser;
    private Context context;

    public EditSubjectFragment() {
    }

    public static EditSubjectFragment newInstance(SubjectEntity subject) {
        EditSubjectFragment fragment = new EditSubjectFragment();
        Bundle args = new Bundle();
        if(subject != null)
            args.putString(ARG_PARAM1, subject.getUid());
        else
            args.putString(ARG_PARAM1, "-1");
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Define there is an option menu
        setHasOptionsMenu(true);

        if(getArguments() != null){
            subjectUuid = getArguments().getString(ARG_PARAM1);

            if(subjectUuid.equals("-1")){
                ((MainActivity) getActivity()).setTitle(getString(R.string.title_fragment_subject_create));
                mToast = Toast.makeText(getContext(), getString(R.string.subject_created), Toast.LENGTH_LONG);
                editMode = false;
            }else{
                ((MainActivity) getActivity()).setTitle(getString(R.string.title_fragment_subject_edit));
                mToast = Toast.makeText(getContext(), getString(R.string.subject_updated), Toast.LENGTH_LONG);
                editMode = true;
            }
        }

        // Get current logged in studentUuid
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        studentUuid = mUser.getUid();

        // get firebase database and reference
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();
        subjectReference = mReference.child("subjects");
        studentSubjectsReference = mReference.child("studentSubjects").child(studentUuid);
        markReference = mReference.child("marks");
        subjectMarksReference = mReference.child("subjectMarks").child(subjectUuid);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the application context
        context = container.getContext();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_subject, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initForm();
        if(editMode)
            getSubject();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(editMode)
            inflater.inflate(R.menu.delete_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteButton:
                performDeleteWithConfirmation();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void fillForm() {
        // Fill the form with existing values
        etName.setText(subject.getName());
        etDescription.setText(subject.getDescription());
    }

    private void initForm() {
        // Get GUI elements for the form
        etName = (EditText) getActivity().findViewById(R.id.subjectNameEditText);
        etDescription = (EditText) getActivity().findViewById(R.id.subjectDescriptionEditText);
        final Button saveBtn = (Button) getActivity().findViewById(R.id.saveSubject);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(saveChanges(etName.getText().toString(), etDescription.getText().toString())) {
                    Utils.hideKeyboard(context);
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });
    }

    private void performDeleteWithConfirmation(){
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle(getString(R.string.delete));
        alertDialog.setCancelable(false);
        alertDialog.setMessage(getString(R.string.dialog_delete_subject));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // remove marks linked to the subject and links
                ValueEventListener getMarks = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot element : dataSnapshot.getChildren()) {
                            markReference.child(element.getKey()).removeValue();
                        }

                        subjectMarksReference.removeValue();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };

                subjectMarksReference.addListenerForSingleValueEvent(getMarks);

                // remove subject and links with student
                studentSubjectsReference.child(subjectUuid).removeValue();//!!!
                subjectReference.child(subjectUuid).removeValue();

                // go back to subjects list
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
        return;
    }

    private boolean saveChanges(String name, String description){
        if(description.equals("")) {
            etDescription.setError(getString(R.string.error_value_empty));
            etDescription.requestFocus();
            return false;
        }

        if(name.equals("")) {
            etName.setError(getString(R.string.error_name_empty));
            etName.requestFocus();
            return false;
        }

        if(editMode){
            subject.setName(name);
            subject.setDescription(description);
            updateSubject(subject);
        }else{
            SubjectEntity newSubject = new SubjectEntity();
            newSubject.setName(name);
            newSubject.setDescription(description);
            newSubject.setStudent(studentUuid);
            addSubject(newSubject);
        }

        return true;
    }

    public void getSubject() {
        subjectReference.child(subjectUuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    // Get data
                    subject = dataSnapshot.getValue(SubjectEntity.class);
                    subject.setStudent(studentUuid);
                    subject.setUid(dataSnapshot.getKey());

                    // Fill form
                    fillForm();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addSubject(final SubjectEntity subject){
        subject.setUid(subjectReference.push().getKey());
        subjectReference.child(subject.getUid()).setValue(subject.toMap(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(final DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.d(TAG, "Insert failure!", databaseError.toException());
                } else {
                    studentSubjectsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Map<String, Object> studentSubject = new HashMap<>();

                            for (DataSnapshot element : dataSnapshot.getChildren()) {
                                studentSubject.put(element.getKey(), element.getValue().toString());
                            }

                            studentSubject.put(subject.getUid(), "true");
                            studentSubjectsReference.updateChildren(studentSubject);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    Log.d(TAG, "Insert successful!");
                    mToast.show();
                }
            }
        });


    }

    private void updateSubject(SubjectEntity subject){
        subjectReference.child(subject.getUid()).updateChildren(subject.toMap(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.d(TAG, "Update failure!", databaseError.toException());
                } else {
                    Log.d(TAG, "Update successful!");
                    mToast.show();
                }
            }
        });
    }
}