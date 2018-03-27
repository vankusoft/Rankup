package com.jgntic.ivan.dormnews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Ivan on 3/21/2018.
 */

public class CustomListLayoutPostComments extends ArrayAdapter<String>{

    Context context,appContext;
    List<String> comments,profilePictureList;

    public CustomListLayoutPostComments(@NonNull Context context, List<String> item, List<String> comments,
                                        List<String> profilePictureList,Context applicationContext) {
        super(context,R.layout.custom_list_layout_post_comments ,item);
        this.context=context;
        this.comments=comments;
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
            convertView = vi.inflate(R.layout.custom_list_layout_post_comments, parent, false);

            TextView nameTV=(TextView)convertView.findViewById(R.id.customPostCommentsName);
            TextView commentsTV=(TextView)convertView.findViewById(R.id.customPostCommentsComment);
            ImageView imageView=(ImageView)convertView.findViewById(R.id.customPostCommentsImage);

            if(!profilePictureList.get(position).contentEquals("null"))
            {
                String  url=profilePictureList.get(position);

                Picasso.with(appContext)
                        .load(url)
                        .resize(90,90)
                        .onlyScaleDown()
                        .centerCrop()
                        .into(imageView);
            }
            if(!getItem(position).contentEquals("null"))
            {
                String nameString=getItem(position);
                nameTV.setText(nameString);
            }
            if(!comments.get(position).contentEquals("null"))
            {
                String commentsString=comments.get(position);
                commentsTV.setText(commentsString);
            }
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
