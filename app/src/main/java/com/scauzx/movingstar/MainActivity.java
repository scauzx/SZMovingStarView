package com.scauzx.movingstar;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

/**
 *
 * @author scauzx
 * @date 2017/9/30
 */

public class MainActivity extends Activity {
    private MovingStarView mMovingStarView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMovingStarView = new MovingStarView(this);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setContentView(mMovingStarView,layoutParams);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMovingStarView != null) {
            mMovingStarView.onDestory();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMovingStarView != null) {
            mMovingStarView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMovingStarView != null) {
            mMovingStarView.onPause();
        }
    }
}
