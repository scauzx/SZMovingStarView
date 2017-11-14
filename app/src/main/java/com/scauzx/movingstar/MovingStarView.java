package com.scauzx.movingstar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.LruCache;
import android.view.View;
import android.view.animation.LinearInterpolator;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author scauzx
 * @date 2017/9/23
 */

public class MovingStarView extends View {
    private LruCache<String,Bitmap> mBitmapCache;
    private int[] drawableIds = new int[]{R.drawable.first,R.drawable.second,R.drawable.third,R.drawable.four,R.drawable.five};
    private ArrayList<StarInfo> mStarInfos =  new ArrayList<>();
    private boolean mInited = false;
    private ValueAnimator valueAnimator;

    private static final float[][] STAR_LOCATION = new float[][] {
            {0.5f, 0.2f}, {0.68f, 0.35f}, {0.5f, 0.05f},
            {0.15f, 0.15f}, {0.5f, 0.5f}, {0.15f, 0.8f},
            {0.2f, 0.3f}, {0.77f, 0.4f}, {0.75f, 0.5f},
            {0.8f, 0.55f}, {0.9f, 0.6f}, {0.1f, 0.7f},
            {0.1f, 0.1f}, {0.7f, 0.8f}, {0.5f, 0.6f},{0.9f,0.9f}
    };


    public MovingStarView(Context context) {
        super(context);
        init();
    }


    public MovingStarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public MovingStarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void onDestory(){
        if (mBitmapCache != null) {
            if (mBitmapCache.size() > 0) {
                mBitmapCache.evictAll();
            }
            mBitmapCache = null;
        }
        if (mStarInfos != null) {
            mStarInfos.clear();
        }
    }

    public void onPause() {
        mPause = true;
    }

    public void onResume() {
        mPause = false;
    }


    private void init() {
        //app运行期间的最大内存
        int maxMemory = (int)Runtime.getRuntime().maxMemory();
        mBitmapCache = new LruCache<String,Bitmap>(maxMemory/8){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
        Bitmap bitmap;
        for (int i = 0; i < drawableIds.length; i++) {
            bitmap = ((BitmapDrawable)getResources().getDrawable(drawableIds[i])).getBitmap();
            mBitmapCache.put(String.valueOf(i),bitmap);
        }

        initStarInfo();
    }

    private void initStarInfo() {
        StarInfo starInfo;
        for (int i = 0; i < STAR_LOCATION.length ; i++) {
            starInfo = new StarInfo();
            starInfo.sizePercent = getSizePercent(0.4f,0.9f);
            starInfo.alpha = getSizePercent(0.3f, 0.8f);
            starInfo.xLocation = STAR_LOCATION[i][0];
            starInfo.yLocation = STAR_LOCATION[i][1];
            starInfo.xSpeed = getSpeed();
            starInfo.ySpeed = getSpeed();
            mStarInfos.add(starInfo);
        }
    }

    private int getSpeed(){
        Random random = new Random();
        int result;
        if (random.nextBoolean()) {
            result = -random.nextInt(5);
        } else {
            result = random.nextInt(5);
        }
        return  result;
    }


    private float getSizePercent(float start,float end){
        float nextFloat = (float) Math.random();
        if (start < nextFloat && nextFloat < end) {
            return nextFloat;
        } else {
            // 如果不处于想要的数据段，则再随机一次，因为不断递归有风险
            return (float) Math.random();
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {

        Bitmap bitmap;
        Bitmap backBitmap = ((BitmapDrawable)getResources().getDrawable(R.drawable.back)).getBitmap();
        canvas.drawBitmap(backBitmap,0,0,null);
        Paint paint = new Paint();
        Rect dst = new Rect();
        Rect src = new Rect();
        StarInfo starInfo;
        for (int i = 0; i < STAR_LOCATION.length; i++) {
            starInfo = mStarInfos.get(i);
            if (!mInited) {
                starInfo.xLocation = getWidth()*starInfo.xLocation;
                starInfo.yLocation = getHeight()*starInfo.yLocation;
            }
            if (starInfo.bitmap == null || starInfo.bitmap.get() == null) {
                bitmap = Bitmap.createScaledBitmap( mBitmapCache.get(String.valueOf(i%5)),(int)( mBitmapCache.get(String.valueOf(i%5)).getWidth() * starInfo.sizePercent),(int)( mBitmapCache.get(String.valueOf(i%5)).getHeight() * starInfo.sizePercent),true);
                starInfo.bitmap = new WeakReference<>(bitmap);
            }
            paint.setAlpha((int)(mStarInfos.get(i).alpha * 255));
            src.left = (int)starInfo.xLocation;
            src.top = (int)starInfo.yLocation;
            src.right = src.left + starInfo.bitmap.get().getWidth();
            src.bottom = src.top + starInfo.bitmap.get().getHeight();
            dst.left = 0;
            dst.top = 0;
            dst.right = dst.left + starInfo.bitmap.get().getWidth();
            dst.bottom = dst.top + starInfo.bitmap.get().getHeight();
            canvas.drawBitmap(starInfo.bitmap.get(),dst,src,paint);

        }
        if (!mInited) {
            startMoving();
            mInited = true;
        }
    }


    private void resetDraw(){
        if (mStarInfos == null) {
            return;
        }
        StarInfo starInfo;
        for (int i = 0; i < mStarInfos.size(); i++) {
            starInfo = mStarInfos.get(i);
            starInfo.xLocation += starInfo.xSpeed;
            starInfo.yLocation += starInfo.ySpeed;
            starInfo.xLocation %= getWidth();
            starInfo.yLocation %= getHeight();
        }

    }

    /**
     * Activity是否处于onPause状态，是的话就不需要进行计算绘制
     */
    public boolean mPause = false;

    private void startMoving(){
        valueAnimator = ValueAnimator.ofFloat(0,1f);
        valueAnimator.setDuration(1000);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setTarget(this);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (!mPause) {
                    resetDraw();
                    postInvalidate();
                }
            }
        });
        valueAnimator.start();

    }

    class StarInfo{
        /**
         * 缩放比例
         */
        float sizePercent;

        /**
         * x位置
         */
        float xLocation;

        /**
         * y位置
         */
        float yLocation;

        /**
         * 透明度
         */
        float alpha;

        /**
         *  x轴速度
         */
        int xSpeed;

        /**
         *  y轴速度
         */
        int ySpeed;

        /**
         * 显示的图片
         */
        WeakReference<Bitmap> bitmap;
    }

}
