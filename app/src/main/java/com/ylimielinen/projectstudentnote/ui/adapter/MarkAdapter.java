package com.ylimielinen.projectstudentnote.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ylimielinen.projectstudentnote.R;
import com.ylimielinen.projectstudentnote.db.entity.MarkEntity;

import java.util.List;

/**
 * Created by decai on 11.11.2017.
 */

public class MarkAdapter extends ArrayAdapter<MarkEntity> {

    public MarkAdapter(Context context, List<MarkEntity> marks) {
        super(context, 0, marks);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_mark,parent, false);
        }

        MarkViewHolder viewHolder = (MarkViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new MarkViewHolder();
            viewHolder.markName = (TextView) convertView.findViewById(R.id.markName);
            viewHolder.markValue = (TextView) convertView.findViewById(R.id.markValue);
            convertView.setTag(viewHolder);
        }

        //getItem(position) va récupérer l'item [position] de la List<SubjectEntity> subjects
        MarkEntity mark = getItem(position);

        viewHolder.markName.setText(mark.getName());
        viewHolder.markValue.setText(String.valueOf(mark.getValue()));

        return convertView;
    }

    public class MarkViewHolder {
        public TextView markName;
        public TextView markValue;
    }
}