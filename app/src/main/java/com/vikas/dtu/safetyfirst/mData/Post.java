package com.vikas.dtu.safetyfirst.mData;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class Post {

    public String authorImageUrl;
    public String uid;
    public String author;
    public String title;
    public String body;
    public String image;
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String uid, String author, String title, String body, String image, String authorImageUrl) {
        this.authorImageUrl = authorImageUrl;
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.body = body;
        this.image = image;
    }

    public String getPhotoUrl() {
        return authorImageUrl;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("title", title);
        result.put("body", body);
        result.put("image", image);
        result.put("starCount", starCount);
        result.put("stars", stars);
        result.put("authorImageUrl", authorImageUrl);

        return result;
    }
    // [END post_to_map]

}
// [END post_class]
