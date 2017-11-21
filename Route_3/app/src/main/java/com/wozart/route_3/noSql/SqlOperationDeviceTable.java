package com.wozart.route_3.noSql;

import android.util.Log;

import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.models.nosql.KeysDO;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wozart on 17/11/17.
 */

public class SqlOperationDeviceTable {
    private static final String LOG_TAG = SqlOperationDeviceTable.class.getSimpleName();
    private final static DynamoDBMapper mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();

    public KeysDO SearchAvailableDevices() {
        KeysDO availableDevice;
        Map<String, AttributeValue> availableThings = new HashMap<>();
        availableThings.put(":val1", new AttributeValue().withN("1"));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("Available = :val1").withExpressionAttributeValues(availableThings);

        List<KeysDO> scanResult = mapper.scan(KeysDO.class, scanExpression);
        if(scanResult.isEmpty()) {
            Log.e(LOG_TAG, "No Devices available on AWS: " + scanResult);
            return null;
        } else {
            availableDevice = scanResult.get(0);
            Log.d(LOG_TAG, "Received Thing Name: " + availableDevice.getThing());
            return availableDevice;
        }
    }

    public void UpdateAvailablity(String device){
        KeysDO changeAvailablity = mapper.load(KeysDO.class, device);
        changeAvailablity.setAvailable(0.0);
        mapper.save(changeAvailablity);
        Log.d(LOG_TAG, "Availability changed ");
    }
}
