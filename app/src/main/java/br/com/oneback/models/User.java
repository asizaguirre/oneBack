package br.com.oneback.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

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
    public String uid;


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public User(String username, String email, String userType, String document, String celNumber, String address, String zipCode, String nickName, String nickId, String avatar, String termsOfUse, String termsOfUseValue, String uid) {
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
        this.uid = uid;
    }


    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("username", username);
        result.put("email", email);
        result.put("userType", userType);
        result.put("document", document);
        result.put("celNumber", celNumber);
        result.put("address", address);
        result.put("zipCode", zipCode);
        result.put("nickName", nickName);
        result.put("nickId", nickId);
        result.put("avatar", avatar);
        result.put("termsOfUse", termsOfUse);
        result.put("termsOfUseValue", termsOfUseValue);

        return result;
    }


}
// [END blog_user_class]
