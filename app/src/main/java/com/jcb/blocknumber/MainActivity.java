package com.jcb.blocknumber;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;


public class MainActivity extends FragmentActivity {


    private ViewPager vwPager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);

        if (PreferenceHelper.getFromPreference(MainActivity.this, BlockConstants.SHRD_FIRSTTIME, true)) {
            BlockUtils.showCustomToast(MainActivity.this);
        }
        PreferenceHelper.putToPreference(MainActivity.this, BlockConstants.SHRD_FIRSTTIME, false);

        vwPager = (ViewPager) findViewById(R.id.viewpager);

        vwPager.setAdapter(new BlockPagerAdapter(getSupportFragmentManager()));
        vwPager.setCurrentItem(0);
        vwPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                BlockUtils.hideSoftKeyBoardFromWindow(MainActivity.this);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        BlockUtils.getBlockListFromShared(MainActivity.this);

    }

    @Override
    public void onBackPressed() {

        if (vwPager.getCurrentItem() > 0) {

            vwPager.setCurrentItem(0);
        } else {
            super.onBackPressed();
        }

    }
}
