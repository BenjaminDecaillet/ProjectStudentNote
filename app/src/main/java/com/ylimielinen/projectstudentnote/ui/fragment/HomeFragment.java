package com.ylimielinen.projectstudentnote.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.ylimielinen.projectstudentnote.entity.MarkEntity;
import com.ylimielinen.projectstudentnote.ui.activity.MainActivity;
import com.ylimielinen.projectstudentnote.ui.adapter.HomeMarkAdapter;
import com.ylimielinen.projectstudentnote.ui.adapter.MarkAdapter;
import com.ylimielinen.projectstudentnote.ui.fragment.mark.EditMarkFragment;
import com.ylimielinen.projectstudentnote.util.ClickListener;
import com.ylimielinen.projectstudentnote.util.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Home fragment to display all notes of the student logged in
 */
public class HomeFragment extends Fragment {
    private static final String TAG = "SubjectsFragment";

    private List<MarkEntity> marks;
    private RecyclerView recyclerView;

    private String studentUuid;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference, markReference, subjectMarksReference, studentSubjectsReference;
    private FirebaseUser mUser;

    private HomeMarkAdapter homeMarkAdapter;
    private ValueEventListener marksListener;
    private ValueEventListener subjectMarksListener;
    private ValueEventListener studentSubjectsListener;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(getString(R.string.title_fragment_main));

        // Get current logged in studentUuid
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        studentUuid = mUser.getUid();

        // get firebase database and useful references
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();
        studentSubjectsReference = mDatabase.getReference("studentSubjects").child(studentUuid);
        markReference = mReference.child("marks");
        subjectMarksReference = mReference.child("subjectMarks");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Create the fragment view
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Add marks list
        recyclerView = (RecyclerView) view.findViewById(R.id.homeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        // Create the adapter and link it with recyclerview
        marks = new ArrayList<>();
        homeMarkAdapter = new HomeMarkAdapter(marks);
        recyclerView.setAdapter(homeMarkAdapter);

            // On click listener
            recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new ClickListener() {
                @Override
                public void onClick(View view, int position) {}

                @Override
                public void onLongClick(View view, int position) {
                    // on long click => open modification view
                    MarkEntity mark = marks.get(position);
                    EditMarkFragment emf = EditMarkFragment.newInstance(mark);

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.flContent, emf, "EditMark")
                            .addToBackStack("EditMark")
                            .commit();
                }
            }));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.title_fragment_main));

        // Get student's subjects, get subject's marks and then get these marks
        createMarkListener();
        createSubjectMarksListener();
        createStudentSubjectsListener();
        studentSubjectsReference.addValueEventListener(studentSubjectsListener);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Remove listeners to free memory
        studentSubjectsReference.removeEventListener(studentSubjectsListener);
        removeStudentSubjectsListener();
        removeSubjectMarksListener();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    private void createStudentSubjectsListener(){
        studentSubjectsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    subjectMarksReference.child(ds.getKey()).addValueEventListener(subjectMarksListener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    private void createMarkListener(){
        marksListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    MarkEntity markEntity = dataSnapshot.getValue(MarkEntity.class);
                    markEntity.setUid(dataSnapshot.getKey());

                    // update or adds mark
                    if(marks.contains(markEntity)) {
                        marks.set(marks.indexOf(markEntity), markEntity);
                    }else {
                        marks.add(markEntity);
                    }
                }else{
                    // remove mark
                    MarkEntity markEntity = new MarkEntity();
                    markEntity.setUid(dataSnapshot.getKey());
                    marks.remove(markEntity);
                }

                // update recyclerview
                homeMarkAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    private void createSubjectMarksListener(){
        subjectMarksListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot mark : dataSnapshot.getChildren()){
                    markReference.child(mark.getKey()).addValueEventListener(marksListener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    private void removeSubjectMarksListener(){
        subjectMarksListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot mark : dataSnapshot.getChildren()){
                    markReference.child(mark.getKey()).removeEventListener(marksListener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    private void removeStudentSubjectsListener(){
        studentSubjectsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    subjectMarksReference.child(ds.getKey()).removeEventListener(subjectMarksListener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }
}