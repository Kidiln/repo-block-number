package com.jcb.blocknumber;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceHelper {

	public synchronized static void putToPreference(Context context,
			String key, Object value) {
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();
		if (value instanceof Boolean) {
			editor.putBoolean(key, (Boolean) value);
		} else if (value instanceof Float) {
			editor.putFloat(key, (Float) value);
		} else if (value instanceof Long) {
			editor.putLong(key, (Long) value);
		} else if (value instanceof Integer) {
			editor.putInt(key, (Integer) value);
		} else {
			editor.putString(key, String.valueOf(value));
		}
		editor.commit();
	}

	@SuppressWarnings("unchecked")
	public synchronized static <T> T getFromPreference(Context context,
			String key, T defValue) {
		Object objValue = null;
		if (defValue instanceof Boolean) {
			objValue = PreferenceManager.getDefaultSharedPreferences(context)
					.getBoolean(key, (Boolean) defValue);
		} else if (defValue instanceof Float) {
			objValue = PreferenceManager.getDefaultSharedPreferences(context)
					.getFloat(key, (Float) defValue);
		} else if (defValue instanceof Long) {
			objValue = PreferenceManager.getDefaultSharedPreferences(context)
					.getLong(key, (Long) defValue);
		} else if (defValue instanceof Integer) {
			objValue = PreferenceManager.getDefaultSharedPreferences(context)
					.getInt(key, (Integer) defValue);
		} else {
			objValue = PreferenceManager.getDefaultSharedPreferences(context)
					.getString(key, String.valueOf(defValue));
		}

		return (T) objValue;

	}

}
