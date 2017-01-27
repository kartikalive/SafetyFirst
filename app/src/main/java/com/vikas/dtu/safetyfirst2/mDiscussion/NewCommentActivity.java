package com.vikas.dtu.safetyfirst2.mDiscussion;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.style.TypefaceSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.commonsware.cwac.richedit.Effect;
import com.commonsware.cwac.richedit.RichEditText;
import com.commonsware.cwac.richedit.StyleEffect;
import com.commonsware.cwac.richedit.TypefaceEffect;
import com.commonsware.cwac.richtextutils.Selection;
import com.commonsware.cwac.richtextutils.SpannedXhtmlGenerator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vikas.dtu.safetyfirst2.BaseActivity;
import com.vikas.dtu.safetyfirst2.R;
import com.vikas.dtu.safetyfirst2.mData.Comment;
import com.vikas.dtu.safetyfirst2.mData.User;
import com.vikas.dtu.safetyfirst2.model.PostNotify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.realm.Realm;

public class NewCommentActivity extends BaseActivity implements View.OnClickListener{

    private RichEditText mCommentField;
    private Button mCommentButton;

    private DatabaseReference mCommentsReference;
    private String mPostKey;

    private Button mBoldButton;
    private Button mItalicButton;
    private Button mUnderlineButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_comment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCommentField = (RichEditText) findViewById(R.id.editor);
        mCommentButton = (Button) findViewById(R.id.button_post_comment);
        mBoldButton = (Button) findViewById(R.id.bold_button);
        mItalicButton = (Button) findViewById(R.id.italic_button);
        mUnderlineButton = (Button) findViewById(R.id.underline_button);

        mBoldButton.setOnClickListener(this);
        mItalicButton.setOnClickListener(this);
        mUnderlineButton.setOnClickListener(this);
        mCommentButton.setOnClickListener(this);

        mPostKey = getIntent().getStringExtra(PostDetailActivity.EXTRA_POST_KEY);
        if (mPostKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }
        mCommentsReference = FirebaseDatabase.getInstance().getReference()
                .child("post-comments").child(mPostKey);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Answer");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        // return true if you handled the button click, otherwise return false.
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_post_comment:
                if (!mCommentField.getText().toString().trim().equals("")) {
                    postComment();
                } else
                    Toast.makeText(this, "Write valid answer.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bold_button:
                mCommentField.applyEffect(RichEditText.BOLD, !mCommentField.hasEffect(RichEditText.BOLD));
                break;
            case R.id.italic_button:
                mCommentField.applyEffect(RichEditText.ITALIC, !mCommentField.hasEffect(RichEditText.ITALIC));
                break;
            case R.id.underline_button:
                mCommentField.applyEffect(RichEditText.UNDERLINE, !mCommentField.hasEffect(RichEditText.UNDERLINE));
                break;

        }
    }

    private void postComment() {
        mCommentButton.setClickable(false);
        mCommentButton.setBackgroundColor(getResources().getColor(R.color.grey));
        final String uid = getUid();
        FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user information
                        User user = dataSnapshot.getValue(User.class);
                        String authorName = user.username;

                        // Create new comment object
//                        SpannedXhtmlGenerator htmlText = new SpannedXhtmlGenerator();
//                        String commentText = htmlText.toXhtml(mCommentField.getText());
                        String commentText = mCommentField.getText().toString();
                        Comment comment = new Comment(uid, authorName, commentText);

                        // Push the comment, it will appear in the list
                        mCommentsReference.push().setValue(comment);

                        // Clear the field
                        mCommentField.setText(null);

                        // Add post-key to local DB to check for notification //
                        final DatabaseReference postNotifyRef = FirebaseDatabase.getInstance().getReference().child("post-notify");
                        final Realm realm = Realm.getDefaultInstance();
                        PostNotify postNotify = realm.where(PostNotify.class).equalTo("postKey", mPostKey).findFirst();
                        if (postNotify == null) {
                            realm.beginTransaction();
                            postNotify = realm.createObject(PostNotify.class);
                            postNotify.setUid(getUid());
                            postNotify.setPostKey(mPostKey);
                            final PostNotify finalPostNotify = postNotify;
                            postNotifyRef.child(mPostKey).child("num_of_comments").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    finalPostNotify.setNumComments(Integer.parseInt(dataSnapshot.getValue().toString()) + 1);
                                    realm.commitTransaction();
                                    postNotifyRef.child(mPostKey).child("num_of_comments").setValue(finalPostNotify.getNumComments());
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            /* For Star click
                            postNotifyRef.child(mPostKey).child("num_of_stars").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    finalPostNotify.setNumStars(Integer.parseInt(dataSnapshot.getValue().toString()));
                                    postNotifyRef.child(mPostKey).child("num_of_stars").setValue(finalPostNotify.getNumStars());
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            */
                        } else {
                            realm.beginTransaction();
                            final PostNotify finalPostNotify = realm.where(PostNotify.class).equalTo("postKey",mPostKey).findFirst();

                            postNotifyRef.child(mPostKey).child("num_of_comments").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    finalPostNotify.setNumComments(Integer.parseInt(dataSnapshot.getValue().toString()) + 1);
                                    realm.commitTransaction();
                                    postNotifyRef.child(mPostKey).child("num_of_comments").setValue(finalPostNotify.getNumComments());
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        //  END Realm Stuff //

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        mCommentButton.setClickable(true);
        finish();
        //  mCommentButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
    }
}
