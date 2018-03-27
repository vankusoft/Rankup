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
 * Created by Ivan on 3/26/2018.
 */

public class CustomListLayoutPostLikes extends ArrayAdapter<String>{

    List<String> profilePictureList;
    Context appContext,context;


    public CustomListLayoutPostLikes(@NonNull Context context, List<String> items,
                            List<String> profilePictureList, Context applicationContext) {
        super(context,R.layout.custom_list_layout_post_likes ,items);
        this.context=context;
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
            convertView = vi.inflate(R.layout.custom_list_layout_post_likes, parent, false);

            TextView nameTV=(TextView)convertView.findViewById(R.id.likesListName);
            ImageView imageView=(ImageView)convertView.findViewById(R.id.likesListImageView);

            String name=getItem(position);

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
