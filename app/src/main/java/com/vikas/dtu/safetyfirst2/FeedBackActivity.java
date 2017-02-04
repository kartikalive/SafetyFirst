package com.vikas.dtu.safetyfirst2;


import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.vikas.dtu.safetyfirst2.mData.Feedback;
import com.vikas.dtu.safetyfirst2.mData.User;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class FeedBackActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "NewFeedbackActivity";
    private static final String REQUIRED = "Required";
  //  private FirebaseStorage storage;

    private GoogleApiClient client;

    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref]

    private EditText mTitleField;
    private EditText mBodyField;
    private ImageView mVeryGood;
    private ImageView mGood;
    private ImageView mSatisfactory;
    private ImageView mBad;
    //private ImageView mStars;
    private ImageView mSubmit;
    private RatingBar mRatingBar;
    String title = "nothing";

    boolean very_good = false;
    boolean good = false;
    boolean satisfactory = false;
    boolean bad = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ActionBar ab = getSupportActionBar();

        // Enable the Up round_blue_dark
        ab.setDisplayHomeAsUpEnabled(true);

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
     //   storage = FirebaseStorage.getInstance();
        // [END initialize_database_ref]

        mTitleField = (EditText) findViewById(R.id.field_title);
        mBodyField = (EditText) findViewById(R.id.field_body);
        mVeryGood = (ImageView) findViewById(R.id.very_good);
        mGood = (ImageView) findViewById(R.id.good);
        mSatisfactory = (ImageView) findViewById(R.id.satisfactory);
        mBad = (ImageView) findViewById(R.id.bad);
        mSubmit = (ImageView) findViewById(R.id.submit);
        mRatingBar = (RatingBar)findViewById(R.id.ratingBar);
       // mStars = (ImageView) findViewById(R.id.stars);
        mVeryGood.setOnClickListener(this);
        mGood.setOnClickListener(this);
        mSatisfactory.setOnClickListener(this);
        mBad.setOnClickListener(this);
       mSubmit.setOnClickListener(this);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        //  ----------------------------------Code changed---------------------------------------------
        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if( rating==1)
                {
                    title = "Bad";
                    mVeryGood.setImageResource(R.drawable.very_good);
                    mGood.setImageResource(R.drawable.good);
                    mSatisfactory.setImageResource(R.drawable.satisfactory);
                    mBad.setImageResource(R.drawable.bad_pressed_r);
                }

                if(rating==2)
                {
                    title = "Satisfactory";
                    mVeryGood.setImageResource(R.drawable.very_good);
                    mGood.setImageResource(R.drawable.good);
                    mSatisfactory.setImageResource(R.drawable.satisfactory_pressed);
                    mBad.setImageResource(R.drawable.bad);
                }

                if(rating==3)
                {
                    title = "Good";
                    mVeryGood.setImageResource(R.drawable.very_good);
                    mGood.setImageResource(R.drawable.good_pressed);
                    mSatisfactory.setImageResource(R.drawable.satisfactory);
                    mBad.setImageResource(R.drawable.bad);
                }

                if(rating==4)
                {
                    title = "Very Good";
                    mVeryGood.setImageResource(R.drawable.pressed_very_good);
                    mGood.setImageResource(R.drawable.good);
                    mSatisfactory.setImageResource(R.drawable.satisfactory);
                    mBad.setImageResource(R.drawable.bad);
                }
            }
        });

        // ----------------------------------------------------------------------------------------------
    }


    private void submitPost() {

        final String body = mBodyField.getText().toString();


        // Body is required
        if (TextUtils.isEmpty(body)) {
            mBodyField.setError(REQUIRED);
            return;
        }

        // [START single_value_read]
        final String userId = getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        // [START_EXCLUDE]
                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(FeedBackActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new post
                            writeNewPost(userId, user.username, title, body);
                        }

                        // Finish this Activity, back to the stream
                        finish();
                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
        // [END single_value_read]
    }

    // [START write_fan_out]
    private void writeNewPost(String userId, String username, String title, String body) {
        // Create new feedback at /feedback/$userid/$feedbackid and at
        String key = mDatabase.child("feedback").push().getKey();
       Feedback feedback = new Feedback(userId, username, title, body);
        Map<String, Object> feedbackValues = feedback.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/feedback/" + key, feedbackValues);

        mDatabase.updateChildren(childUpdates);
        Toast.makeText(FeedBackActivity.this, "Feedback sent", Toast.LENGTH_SHORT).show();
    }
    // [END write_fan_out]




    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Pic Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.vikas.dtu.safetyfirst2/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Pic Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.vikas.dtu.safetyfirst2/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    public void onClick(View view) {

        switch(view.getId()) {
            case R.id.submit:
                submitPost();
                break;
            case R.id.very_good:
                very_good = !very_good;
                good = false;
                satisfactory = false;
                bad = false;
                setView();
                break;

            case R.id.good:
                very_good = false;
                good = !good;
                satisfactory = false;
                bad = false;
                setView();

                break;
            case R.id.satisfactory:
                very_good = false;
                good = false;
                satisfactory = !satisfactory;
                bad = false;
                setView();
                break;
            case R.id.bad:
                very_good = false;
                good = false;
                satisfactory = false;
                bad = !bad;
                setView();

                break;
        }

    }

    private void setView() {
        if (very_good) {
            title = "Very Good";
            mVeryGood.setImageResource(R.drawable.pressed_very_good);
            mGood.setImageResource(R.drawable.good);
            mSatisfactory.setImageResource(R.drawable.satisfactory);
            mBad.setImageResource(R.drawable.bad);
            mRatingBar.setRating(4);
            //   mStars.setImageResource(R.drawable.stars4);

        }
        if (good) {
            title = "Good";
            mVeryGood.setImageResource(R.drawable.very_good);
            mGood.setImageResource(R.drawable.good_pressed);
            mSatisfactory.setImageResource(R.drawable.satisfactory);
            mBad.setImageResource(R.drawable.bad);
            mRatingBar.setRating(3);
            // mStars.setImageResource(R.drawable.stars3);
        }
        if (satisfactory) {
            title = "Satisfactory";
            mVeryGood.setImageResource(R.drawable.very_good);
            mGood.setImageResource(R.drawable.good);
            mSatisfactory.setImageResource(R.drawable.satisfactory_pressed);
            mBad.setImageResource(R.drawable.bad);
            mRatingBar.setRating(2);
            //mStars.setImageResource(R.drawable.stars2);
        }
        if (bad) {
            title = "Bad";
            mVeryGood.setImageResource(R.drawable.very_good);
            mGood.setImageResource(R.drawable.good);
            mSatisfactory.setImageResource(R.drawable.satisfactory);
            mBad.setImageResource(R.drawable.bad_pressed_r);
            mRatingBar.setRating(1);
            //mStars.setImageResource(R.drawable.stars1);}
        }

    }
    }