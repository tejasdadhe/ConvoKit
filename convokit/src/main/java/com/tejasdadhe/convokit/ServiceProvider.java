package com.tejasdadhe.convokit;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ServiceProvider {



    public static String generatePrivateConversationId(String userId1, String userID2)
    {
        String newKey;

        if(userId1.compareToIgnoreCase(userID2) > 0)
            newKey = userId1 + "someRandomSalt " + userID2;
        else
            newKey = userID2 + "someRandomSalt " + userId1;
        return md5(newKey);
    }


    private static String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}
