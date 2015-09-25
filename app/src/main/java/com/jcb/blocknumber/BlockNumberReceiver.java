package com.jcb.blocknumber;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BlockNumberReceiver extends BroadcastReceiver {


    private static final int MSG_REPLY = 1;
    private static final int DELAY_TIME = 4000;
    private static boolean isSmsSent = false;
    private ITelephony telephonyService;
    private Context mContext = null;
    private Handler callSmsHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_REPLY) {
                Log.d(BlockConstants.TAG, "MSG_REPLY Received");

                isSmsSent = false;
            }
        }
    };
    PhoneStateListener callBlockListener = new PhoneStateListener() {

        public void onCallStateChanged(int state, String incomingNumber) {

            boolean isBlockOn = PreferenceHelper.getFromPreference(mContext,
                    BlockConstants.SHRD_KEY_BLOCK, false);

            if (state == TelephonyManager.CALL_STATE_RINGING && isBlockOn) {
                // USED FOR HANDLING INCOMING CALLS
                Log.d(BlockConstants.TAG, "onCallStateChanged : CALL_STATE_RINGING");

                if (BlockConstants.listBlockContacts == null) {
                    BlockUtils.getBlockListFromShared(mContext);
                }

                if (BlockUtils.isContactInBlockList(incomingNumber)) {
                    telephonyService.endCall();

                    if (!isSmsSent) {
                        Log.d(BlockConstants.TAG, "MSG_REPLY Sent");

                        String strTemp = PreferenceHelper.getFromPreference(mContext, BlockConstants.SHRD_SMSCONTENT, BlockConstants.DEFUALT_SMS);
                        if (!strTemp.trim().isEmpty())
                            BlockUtils.sendSms(incomingNumber, strTemp);
                        isSmsSent = true;
                        callSmsHandler.sendMessageDelayed(callSmsHandler.obtainMessage(MSG_REPLY), DELAY_TIME);
                    }
                    Log.d(BlockConstants.TAG, "IS BLOCKED");
                    return;
                } else {

                    // Do Nothing as the incoming number is VIP number
                    Log.d(BlockConstants.TAG, "IS NOT BLOCKED");
                }

            } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                Log.d(BlockConstants.TAG, "onCallStateChanged : CALL_STATE_OFFHOOK");
            } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                Log.d(BlockConstants.TAG, "onCallStateChanged : CALL_STATE_IDLE");
            }
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e("INCOMING", "INCOMING onReceive");
        try {

            this.mContext = context;

            TelephonyManager telephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            // Java Reflections
            Class c = null;
            try {
                c = Class.forName(telephonyManager.getClass().getName());
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Method m = null;
            try {
                m = c.getDeclaredMethod("getITelephony");
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            m.setAccessible(true);
            try {
                telephonyService = (ITelephony) m.invoke(telephonyManager);
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            telephonyManager.listen(callBlockListener,
                    PhoneStateListener.LISTEN_CALL_STATE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}