package com.jcb.blocknumber;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;

public class BlockFragmentMain extends Fragment {


    private static BlockFragmentMain blockFragmentMain = null;
    private ScrollView scrollView = null;
    private Button btnBlockNumber = null;
    private CheckBox ckbxSendSms = null;
    private EditText edtSendSms = null;
    private Context mContext = null;
    /**
     * Watcher for auto reply char count
     */
    private TextWatcher CharRemainListener = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence entry, int start, int before,
                                  int count) {
            PreferenceHelper.putToPreference(mContext,
                    BlockConstants.SHRD_SMSCONTENT, String.valueOf(entry));
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable entry) {

        }
    };

    public static Fragment newInstance() {

        if (blockFragmentMain == null) {
            blockFragmentMain = new BlockFragmentMain();
        }
        return blockFragmentMain;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mContext = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.activity_main, null);

        scrollView = (ScrollView) root.findViewById(R.id.scrollMain);
        btnBlockNumber = (Button) root.findViewById(R.id.btnBlockNumber);
        ckbxSendSms = (CheckBox) root.findViewById(R.id.ckbxSendSms);
        edtSendSms = (EditText) root.findViewById(R.id.edtSendSms);

        doOnFirstLaunch();


        btnBlockNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                doOnBlockBtnClick();
                edtSendSms.setFocusable(false);
            }
        });

        ckbxSendSms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                edtSendSms.setFocusable(false);

                doOnCheckChange(isChecked);
            }
        });

        // Handling autofocus of EditText, to prevent auto scroll in Scrollview
        edtSendSms.setFocusable(false);
        edtSendSms.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {

                view.setFocusable(true);
                view.setFocusableInTouchMode(true);
                return false;
            }
        });

        edtSendSms.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                if (hasFocus) {
                    scrollView.fullScroll(View.FOCUS_DOWN);
                } else {
                    BlockUtils.hideSoftKeyBoard(view, mContext);
                }
            }
        });

        edtSendSms.addTextChangedListener(CharRemainListener);
        return root;
    }

    private void doOnBlockBtnClick() {
        boolean isBlockEnabled = PreferenceHelper.getFromPreference(mContext, BlockConstants.SHRD_KEY_BLOCK, false);

        if (isBlockEnabled) {
            PreferenceHelper.putToPreference(mContext, BlockConstants.SHRD_KEY_BLOCK, false);
            btnBlockNumber.setText(getResources().getString(R.string.block_begin));
            btnBlockNumber.setSelected(false);
        } else {
            PreferenceHelper.putToPreference(mContext, BlockConstants.SHRD_KEY_BLOCK, true);
            btnBlockNumber.setText(getResources().getString(R.string.block_end));
            btnBlockNumber.setSelected(true);


        }
    }

    private void doOnFirstLaunch() {
        boolean isBlockEnabled = PreferenceHelper.getFromPreference(mContext, BlockConstants.SHRD_KEY_BLOCK, false);

        if (isBlockEnabled) {
            btnBlockNumber.setText(getResources().getString(R.string.block_end));
            btnBlockNumber.setSelected(true);

        } else {
            btnBlockNumber.setText(getResources().getString(R.string.block_begin));
            btnBlockNumber.setSelected(false);

        }

        boolean isSmsChecked = PreferenceHelper.getFromPreference(mContext, BlockConstants.SHRD_KEY_SMS, false);

        ckbxSendSms.setChecked(isSmsChecked);
        doOnCheckChange(isSmsChecked);
    }

    private void doOnCheckChange(boolean isChecked) {

        PreferenceHelper.putToPreference(mContext, BlockConstants.SHRD_KEY_SMS, isChecked);
        if (isChecked) {
            edtSendSms.setText(PreferenceHelper.getFromPreference(mContext, BlockConstants.SHRD_SMSCONTENT, BlockConstants.STR_EMPTY));
        }
        edtSendSms.setEnabled(isChecked);

    }
}
