package com.jgntic.ivan.dormnews;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Ivan on 2/22/2018.
 */

public class CardViewHolder extends RecyclerView.ViewHolder {

    View mView;

    public CardViewHolder(View itemView) {
        super(itemView);

        mView=itemView;
    }

    public void setTitle(String title)
    {
        TextView postTitle=(TextView) mView.findViewById(R.id.postTitle);
        postTitle.setText(title);
    }

    public void setDescription(String desc)
    {
        TextView postDesc=(TextView) mView.findViewById(R.id.postDescription);
        postDesc.setText(desc);
    }

    public void setImage(String image, Context ctx)
    {
        ImageView imageView=(ImageView)mView.findViewById(R.id.postImage);
        Picasso.with(ctx).load(image).resize(700,700).onlyScaleDown().centerCrop().into(imageView);
    }

    public void setProfileImage(String image,Context ctx)
    {
        ImageView imageView=(ImageView)mView.findViewById(R.id.profilePictureCardView);
        Picasso.with(ctx).load(image).resize(60,60).onlyScaleDown().centerCrop().into(imageView);
    }

    public void setVotes(String votes)
    {
        TextView textView=(TextView)mView.findViewById(R.id.countText);
        textView.setText(votes);
    }

    public void setCommentsCount(String commentsCount)
    {
        TextView commentTextView=(TextView)mView.findViewById(R.id.countCommentsText);
        commentTextView.setText(commentsCount);
    }
}
