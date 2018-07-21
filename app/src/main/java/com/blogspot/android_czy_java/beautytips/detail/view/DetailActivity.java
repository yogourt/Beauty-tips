package com.blogspot.android_czy_java.beautytips.detail.view;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ScrollView;
import android.widget.TextView;

import com.blogspot.android_czy_java.beautytips.R;
import com.blogspot.android_czy_java.beautytips.appUtils.SnackbarHelper;
import com.blogspot.android_czy_java.beautytips.detail.firebase.DetailFirebaseHelper;
import com.blogspot.android_czy_java.beautytips.ingredient.view.IngredientActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.blogspot.android_czy_java.beautytips.listView.view.ListViewAdapter.KEY_AUTHOR;
import static com.blogspot.android_czy_java.beautytips.listView.view.ListViewAdapter.KEY_FAV_NUM;
import static com.blogspot.android_czy_java.beautytips.listView.view.ListViewAdapter.KEY_ID;
import static com.blogspot.android_czy_java.beautytips.listView.view.ListViewAdapter.KEY_IMAGE;
import static com.blogspot.android_czy_java.beautytips.listView.view.ListViewAdapter.KEY_TITLE;

public class DetailActivity extends BaseItemActivity implements
        DetailFirebaseHelper.DetailViewInterface {

    @BindView(R.id.fab)
    FloatingActionButton mFab;

    @BindView(R.id.detail_scroll_view)
    ScrollView mScrollView;

    @BindView(R.id.layout_ingredients)
    View mLayoutIngredients;

    @BindView(R.id.description_text_view)
    TextView mDescTextView;

    @BindView(R.id.ingredient1)
    TextView mIngredient1;

    @BindView(R.id.ingredient2)
    TextView mIngredient2;

    @BindView(R.id.ingredient3)
    TextView mIngredient3;

    @BindView(R.id.ingredient4)
    TextView mIngredient4;

    @BindView(R.id.layout_author)
    View mAuthorLayout;

    @BindView(R.id.author_photo)
    CircleImageView mAuthorPhoto;

    @BindView(R.id.nickname_text_view)
    TextView mAuthorTv;

    @BindView(R.id.fav_text_view)
    TextView mFavTv;

    @BindView(R.id.source_text_view)
    TextView mSourceTv;

    private String mAuthorId;
    private long mFavNum;

    private DetailFirebaseHelper mFirebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            if(bundle.containsKey(KEY_AUTHOR)) mAuthorId = bundle.getString(KEY_AUTHOR);

            //get the fav num from bundle (it comes from db) when the activity opens for the first
            //time, and if it's orientation change get it from saved instance state, as it may
            //changed and not being already in db.
            //fav num is negative in db - it allows sorting in descending order of popularity
            if(savedInstanceState == null) mFavNum = bundle.getLong(KEY_FAV_NUM) * -1;
            else {
                mFavNum = savedInstanceState.getLong(KEY_FAV_NUM);
            }

            mFirebaseHelper = new DetailFirebaseHelper(this, mId);

            mFirebaseHelper.getFirebaseDatabaseData();
            prepareFab();
            prepareFavNum();
        }
        else {
            finish();
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(KEY_FAV_NUM, mFavNum);
    }

    public void prepareContent(@NonNull DataSnapshot dataSnapshot) {
        String description = (String) dataSnapshot.child("description").getValue();
        mDescTextView.setText(description);
        String ingredient1 = (String) dataSnapshot.child("ingredient1").getValue();
        if (!TextUtils.isEmpty(ingredient1)) {
            mIngredient1.setVisibility(View.VISIBLE);
            mIngredient1.setText(ingredient1);
        }
        String ingredient2 = (String) dataSnapshot.child("ingredient2").getValue();
        if (!TextUtils.isEmpty(ingredient2)) {
            mIngredient2.setVisibility(View.VISIBLE);
            mIngredient2.setText(ingredient2);
        }
        String ingredient3 = (String) dataSnapshot.child("ingredient3").getValue();
        if (!TextUtils.isEmpty(ingredient3)) {
            mIngredient3.setVisibility(View.VISIBLE);
            mIngredient3.setText(ingredient3);
        }
        String ingredient4 = (String) dataSnapshot.child("ingredient4").getValue();
        if (!TextUtils.isEmpty(ingredient4)) {
            mIngredient4.setVisibility(View.VISIBLE);
            mIngredient4.setText(ingredient4);
        }


        if (!TextUtils.isEmpty(mAuthorId)) {
            mAuthorLayout.setVisibility(View.VISIBLE);
            mFirebaseHelper.getAuthorPhotoFromDb(mAuthorId);
            mFirebaseHelper.getNicknameFromDb(mAuthorId);
        } else {
            int padding = (int) getResources().getDimension(R.dimen.desc_padding);
            int bottomPadding = (int) getResources().getDimension(
                    R.dimen.author_bottom_margin);
            int topPadding = (int) getResources().getDimension(
                    R.dimen.desc_top_padding);

            mDescTextView.setPadding(padding, topPadding, padding, bottomPadding);
        }


        mImageView.setContentDescription(getResources()
                .getString(R.string.description_tip_image, mTitle));

        //set source if it's in database
        if (dataSnapshot.child("source").getValue() != null) {
            String source = String.valueOf(dataSnapshot.child("source").getValue());
            mSourceTv.setVisibility(View.VISIBLE);
            mSourceTv.setText(Html.fromHtml(getResources().getString(R.string.source_label, source)));
            mSourceTv.setMovementMethod(LinkMovementMethod.getInstance());
        }

        makeIngredientsClickable();
    }

    private void prepareFavNum() {
        mFavTv.setText(getResources().getString(R.string.fav_label, String.valueOf(mFavNum)));
        if(mFavNum != 0) {
            mFavTv.setVisibility(View.VISIBLE);
        } else mFavTv.setVisibility(View.INVISIBLE);
    }

    public  void setAuthor(String nickname) {
        mAuthorTv.setText(nickname);
    }

    public void setAuthorPhoto(String photoUrl) {
        Glide.with(this)
                .setDefaultRequestOptions(new RequestOptions().placeholder(R.color.bluegray700))
                .load(photoUrl)
                .into(mAuthorPhoto);
    }

    /*
      I want FAB to be visible when ingredients layout is visible, and when user scrolls lower,
      it hides
     */
    private void prepareFab() {
        mScrollView.getViewTreeObserver().addOnScrollChangedListener(
                new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        if (mScrollView.getScrollY() < mLayoutIngredients.getHeight()){
                            mFab.show();
                        } else {
                            mFab.hide();
                        }
                    }
                }
        );
        if(!(FirebaseAuth.getInstance().getCurrentUser() == null) &&
                !FirebaseAuth.getInstance().getCurrentUser().isAnonymous()) {
            mFirebaseHelper.setFabState();
        }

    }

    public void changeFavouriteState(View view) {
        Animation scaleAnim = AnimationUtils.loadAnimation(this, R.anim.scale);
        mFab.startAnimation(scaleAnim);

        if(FirebaseAuth.getInstance().getCurrentUser() == null ||
                        FirebaseAuth.getInstance().getCurrentUser().isAnonymous()) {
            SnackbarHelper.showFeatureForLoggedInUsersOnly(
                    getResources().getString(R.string.feature_favourites), mScrollView);
            return;
        }
        int bluegray700 = getResources().getColor(R.color.bluegray700);
        if(mFab.getImageTintList().getDefaultColor() == bluegray700) {
            setFabActive();
            mFavNum++;
            mFirebaseHelper.addTipToFavourites(mFavNum);
            prepareFavNum();
        } else {
            mFab.setImageTintList(ColorStateList.valueOf(bluegray700));
            mFavNum--;
            mFirebaseHelper.removeTipFromFavourites(mFavNum);
            prepareFavNum();
        }
    }

    @Override
    public void setFabActive() {
        mFab.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.pink200)));
    }

    private void makeIngredientsClickable() {
        FirebaseDatabase.getInstance().getReference("ingredientList").
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ingredient: dataSnapshot.getChildren()) {
                            String ingredientTitle = String.valueOf(
                                    ingredient.child("title").getValue());

                            if(mIngredient1.getText().toString().toLowerCase()
                                    .equals(ingredientTitle.toLowerCase())) {
                                makeIngredientClickable(mIngredient1, ingredient);
                            } else if(mIngredient2.getText().toString().toLowerCase().
                                    equals(ingredientTitle.toLowerCase())) {
                                makeIngredientClickable(mIngredient2, ingredient);
                            } else if(mIngredient3.getText().toString().toLowerCase().
                                    equals(ingredientTitle.toLowerCase())) {
                                makeIngredientClickable(mIngredient3, ingredient);
                            }else if(mIngredient4.getText().toString().toLowerCase().
                                    equals(ingredientTitle.toLowerCase())) {
                                makeIngredientClickable(mIngredient4, ingredient);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void makeIngredientClickable(TextView ingredientView, final DataSnapshot ingredientData) {
        ingredientView.setPaintFlags(ingredientView.getPaintFlags()|
                Paint.UNDERLINE_TEXT_FLAG);
        ingredientView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ingredientActivityIntent = new Intent(DetailActivity.this,
                        IngredientActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(KEY_TITLE, String.valueOf(ingredientData.child("title").getValue()));
                bundle.putString(KEY_IMAGE, String.valueOf(ingredientData.child("image").getValue()));
                bundle.putString(KEY_ID, ingredientData.getKey());
                ingredientActivityIntent.putExtras(bundle);
                startActivity(ingredientActivityIntent);
            }
        });
    }

}
