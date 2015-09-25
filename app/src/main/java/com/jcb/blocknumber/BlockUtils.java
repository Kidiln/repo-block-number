package com.jcb.blocknumber;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BlockUtils {


    public static void showCustomToast(Context context) {

        Toast customToast = new Toast(context);
        customToast.setDuration(Toast.LENGTH_LONG);

        LayoutInflater inflate = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflate.inflate(R.layout.toast_next, null);
        TextView tv = (TextView) v.findViewById(R.id.next);
        tv.setText(context.getResources().getString(R.string.toast_next));
        customToast.setGravity(Gravity.RIGHT, 20, 0);
        customToast.setView(v);

        customToast.show();

    }


    public static void showToast(Context context, String message) {

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    public static void printLog(String message) {
        Log.d(BlockConstants.TAG, message);
    }

    /**
     * Method to send sms
     *
     * @param phonenumber : number to sent
     * @param message     : sms content
     */
    public static void sendSms(String phonenumber, String message) {
        SmsManager manager = SmsManager.getDefault();

        phonenumber = removeNonNumbers(phonenumber);
        int length = message.length();

//        if (length > SafeConstants.AUTOREPLY_TEXTLIMIT) {
        ArrayList<String> messagelist = manager.divideMessage(message);

        manager.sendMultipartTextMessage(phonenumber, null, messagelist,
                null, null);
//        } else {
//            manager.sendTextMessage(phonenumber, null, finalMessmessageage, null, null);
//        }
    }


    /**
     * Method for fetching the entire contacts of the device
     *
     * @param context : context passed
     * @return : list of contact, with their name, number, and contact id
     */
    public static ArrayList<String> fetchDeviceContact(Context context) {

        ContentResolver cr = context.getContentResolver();
        ArrayList<String> listContacts = null;
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                null, null, null);
        if (cur.getCount() > 0) {

            listContacts = new ArrayList<String>();
            while (cur.moveToNext()) {
                String id = cur.getString(cur
                        .getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur
                        .getString(cur
                                .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer
                        .parseInt(cur.getString(cur
                                .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                    + " = ?", new String[]{id}, null);

                    while (pCur.moveToNext()) {

                        listContacts.add(name + BlockConstants.STR_SEMICOLON + pCur.getString(pCur
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));

                    }

                    pCur.close();
                }
            }
        }

        cur.close();
        return listContacts;
    }

    /**
     * Method for sorting the list alphabatically
     *
     * @param sortList : list to be sorted
     */
    public static void sortContactsAlphabetically(ArrayList<String> sortList) {

        Collections.sort(sortList);


//        Collections.sort(sortList, new Comparator<Contact>() {
//
//            @Override
//            public int compare(Contact lhs, Contact rhs) {
//
//                if (lhs == null || rhs == null || lhs.getContactName() == null
//                        || rhs.getContactName() == null) {
//                    return -1;
//                }
//
//                return (lhs.getContactName()).compareTo(rhs.getContactName());
//            }
//        });

    }

    public static String getFinalBlockString(String number) {
        return BlockConstants.APPEND_NUM + number;
    }


    public static boolean isContactInBlockList(String number) {


        String checkNumber = removeNonNumbers(number);
        boolean isBlock = false;

        StringBuilder strTemp = new StringBuilder();
        for (String temp : BlockConstants.listBlockContacts) {
            strTemp.setLength(0);
            strTemp.append(removeNonNumbers(temp));

            if ((strTemp.toString()).contains(checkNumber) || checkNumber.contains(strTemp.toString())) {
                isBlock = true;
                break;
            }
        }

        return isBlock;

    }

    public static void saveBlockToShared(Context context) {

        StringBuffer strBuffer = new StringBuffer();
        for (String temp : BlockConstants.listBlockContacts) {

            strBuffer.append(temp);
            strBuffer.append("#");
        }
        strBuffer.trimToSize();
        if (strBuffer.length() > 0) {
            strBuffer.deleteCharAt(strBuffer.length() - 1);
        }
        Log.d(BlockConstants.TAG, strBuffer.toString());


        PreferenceHelper.putToPreference(context, BlockConstants.SHRD_KEY_SAVE, strBuffer.toString());
    }


    public static void getBlockListFromShared(Context context) {

        String strSave = PreferenceHelper.getFromPreference(context, BlockConstants.SHRD_KEY_SAVE, BlockConstants.STR_EMPTY);
        if (BlockConstants.listBlockContacts == null) {
            BlockConstants.listBlockContacts = new ArrayList<String>();
        }
        if (strSave.isEmpty()) {
            BlockConstants.listBlockContacts.clear();
        } else {
            String[] splitValue = strSave.split("#");
            List<String> tempList = (List<String>) Arrays.asList(splitValue);
            BlockConstants.listBlockContacts.clear();
            BlockConstants.listBlockContacts = new ArrayList<String>(tempList);
        }
    }


    /**
     * takes only the last 10 digit numbers of the phone number saved
     *
     * @param number : the number fetched
     * @return : the 10 digit number
     */
    public static String removeNonNumbers(String number) {

        if (number == null || number.isEmpty()) {
            return BlockConstants.STR_EMPTY;
        }

        String updateNumber = number.replaceAll(
                BlockConstants.REGEX_ONLYNUMBERS, BlockConstants.STR_EMPTY);
        int lenNum = updateNumber.length();
        if (lenNum > 10) {
            return updateNumber.substring(lenNum - 10);
        }
        return updateNumber;
    }

    public static void hideSoftKeyBoard(View view, Context ctx) {
        // This following command is used to hide the soft keyboard
        if (view != null && ctx != null) {
            InputMethodManager imm = (InputMethodManager) ctx
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }


    public static void hideSoftKeyBoardFromWindow(Activity activity) {
        // This following command is used to hide the soft keyboard

        View view = activity.getWindow().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

}
