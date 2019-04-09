package com.tejasdadhe.convokit.Modals;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;


public class User implements Parcelable {


    private String profilePicUri;
    private String displayName;
    private String userId;
    private String lastSeen;
    private String email;
    private String phoneNumber;
    private String presenceStamp;
    private String deviceToken;
    private String userType;

    public User(String profilePicUri, String displayName, String userId, String lastSeen, String email, String phoneNumber) {
        this.profilePicUri = profilePicUri;
        this.displayName = displayName;
        this.userId = userId;
        this.lastSeen = lastSeen;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public User()
    { }

    public String getProfilePicUri() {
        return profilePicUri;
    }

    public void setProfilePicUri(String profilePicUri) {
        this.profilePicUri = profilePicUri;
    }

    public @NonNull String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPresenceStamp() {
        return presenceStamp;
    }

    public void setPresenceStamp(String presenceStamp) {
        this.presenceStamp = presenceStamp;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public String getUserType() { return userType; }

    public void setUserType(String userType) { this.userType = userType; }

    protected User(Parcel in) {
        profilePicUri = in.readString();
        displayName = in.readString();
        userId = in.readString();
        lastSeen = in.readString();
        email = in.readString();
        phoneNumber = in.readString();
        presenceStamp = in.readString();
        deviceToken = in.readString();
        userType = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(profilePicUri);
        dest.writeString(displayName);
        dest.writeString(userId);
        dest.writeString(lastSeen);
        dest.writeString(email);
        dest.writeString(phoneNumber);
        dest.writeString(presenceStamp);
        dest.writeString(deviceToken);
        dest.writeString(userType);
    }

    @SuppressWarnings("unused")
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}