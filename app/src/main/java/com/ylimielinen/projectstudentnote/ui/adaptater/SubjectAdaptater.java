package com.ylimielinen.projectstudentnote.ui.adaptater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ylimielinen.projectstudentnote.R;
import com.ylimielinen.projectstudentnote.db.entity.SubjectEntity;

import java.util.List;

/**
 * Created by decai on 11.11.2017.
 */

public class SubjectAdaptater extends ArrayAdapter<SubjectEntity> {

    public SubjectAdaptater(Context context, List<SubjectEntity> subjects) {
        super(context, 0, subjects);
    }

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

    public class SubjectViewHolder {
//        public TextView idSubject;
        public TextView subjectName;
        public TextView subjectDescription;
    }
}

