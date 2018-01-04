package com.ylimielinen.projectstudentnote.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ylimielinen.projectstudentnote.R;
import com.ylimielinen.projectstudentnote.entity.MarkEntity;
import com.ylimielinen.projectstudentnote.entity.SubjectEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by decai on 11.11.2017.
 * Adapter for the subject display in the subject list
 */

public class SubjectAdapter extends RecyclerView.Adapter {
    private ArrayList<SubjectEntity> subjects;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference, subjectMarksReference, markReference;

    public SubjectAdapter(ArrayList<SubjectEntity> subjects) {
        this.subjects = subjects;

        // get firebase database and useful references
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();
        subjectMarksReference = mReference.child("subjectMarks");
        markReference = mReference.child("marks");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView  = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_subject, parent, false);
        return new SubjectViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final List<MarkEntity> marks = new ArrayList<>();
        SubjectEntity subject = subjects.get(position);
        final SubjectViewHolder sbvh = ((SubjectViewHolder)holder);

        subjectMarksReference.child(subject.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    for (final DataSnapshot mark :dataSnapshot.getChildren()) {
                        markReference.child(mark.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                MarkEntity markEntity = dataSnapshot.getValue(MarkEntity.class);
                                marks.add(markEntity);

                                double moy = displayAverage(marks);

                                if(moy !=-1) {
                                    sbvh.subjectAverage.setText(""+moy);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sbvh.subjectName.setText(subject.getName());
        sbvh.subjectDescription.setText(subject.getDescription());

    }

    private double displayAverage(List<MarkEntity> marks){
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

        return moy;
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    public static class SubjectViewHolder extends RecyclerView.ViewHolder {
        public TextView subjectName;
        public TextView subjectDescription;
        public TextView subjectAverage;

        public SubjectViewHolder(View itemView) {
            super(itemView);
            this.subjectName = (TextView) itemView.findViewById(R.id.subjectName);
            this.subjectDescription = (TextView) itemView.findViewById(R.id.subjectDescription);
            this.subjectAverage = (TextView) itemView.findViewById(R.id.subjectAverage);
        }
    }
}