package com.blogspot.android_czy_java.beautytips.view.listView.view;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.android_czy_java.beautytips.R;
import com.blogspot.android_czy_java.beautytips.database.recipe.RecipeModel;
import com.blogspot.android_czy_java.beautytips.view.listView.view.callback.NestedListCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.ItemViewHolder> {

    public static final String KEY_ITEM = "item";

    static final int REQUEST_CODE_DETAIL_ACTIVITY = 50;

    private int lastAnimatedPosition = -1;

    private List<RecipeModel> items;

    private NestedListCallback activityCallback;

    public RecipeListAdapter(NestedListCallback activityCallback, List<RecipeModel> list) {
        this.activityCallback = activityCallback;
        items = list;

    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View itemView = inflater.inflate(R.layout.card,
                parent, false);

        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        RecipeModel item = items.get(position);

        ViewCompat.setTransitionName(holder.mImage, item.getImageUrl());

        Context context = holder.itemView.getContext();
        Glide.with(context).
                setDefaultRequestOptions(new RequestOptions().dontTransform()).
                load(item.getImageUrl()).
                into(holder.mImage);

        holder.mTitle.setText(item.getTitle());
        holder.mImage.setContentDescription(context.getResources()
                .getString(R.string.description_tip_image, item.getTitle()));
        holder.card.setOnClickListener(view -> activityCallback.onRecipeClick(item.getRecipeId()));

        setAnimation(holder.itemView, position);

    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastAnimatedPosition) {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(),
                    R.anim.item_animation_fall_down);
            viewToAnimate.startAnimation(animation);
            lastAnimatedPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image)
        ImageView mImage;

        @BindView(R.id.title)
        TextView mTitle;

        @BindView(R.id.card)
        View card;

        ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private Bundle createSharedElementTransition() {
            Pair<View, String> imagePair = new Pair<>(mImage, mImage.getTransitionName());
            return ActivityOptions.makeSceneTransitionAnimation((Activity) this.itemView.getContext(),
                    imagePair).toBundle();
        }
    }


}
