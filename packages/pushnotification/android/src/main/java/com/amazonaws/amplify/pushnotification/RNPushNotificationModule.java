/*
 * Copyright 2017-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is located at
 *
 *     http://aws.amazon.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
 
package com.amazonaws.amplify.pushnotification;

import android.util.Log;
import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.ActivityEventListener;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;

import com.google.firebase.iid.FirebaseInstanceId;

import com.amazonaws.amplify.pushnotification.modules.RNPushNotificationJsDelivery;
import com.amazonaws.amplify.pushnotification.modules.RNPushNotificationCommon;

public class RNPushNotificationModule extends ReactContextBaseJavaModule implements ActivityEventListener
    private static final String LOG_TAG = "RNPushNotificationModule";

    public RNPushNotificationModule(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addActivityEventListener(this);
    }

    @Override
    public String getName() {
        return "RNPushNotification";
    }

    @ReactMethod
    public void initialize() {
        ReactApplicationContext context = getReactApplicationContext();
        Log.i(LOG_TAG, "initializing RNPushNotificationModule");

        // get the device token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        // send the token to device emitter
        // on register
        RNPushNotificationJsDelivery jsDelivery = new RNPushNotificationJsDelivery(context);
        Bundle bundle = new Bundle();
        bundle.putString("refreshToken", refreshedToken);
        jsDelivery.emitTokenReceived(bundle);
    }

    @ReactMethod
    public void getInitialNotification(Promise promise) {
        WritableMap params = Arguments.createMap();
        Activity activity = getCurrentActivity();
        if (activity != null) {
            Bundle bundle = RNPushNotificationCommon.getNotificationBundleFromIntent(activity.getIntent());
            if (bundle != null) {
                bundle.putBoolean("foreground", false);
                String bundleString = RNPushNotificationCommon.convertJSON(bundle);
                params.putString("dataJSON", bundleString);
            }
        }
        promise.resolve(params);
    }
     public void onNewIntent(Intent intent) {
        Bundle bundle = RNPushNotificationCommon.getNotificationBundleFromIntent(intent);
        if (bundle != null) intent.putExtra("notification", bundle);
    }
     public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        // Ignored, required to implement ActivityEventListener
    }
}
