package com.amazonaws.models.nosql;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;
import java.util.Map;
import java.util.Set;

@DynamoDBTable(tableName = "auramobile-mobilehub-643849732-Users")

public class UsersDO {
    private String _userID;
    private List<String> _devices;
    private String _mobileID;

    @DynamoDBHashKey(attributeName = "UserID")
    @DynamoDBAttribute(attributeName = "UserID")
    public String getUserID() {
        return _userID;
    }

    public void setUserID(final String _userID) {
        this._userID = _userID;
    }
    @DynamoDBAttribute(attributeName = "Devices")
    public List<String> getDevices() {
        return _devices;
    }

    public void setDevices(final List<String> _devices) {
        this._devices = _devices;
    }
    @DynamoDBAttribute(attributeName = "MobileID")
    public String getMobileID() {
        return _mobileID;
    }

    public void setMobileID(final String _mobileID) {
        this._mobileID = _mobileID;
    }

}
