package com.jgntic.ivan.dormnews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Ivan on 3/15/2018.
 */

public class CustomListLayoutHotOrNot extends ArrayAdapter<String> {

    List<String> profilePictureList,votes;
    Context appContext,context;


    public CustomListLayoutHotOrNot(@NonNull Context context, List<String> items, List<String> votes, List<String> profilePictureList, Context applicationContext) {
        super(context,R.layout.custom_list_layout_hot_or_not ,items);
        this.context=context;
        this.votes=votes;
        this.profilePictureList=profilePictureList;
        this.appContext=applicationContext;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        if(convertView==null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.custom_list_layout_hot_or_not, parent, false);

            TextView nameTV = (TextView) convertView.findViewById(R.id.hotListText);
            TextView votesTV = (TextView) convertView.findViewById(R.id.hotListVote);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.hotListImage);

            String name = getItem(position);
            String vote = votes.get(position);

            if (!profilePictureList.get(position).contentEquals("null")) {
                String url = profilePictureList.get(position);

                Picasso.with(appContext)
                        .load(url)
                        .resize(100, 100)
                        .centerCrop()
                        .into(imageView);

            }

            nameTV.setText(name);
            votesTV.setText(vote);
        }

        return convertView;
    }

    @Override
    public int getViewTypeCount() {

        int count=getCount();

        if(count>0)
        {
            return count;
        }else
        {
            return super.getViewTypeCount();
        }
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }
}
