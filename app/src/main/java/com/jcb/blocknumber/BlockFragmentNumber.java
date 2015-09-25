package com.jcb.blocknumber;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class BlockFragmentNumber extends Fragment {


    private static BlockFragmentNumber blockFragmentNumber = null;
    private Button btnSaveNumber = null;
    private EditText edtSaveNumber = null;
    private ListView lstvBlock = null;
    private Context mContext = null;
    private ArrayAdapter<String> listAdapter = null;


    public static Fragment newInstance() {
        if (blockFragmentNumber == null) {
            blockFragmentNumber = new BlockFragmentNumber();
        }
        return blockFragmentNumber;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mContext = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.layout_blocklist, null);

        btnSaveNumber = (Button) root.findViewById(R.id.btnEnterNumber);

        edtSaveNumber = (EditText) root.findViewById(R.id.edtEnterNumber);
        lstvBlock = (ListView) root.findViewById(R.id.lstvBlockList);

        btnSaveNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doOnSaveNumberClick();
            }
        });
        lstvBlock.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                doOnListItemClick(position);
            }
        });

        // Handling autofocus of EditText, to prevent auto scroll in Scrollview
        edtSaveNumber.setFocusable(false);
        edtSaveNumber.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {

                view.setFocusable(true);
                view.setFocusableInTouchMode(true);
                return false;
            }
        });

        listAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, BlockConstants.listBlockContacts);
        lstvBlock.setAdapter(listAdapter);

        return root;
    }

    private void doOnSaveNumberClick() {

        String number = String.valueOf(edtSaveNumber.getText());
        edtSaveNumber.setText(BlockConstants.STR_EMPTY);

        if (number.isEmpty()) {

            BlockUtils.showToast(mContext, "Kindly enter a number");

        } else {
            String input = BlockUtils.getFinalBlockString(number);

            if (BlockUtils.isContactInBlockList(number)) {

                BlockUtils.showToast(mContext, "Number already added");

            } else {
                BlockConstants.listBlockContacts.add(input);

                BlockUtils.saveBlockToShared(mContext);

                listAdapter.notifyDataSetChanged();
            }
        }

        BlockUtils.hideSoftKeyBoard(edtSaveNumber, mContext);

    }

    private void doOnListItemClick(int position) {

        BlockConstants.listBlockContacts.remove(position);

        BlockUtils.saveBlockToShared(mContext);

        listAdapter.notifyDataSetChanged();
    }


}
