package com.wozart.route_3;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobilehelper.auth.DefaultSignInResultHandler;
import com.amazonaws.mobilehelper.auth.IdentityHandler;
import com.amazonaws.mobilehelper.auth.IdentityManager;
import com.amazonaws.mobilehelper.auth.IdentityProvider;
import com.amazonaws.mobilehelper.auth.user.IdentityProfile;
import com.wozart.route_3.noSql.SqlOperationUserTable;

/**
 * Created by wozart on 12/10/17.
 */

public class SignInHandler extends DefaultSignInResultHandler {

    private boolean IsUserAvailable;
    private static final String LOG_TAG = SignInHandler.class.getSimpleName();

    @Override
    public void onSuccess(final Activity callingActivity, final IdentityProvider provider) {
        if (provider != null) {
            Log.d(LOG_TAG, String.format("User sign-in with %s provider succeeded",
                    provider.getDisplayName()));
            Toast.makeText(callingActivity, String.format(
                    callingActivity.getString(R.string.sign_in_succeeded_message_format),
                    provider.getDisplayName()), Toast.LENGTH_LONG).show();
        }
        getIdentity();
        goMain(callingActivity);
    }

    @Override
    public boolean onCancel(final Activity callingActivity) {
        // User abandoned sign in flow.
        final boolean shouldFinishSignInActivity = false;
        return shouldFinishSignInActivity;
    }

    /**
     * Go to the main activity.
     */
    private void goMain(final Activity callingActivity) {
        callingActivity.startActivity(new Intent(callingActivity, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    private void userCheck(final String id) {
        final SqlOperationUserTable user = new SqlOperationUserTable();
        Runnable runnable = new Runnable() {
            public void run() {
                IsUserAvailable = user.CheckUser(id);
                if (!IsUserAvailable)
                    userInsert(id);
            }
        };
        Thread saveUserId = new Thread(runnable);
        saveUserId.start();
    }

    private void userInsert(final String id) {
        final SqlOperationUserTable user = new SqlOperationUserTable();
        Runnable runnable = new Runnable() {
            public void run() {
                user.InsertData(id);
            }
        };
        Thread saveUserId = new Thread(runnable);
        saveUserId.start();
    }

    private void getIdentity() {
        Runnable runnable = new Runnable() {
            public void run() {
                final IdentityManager identityManager = AWSMobileClient.defaultMobileClient().getIdentityManager();
                Log.d(LOG_TAG, "fetchUserIdentity");

                AWSMobileClient.defaultMobileClient()
                        .getIdentityManager()
                        .getUserID(new IdentityHandler() {

                            @Override
                            public void onIdentityId(String identityId) {
                                if (identityManager.isUserSignedIn()) {
                                    final IdentityProfile identityProfile = identityManager.getIdentityProfile();

                                    if (identityProfile != null) {
                                        userCheck(identityId);
                                    }
                                }
                            }

                            @Override
                            public void handleError(Exception exception) {
                                Log.e(LOG_TAG, " " + exception);
                            }
                        });
            }
        };
        Thread saveUserId = new Thread(runnable);
        saveUserId.start();
    }

}

