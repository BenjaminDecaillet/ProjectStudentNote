package com.ylimielinen.projectstudentnote.ui.adapter;

import android.content.Context;
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
import com.ylimielinen.projectstudentnote.entity.SubjectEntity;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by decai on 12.11.2017.
 * Adapter for the mark display in the home page
 */

public class HomeMarkAdapter extends RecyclerView.Adapter {
    private List<MarkEntity> marks;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference, subjectReference;

    public HomeMarkAdapter(List<MarkEntity> marks) {
        this.marks = marks;

        // get firebase database and useful references
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();
        subjectReference = mReference.child("subjects");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView  = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_home_mark, parent, false);
        return new HomeMarkAdapter.HomeMarkViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MarkEntity mark = marks.get(position);
        final HomeMarkAdapter.HomeMarkViewHolder mvh = (HomeMarkAdapter.HomeMarkViewHolder)holder;

        // display mark name, value and its subject
        subjectReference.child(mark.getSubject()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    SubjectEntity subjectEntity = dataSnapshot.getValue(SubjectEntity.class);
                    subjectEntity.setUid(dataSnapshot.getKey());
                    mvh.subjectName.setText(subjectEntity.getName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mvh.markValue.setText(mark.getValue().toString());
        mvh.markName.setText(mark.getName());
    }

    @Override
    public int getItemCount() {
        return marks.size();
    }

    public class HomeMarkViewHolder extends RecyclerView.ViewHolder {
        public TextView subjectName;
        public TextView markName;
        public TextView markValue;

        public HomeMarkViewHolder(View view){
            super(view);
            this.subjectName = (TextView) view.findViewById(R.id.markHomeSubject);
            this.markName = (TextView) view.findViewById(R.id.markHomeName);
            this.markValue = (TextView) view.findViewById(R.id.markHomeValue);
        }
    }
}
