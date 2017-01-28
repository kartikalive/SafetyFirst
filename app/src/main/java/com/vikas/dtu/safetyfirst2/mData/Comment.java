package com.vikas.dtu.safetyfirst2.mData;

import com.fasterxml.jackson.databind.deser.std.ObjectArrayDeserializer;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START comment_class]
@IgnoreExtraProperties
public class Comment {

    public String uid;
    public String author;
    public String text;
    public int upvoteCount = 0;
    public int downvoteCount = 0;
    public String xmlText = null;
    public String image = null;
    public String file = null;

    public Comment() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    }

    public Comment(String uid, String author, String text, String xmlText, String image, String file) {
        this.uid = uid;
        this.author = author;
        this.text = text;
        this.xmlText = xmlText;
        this.image = image;
        this.file = file;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("text", text);
        result.put("image", image);
        result.put("xmlText", xmlText);
        result.put("file", file);
        result.put("upvoteCount", upvoteCount);
        result.put("downvoteCount", downvoteCount);

        return result;
    }


}
// [END comment_class]
