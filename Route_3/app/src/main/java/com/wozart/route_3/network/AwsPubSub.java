package com.wozart.route_3.network;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.AWSConfiguration;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
import com.amazonaws.regions.Region;
import com.facebook.AccessToken;
import com.wozart.route_3.model.AwsState;
import com.wozart.route_3.utilities.JsonUtils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static com.amazonaws.mobilehelper.util.ThreadUtils.runOnUiThread;

/**
 * Created by wozart on 13/11/17.
 */

public class AwsPubSub extends Service {

    private static final String LOG_TAG = "AWS IoT PubSub";
    private JsonUtils jsonUtils = new JsonUtils();
    IBinder mBinder = new LocalAwsBinder();
    // --- Constants to modify per your configuration ---

    // Customer specific IoT endpoint
    private static final String CUSTOMER_SPECIFIC_ENDPOINT = "a15bui8ebaqvjn.iot.us-east-1.amazonaws.com";


    private AWSIotMqttManager mqttManager;
    private String clientId;
    private AwsState CloudData = new AwsState();

    private AWSCredentials awsCredentials;
    private CognitoCachingCredentialsProvider credentialsProvider;

    public class LocalAwsBinder extends Binder {
        public AwsPubSub getServerInstance() {
            return AwsPubSub.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Initialize the AWS Cognito credentials provider
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(), // context
                AWSConfiguration.AMAZON_COGNITO_IDENTITY_POOL_ID,// Identity Pool ID
                AWSConfiguration.AMAZON_COGNITO_REGION // Region
        );

        Region region = Region.getRegion(AWSConfiguration.AMAZON_COGNITO_REGION);

        Map<String, String> logins = new HashMap<String, String>();
        logins.put("graph.facebook.com", AccessToken.getCurrentAccessToken().getToken());
        credentialsProvider.setLogins(logins);

        // MQTT Client
        clientId = credentialsProvider.getCachedIdentityId();
        mqttManager = new AWSIotMqttManager(clientId, CUSTOMER_SPECIFIC_ENDPOINT);

        // The following block uses IAM user credentials for authentication with AWS IoT.
        //awsCredentials = new BasicAWSCredentials("ACCESS_KEY_CHANGE_ME", "SECRET_KEY_CHANGE_ME");
        //btnConnect.setEnabled(true);

        // The following block uses a Cognito credentials provider for authentication with AWS IoT.

        new Thread(new Runnable() {
            @Override
            public void run() {
                awsCredentials = credentialsProvider.getCredentials();
                Connect();
            }
        }).start();
        return START_NOT_STICKY;
    }

    private void Connect() {
        Log.d(LOG_TAG, "clientId = " + clientId);

        try {
            mqttManager.connect(credentialsProvider, new AWSIotMqttClientStatusCallback() {
                @Override
                public void onStatusChanged(final AWSIotMqttClientStatus status,
                                            final Throwable throwable) {
                    Log.d(LOG_TAG, "Status = " + String.valueOf(status));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (status == AWSIotMqttClientStatus.Connecting) {

                            } else if (status == AWSIotMqttClientStatus.Connected) {
                                sendDataToActivity("Connected");
                            } else if (status == AWSIotMqttClientStatus.Reconnecting) {
                                if (throwable != null) {
                                    Log.e(LOG_TAG, "Connection error.", throwable);
                                }
                            } else if (status == AWSIotMqttClientStatus.ConnectionLost) {
                                if (throwable != null) {
                                    Log.e(LOG_TAG, "Connection error.", throwable);
                                    throwable.printStackTrace();
                                }
                            }
                        }
                    });
                }
            });
        } catch (final Exception e) {
            Log.e(LOG_TAG, "Connection error: ", e);
        }
    }

    public void AwsSubscribe(String device) {
        final String topic = "$aws/things/" + device + "/shadow/update/accepted";
        final JsonUtils jsonUtils = new JsonUtils();
        Log.d(LOG_TAG, "topic = " + topic);

        try {
            mqttManager.subscribeToTopic(topic, AWSIotMqttQos.QOS0,
                    new AWSIotMqttNewMessageCallback() {
                        @Override
                        public void onMessageArrived(final String topic, final byte[] data) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String message = new String(data, "UTF-8");
                                        Log.d(LOG_TAG, "Message arrived:");
                                        Log.d(LOG_TAG, "   Topic: " + topic);
                                        Log.d(LOG_TAG, " Message: " + message);
                                        //CloudData = jsonUtils.DeserializeAwsData(message);
                                        if (CloudData != null) {
                                            //TODO write function
                                        }
                                    } catch (UnsupportedEncodingException e) {
                                        Log.e(LOG_TAG, "Message encoding error.", e);
                                    }
                                }
                            });
                        }
                    });
        } catch (Exception e) {
            Log.e(LOG_TAG, "Subscription error.", e);
        }
    }

    public void AWSPublish(String device, String data) {

        final String topic = "$aws/things/" + device + "/shadow/update";
        Log.d(LOG_TAG, "Data to Publish: " + data);
        try {
            mqttManager.publishString(data, topic, AWSIotMqttQos.QOS0);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Publish error.", e);
        }

    }

    private void sendDataToActivity(String connected){
        Intent intent = new Intent("AwsShadow");
        intent.putExtra("Connection", connected);
        LocalBroadcastManager.getInstance(AwsPubSub.this).sendBroadcast(intent);
    }
}
