package com.spinyt.stripe;

import android.os.Bundle;

import android.content.Context;
import android.content.Intent;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StripePlugin extends CordovaPlugin {

    private CallbackContext PUBLIC_CALLBACKS = null;

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Context context = cordova.getActivity().getApplicationContext();
        PUBLIC_CALLBACKS = callbackContext;
        if (action.equals("payments_activity")) {
            this.openStripeActivity(context, args.getString(0), args.getString(1), args.getString(2), args.getString(3));
            return true;
        }

        if (action.equals("alert")) {
            callbackContext.success(args.getString(0));
            return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if(resultCode == cordova.getActivity().RESULT_OK){
            Bundle extras = data.getExtras();// Get data sent by the Intent
            String token = extras.getString("token"); // data parameter will be sent from the other activity.
            PUBLIC_CALLBACKS.success(token);
            return;
        }
        // Handle other results if exists.
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void openStripeActivity(Context context, String main_color, String status_bar_color, String title, String stripe_key) {
        Intent intent = new Intent(context, PaymentsActivity.class);
        intent.putExtra("main_color", main_color);
        intent.putExtra("status_bar_color", status_bar_color);
        intent.putExtra("title", title);
        intent.putExtra("stripe_key", stripe_key);
        this.cordova.startActivityForResult((CordovaPlugin) this, intent, 0);
    }
}