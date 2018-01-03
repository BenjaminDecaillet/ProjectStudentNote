package com.ylimielinen.projectstudentnote.ui.fragment.mark;

import android.app.Activity;
import android.content.Context;
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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ylimielinen.projectstudentnote.R;
import com.ylimielinen.projectstudentnote.entity.MarkEntity;
import com.ylimielinen.projectstudentnote.ui.activity.MainActivity;
import com.ylimielinen.projectstudentnote.ui.adapter.MarkAdapter;
import com.ylimielinen.projectstudentnote.util.ClickListener;
import com.ylimielinen.projectstudentnote.util.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;

public class MarksFragment extends Fragment {
    private static final String TAG = "MarksFragment";

    private List<MarkEntity> marks;
    private RecyclerView recyclerView;
    private MarkAdapter markAdapter;

    private Activity activity;

    private String subjectUuid;
    private TextView tvMoy;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference, markReference, subjectMarksReference;
    private FirebaseUser mUser;

    private ValueEventListener marksListener;
    private ValueEventListener subjectMarksListener;

    public MarksFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity.setTitle(getString(R.string.title_fragment_marks));

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            subjectUuid = bundle.getString("subjectUuid", "-1");
        }

        // get firebase database and useful references
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();
        markReference = mReference.child("marks");
        subjectMarksReference = mReference.child("subjectMarks").child(subjectUuid);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_marks, container, false);

        // Add button action
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fabMarks);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditMarkFragment emf = EditMarkFragment.newInstance(null);
                // Set subjectUuid id
                Bundle b = emf.getArguments();
                b.putString("subjectUuid", subjectUuid);

                // Create the fragment and display it
                ((MainActivity)activity).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContent, emf, "EditMark")
                        .addToBackStack("EditMark")
                        .commit();
            }
        });

        // Add marks list
        recyclerView = (RecyclerView) view.findViewById(R.id.marksRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        // Create the adapter and link it with recyclerview
        marks = new ArrayList<>();
        markAdapter = new MarkAdapter(marks);
        recyclerView.setAdapter(markAdapter);

        // On click listener
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {}

            @Override
            public void onLongClick(View view, int position) {
                // on long click => open modification view
                MarkEntity mark = marks.get(position);
                EditMarkFragment emf = EditMarkFragment.newInstance(mark);

                ((MainActivity)activity).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContent, emf, "EditMark")
                        .addToBackStack("EditMark")
                        .commit();
            }
        }));

        return view;
    }

    private void createMarkListener(){
        if(marksListener == null){
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
                    markAdapter.notifyDataSetChanged();
                    getAverage();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }
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

    @Override
    public void onResume() {
        super.onResume();
        activity.setTitle(getString(R.string.title_fragment_marks));
        createMarkListener();
        createSubjectMarksListener();
        subjectMarksReference.addValueEventListener(subjectMarksListener);
    }

    @Override
    public void onPause() {
        super.onPause();

        subjectMarksReference.removeEventListener(subjectMarksListener);
        removeSubjectMarksListener();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        activity = (Activity) context;
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void getAverage(){
        double moy=-1;
        double sum =0;
        double weight=0;
        if(marks.size()>0) {
            for (MarkEntity m : marks) {
                sum += m.getValue()*m.getWeighting();
                weight += m.getWeighting();
            }
            if(weight!=0){
                moy = sum / weight;
            }

        }
        if(isAdded()){
            tvMoy = activity.findViewById(R.id.moySubj);
            if(moy==-1) {
                tvMoy.setText(getString(R.string.average)+" : -");
            } else
            {
                tvMoy.setText(getString(R.string.average)+" : " + moy);
            }
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }


}