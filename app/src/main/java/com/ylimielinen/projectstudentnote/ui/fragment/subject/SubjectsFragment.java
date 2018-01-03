package com.ylimielinen.projectstudentnote.ui.fragment.subject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ylimielinen.projectstudentnote.R;
import com.ylimielinen.projectstudentnote.entity.SubjectEntity;
import com.ylimielinen.projectstudentnote.ui.activity.MainActivity;
import com.ylimielinen.projectstudentnote.ui.adapter.SubjectAdapter;
import com.ylimielinen.projectstudentnote.ui.fragment.mark.MarksFragment;
import com.ylimielinen.projectstudentnote.util.ClickListener;
import com.ylimielinen.projectstudentnote.util.RecyclerTouchListener;

import java.util.ArrayList;

public class SubjectsFragment extends Fragment {
    private static final String TAG = "SubjectsFragment";

    private ArrayList<SubjectEntity> subjects;
    private RecyclerView recyclerView;
    private SubjectAdapter subjectAdapter;

    private String studentUuid;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference, subjectReference, studentSubjectsReference;
    private FirebaseUser mUser;

    private ValueEventListener subjectsListener;
    private ValueEventListener studentSubjectsListener;

    public SubjectsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) getActivity()).setTitle(getString(R.string.title_fragment_subjects));

        // Get current logged in studentUuid
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        studentUuid = mUser.getUid();

        // get firebase database and useful references
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();
        subjectReference = mReference.child("subjects");
        studentSubjectsReference = mReference.child("studentSubjects").child(studentUuid);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Create the fragment view
        View view = inflater.inflate(R.layout.fragment_subjects, container, false);

        // Add button action
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fabSubjects);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditSubjectFragment esf = EditSubjectFragment.newInstance(null);

                // Create the fragment and display it
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContent, esf, "EditSubject")
                        .addToBackStack("EditSubject")
                        .commit();
            }
        });

        // Add subjects list
        recyclerView = (RecyclerView) view.findViewById(R.id.subjectsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        // Create the adapter and link it with recyclerview
        subjects = new ArrayList<>();
        subjectAdapter = new SubjectAdapter(subjects);
        recyclerView.setAdapter(subjectAdapter);

        // On click listener
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                SubjectEntity subject = subjects.get(position);

                MarksFragment mf = new MarksFragment();
                Bundle bundle = new Bundle();
                bundle.putString("subjectUuid", subject.getUid());
                mf.setArguments(bundle);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContent, mf, "ShowMarks")
                        .addToBackStack("marksStudentLoggedIn")
                        .commit();
            }

            @Override
            public void onLongClick(View view, int position) {
                // on long click => open modification view
                SubjectEntity subject = subjects.get(position);
                EditSubjectFragment esf = EditSubjectFragment.newInstance(subject);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContent, esf, "EditSubject")
                        .addToBackStack("EditSubject")
                        .commit();
            }
        }));
        return view;
    }

    private void createSubjectListener(){
        if(subjectsListener == null){
            subjectsListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() != null){
                        SubjectEntity subjectEntity = dataSnapshot.getValue(SubjectEntity.class);
                        subjectEntity.setUid(dataSnapshot.getKey());

                        // update or adds subjects
                        if(subjects.contains(subjectEntity)){
                            subjects.set(subjects.indexOf(subjectEntity), subjectEntity);
                        }else{
                            subjects.add(subjectEntity);
                        }
                    }else{
                        // remove subject
                        SubjectEntity subjectEntity = new SubjectEntity();
                        subjectEntity.setUid(dataSnapshot.getKey());
                        subjects.remove(subjectEntity);
                    }

                    // update recyclerview
                    subjectAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }
    }

    private void createStudentSubjectListener(){
        studentSubjectsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot subject : dataSnapshot.getChildren()) {
                    subjectReference.child(subject.getKey()).addValueEventListener(subjectsListener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    private void removeStudentSubjectListener(){
        studentSubjectsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot subject : dataSnapshot.getChildren()) {
                    subjectReference.child(subject.getKey()).removeEventListener(subjectsListener);
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
        ((MainActivity) getActivity()).setTitle(getString(R.string.title_fragment_subjects));
        createSubjectListener();
        createStudentSubjectListener();
        studentSubjectsReference.addValueEventListener(studentSubjectsListener);
    }

    @Override
    public void onPause() {
        super.onPause();

        studentSubjectsReference.removeEventListener(studentSubjectsListener);
        removeStudentSubjectListener();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

    }
}