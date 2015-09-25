package com.jcb.blocknumber;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class BlockPagerAdapter extends FragmentPagerAdapter {

    private Context _context;

    public BlockPagerAdapter(FragmentManager fm) {
        super(fm);

    }

    public BlockPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        _context = context;

    }

    @Override
    public Fragment getItem(int position) {
        Fragment f = new Fragment();
        switch (position) {
            case 0:
                f = BlockFragmentMain.newInstance();
                break;
            case 1:
                f = BlockFragmentNumber.newInstance();
                break;
        }
        return f;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
