package com.wozart.route_3.noSql;

import android.util.Log;

import com.Constant;
import com.amazonaws.AmazonClientException;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.models.nosql.UsersDO;

import java.util.List;

public class SqlOperationTable {

    private static final String LOG_TAG = SqlOperationTable.class.getSimpleName();
    private final static DynamoDBMapper mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
    private final static UsersDO User = new UsersDO();
    private UserData Device = new UserData();

    public void InsertData(String id) {
        User.setUserID(id);
        User.setDevices(Device.GetDevices());
        User.setMobileID("android-1asdkahsdg87g");
        AmazonClientException lastException = null;

        try {
            mapper.save(User);
            Log.d(LOG_TAG, "User Data Inserted");
        } catch (final AmazonClientException ex) {
            Log.e(LOG_TAG, "Failed saving item : " + ex.getMessage(), ex);
            lastException = ex;
        }

        if (lastException != null) {
            // Re-throw the last exception encountered to alert the user.
            throw lastException;
        }
    }

    public UsersDO GetData(String id) {
        UsersDO selectedUser = mapper.load(UsersDO.class, id);
        if(selectedUser == null)
            return null;
        User.setUserID(selectedUser.getUserID());
        User.setDevices(selectedUser.getDevices());
        User.setMobileID(selectedUser.getMobileID());
        return User;
    }

    public boolean CheckUser(String id) {
        UsersDO checkUser = mapper.load(UsersDO.class, id);
        if (checkUser == null)
            return false;
        else
            return true;
    }

    public void UpdateUserDevices(String device){
        UsersDO updateDevice = mapper.load(UsersDO.class, Constant.IDENTITY_ID);
        List<String> devices = updateDevice.getDevices();
        devices.add(device);
        updateDevice.setDevices(devices);
        mapper.save(updateDevice);
    }

    public UsersDO GetUserInfo() {
        return User;
    }

}

