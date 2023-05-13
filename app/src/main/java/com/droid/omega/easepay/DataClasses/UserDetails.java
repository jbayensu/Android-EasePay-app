package com.droid.omega.easepay.DataClasses;

/**
 * Created by gleam on 3/27/2016.
 */
public class UserDetails {
    private static String userID;
    private static String userEmail;

    public UserDetails() {
    }

    public static String getUserID() {
        return userID;
    }

    public static void setUserID(String userID) {
        UserDetails.userID = userID;
    }

    public static String getUserEmail() {
        return userEmail;
    }

    public static void setUserEmail(String userEmail) {
        UserDetails.userEmail = userEmail;
    }
}
