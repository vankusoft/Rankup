package com.jgntic.ivan.dormnews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Ivan on 3/8/2018.
 */

public class CustomListLayout extends ArrayAdapter<String>{

    List<String> profilePictureList,votes;
    Context appContext,context;


    public CustomListLayout(@NonNull Context context, List<String> items,List<String> votes,
                            List<String> profilePictureList, Context applicationContext) {
        super(context,R.layout.custom_list_layout ,items);
        this.context=context;
        this.votes=votes;
        this.profilePictureList=profilePictureList;
        this.appContext=applicationContext;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        if(convertView==null)
        {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.custom_list_layout, parent, false);

            TextView nameTV=(TextView)convertView.findViewById(R.id.topListText);
            TextView votesTV=(TextView)convertView.findViewById(R.id.topListVote);
            ImageView imageView=(ImageView)convertView.findViewById(R.id.topListImage);

            String name=getItem(position);
            String vote=votes.get(position);

            if(!profilePictureList.get(position).contentEquals("null"))
            {
                String url=profilePictureList.get(position);

                Picasso.with(appContext)
                        .load(url)
                        .resize(100,100)
                        .onlyScaleDown()
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
