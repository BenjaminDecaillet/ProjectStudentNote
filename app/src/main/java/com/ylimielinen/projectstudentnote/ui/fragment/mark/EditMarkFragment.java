package com.ylimielinen.projectstudentnote.ui.fragment.mark;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ylimielinen.projectstudentnote.R;
import com.ylimielinen.projectstudentnote.entity.MarkEntity;
import com.ylimielinen.projectstudentnote.ui.activity.MainActivity;
import com.ylimielinen.projectstudentnote.util.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditMarkFragment extends Fragment {
    private final String TAG = "EditMarkFragment";
    private static final String ARG_PARAM1 = "markUuid";
    private static final String ARG_PARAM2 = "subjectUuid";

    private String studentUuid;
    private boolean editMode;
    private String markUuid;
    private String subjectUuid;

    private MarkEntity mark;
    private EditText etName;
    private EditText etValue;
    private EditText etWeighting;
    private Toast mToast;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference, markReference, subjectMarksReference;
    private Context context;

    public EditMarkFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param mark mark.
     * @return A new instance of fragment EditMarkFragment.
     */
    public static EditMarkFragment newInstance(MarkEntity mark) {
        EditMarkFragment fragment = new EditMarkFragment();
        Bundle args = new Bundle();
        if(mark != null) {
            args.putString(ARG_PARAM1, mark.getUid());
            args.putString(ARG_PARAM2, mark.getSubject());
        }
        else {
            args.putString(ARG_PARAM1, "-1");
            args.putString(ARG_PARAM2, "-1");
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Define there is an option menu
        setHasOptionsMenu(true);

        if(getArguments() != null){
            markUuid = getArguments().getString(ARG_PARAM1);
            subjectUuid = getArguments().getString(ARG_PARAM2);

            if(markUuid.equals("-1")){
                ((MainActivity) getActivity()).setTitle(getString(R.string.title_fragment_mark_create));
                mToast = Toast.makeText(getContext(), getString(R.string.mark_created), Toast.LENGTH_LONG);
                editMode = false;
            }else{
                ((MainActivity) getActivity()).setTitle(getString(R.string.title_fragment_mark_edit));
                mToast = Toast.makeText(getContext(), getString(R.string.mark_updated), Toast.LENGTH_LONG);
                editMode = true;
            }
        }

        // get firebase database and reference
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();
        markReference = mReference.child("marks");
        subjectMarksReference = mReference.child("subjectMarks").child(subjectUuid);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the application context
        context = container.getContext();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_mark, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initForm();
        if(editMode)
            getMark();
    }

    private void getMark() {
        markReference.child(markUuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    // Get data
                    mark = dataSnapshot.getValue(MarkEntity.class);
                    mark.setUid(dataSnapshot.getKey());

                    // fill form
                    fillForm();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addMark(final MarkEntity mark){
        mark.setUid(markReference.push().getKey());
        markReference.child(mark.getUid()).setValue(mark.toMap(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.d(TAG, "Insert failure!", databaseError.toException());
                } else {
                    subjectMarksReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Map<String, Object> subjectMark = new HashMap<>();

                            for (DataSnapshot element : dataSnapshot.getChildren()) {
                                subjectMark.put(element.getKey(), element.getValue().toString());
                            }

                            subjectMark.put(mark.getUid(), "true");
                            subjectMarksReference.updateChildren(subjectMark);
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

    private void updateMark(MarkEntity mark){
        markReference.child(mark.getUid()).updateChildren(mark.toMap(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError != null)
                    Log.d(TAG, "Update failure!", databaseError.toException());
                else{
                    Log.d(TAG, "Update successful!");
                    mToast.show();
                }
            }
        });
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
        etName.setText(mark.getName());
        etValue.setText(mark.getValue().toString());
        etWeighting.setText(mark.getWeighting().toString());
    }

    private void initForm() {
        // Get GUI elements for the form
        etName = (EditText) getActivity().findViewById(R.id.markNameEditText);
        etValue = (EditText) getActivity().findViewById(R.id.markValueEditText);
        etWeighting = (EditText) getActivity().findViewById(R.id.markWeightingEditText);
        final Button saveBtn = (Button) getActivity().findViewById(R.id.saveMark);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(saveChanges(etName.getText().toString(), etValue.getText().toString(), etWeighting.getText().toString())) {
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
        alertDialog.setMessage(getString(R.string.dialog_delete_mark));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                subjectMarksReference.child(markUuid).removeValue();
                markReference.child(markUuid).removeValue();
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

    private boolean saveChanges(String name, String value, String weighting){
        if(name.equals("")) {
            etName.setError(getString(R.string.error_name_empty));
            etName.requestFocus();
            return false;
        }

        if(value.equals("")) {
            etValue.setError(getString(R.string.error_value_empty));
            etValue.requestFocus();
            return false;
        }

        // Get mark value and weighting
        double markValue = Double.parseDouble(value);
        double markWeighting = (weighting.equals("")) ? 1.0 : Double.parseDouble(weighting);

        if(editMode){
            mark.setName(name);
            mark.setValue(markValue);
            mark.setWeighting(markWeighting);
            updateMark(mark);
        }else{
            MarkEntity newMark = new MarkEntity();
            newMark.setValue(markValue);
            newMark.setName(name);
            newMark.setWeighting(markWeighting);
            newMark.setSubject(subjectUuid);
            addMark(newMark);
        }

        return true;
    }
}