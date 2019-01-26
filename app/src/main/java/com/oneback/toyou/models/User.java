package com.oneback.toyou.models;

import com.google.firebase.database.IgnoreExtraProperties;

// [START blog_user_class]
@IgnoreExtraProperties
public class User {

    public String username;
    public String email;
    public String userType;
    public String document;
    public String celNumber;
    public String address;
    public String zipCode;
    public String nickName;
    public String nickId;
    public String avatar;
    public String termsOfUse;
    public String termsOfUseValue;


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }


    public User(String username, String email, String userType, String document, String celNumber, String address, String zipCode, String nickName, String nickId, String avatar, String termsOfUse, String termsOfUseValue) {
        this.username = username;
        this.email = email;
        this.userType = userType;
        this.document = document;
        this.celNumber = celNumber;
        this.address = address;
        this.zipCode = zipCode;
        this.nickName = nickName;
        this.nickId = nickId;
        this.avatar = avatar;
        this.termsOfUse = termsOfUse;
        this.termsOfUseValue = termsOfUseValue;
    }
}
// [END blog_user_class]
