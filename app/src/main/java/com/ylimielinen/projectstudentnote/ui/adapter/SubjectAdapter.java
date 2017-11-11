package com.ylimielinen.projectstudentnote.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ylimielinen.projectstudentnote.R;
import com.ylimielinen.projectstudentnote.db.entity.SubjectEntity;

import java.util.List;

/**
 * Created by decai on 11.11.2017.
 */

public class SubjectAdapter extends RecyclerView.Adapter {
    List<SubjectEntity> subjects;

    public SubjectAdapter(List<SubjectEntity> subjects) {
        this.subjects = subjects;
    }
/*
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_subject,parent, false);
        }

        SubjectViewHolder viewHolder = (SubjectViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new SubjectViewHolder();
            viewHolder.subjectName = (TextView) convertView.findViewById(R.id.subjectName);
            viewHolder.subjectDescription = (TextView) convertView.findViewById(R.id.subjectDescription);
            convertView.setTag(viewHolder);
        }

        //getItem(position) va récupérer l'item [position] de la List<SubjectEntity> subjects
        SubjectEntity subject = getItem(position);

        viewHolder.subjectName.setText(subject.getName());
        viewHolder.subjectDescription.setText(subject.getDescription());

        return convertView;
    }
*/

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView  = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_subject, parent, false);
        return new SubjectViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SubjectEntity subject = subjects.get(position);
        SubjectViewHolder sbvh = ((SubjectViewHolder)holder);
        sbvh.subjectName.setText(subject.getName());
        sbvh.subjectDescription.setText(subject.getDescription());
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    public static class SubjectViewHolder extends RecyclerView.ViewHolder {
        public TextView subjectName;
        public TextView subjectDescription;

        public SubjectViewHolder(View itemView) {
            super(itemView);
            this.subjectName = (TextView) itemView.findViewById(R.id.subjectName);
            this.subjectDescription = (TextView) itemView.findViewById(R.id.subjectDescription);
        }
    }
}