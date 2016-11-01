package cn.jit.zoomlistview.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * 下拉缩放头布局的listView
 */

public class ZoomListView extends ListView {

    private ImageView mImage;
    private int mImageHeight; //图片原始高度
    private int zoomMaxHeight; //图片可拉伸的最大高度

    public ZoomListView(Context context) {
        this(context,null);
    }

    public ZoomListView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ZoomListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setZoomImage(ImageView view){
        this.mImage = view;
        mImageHeight = mImage.getHeight();
        zoomMaxHeight = mImageHeight * 2; //默认指定图片最大拉伸到原图片高度的二倍
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int
            scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean
            isTouchEvent) {
        //当是触摸滑动,并且是下拉时
        if (isTouchEvent && deltaY < 0){
            if (mImage.getHeight() <= zoomMaxHeight){ //如果图片的高度小于可拉伸的最大高度
                //除以3.0是为了 比如你滑动15像素，image高度只增加5像素，形成视差特效
                int newHeight = (int) (mImage.getHeight() + Math.abs(deltaY / 3.0f));
                mImage.getLayoutParams().height = newHeight;
                mImage.requestLayout(); //动态改变图片高度

                float mPercent = scalePercent(newHeight);
                mImage.setScaleX(mPercent);
                mImage.setScaleY(mPercent); //动态改变图片缩放
            }
        }

        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY,
                maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    /**
     * 根据当前高度计算图片缩放比例 0.5f 最大放大1.5倍
     * 1.0f最大放大2.0倍 自己控制
     */
    private float scalePercent(int height){
        return (height-mImageHeight) * 0.5f / (zoomMaxHeight -mImageHeight) + 1;
    }

    /**
     * 手指抬起时 执行回到初始状态的动画
     * 包括图片的高度回到初始状态 图片缩放回到初始状态
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_UP:
                final int startHeight = mImage.getHeight();
                final int endHeight = mImageHeight;
                ValueAnimator mValueAnim = ValueAnimator.ofInt(1);
                mValueAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator mAnim) {
                        float fraction = mAnim.getAnimatedFraction();
                        Integer newHeight = evaluate(fraction, startHeight, endHeight);
                        mImage.getLayoutParams().height = newHeight;
                        mImage.requestLayout(); //执行动画过程中高度回到初始状态

                        Float aFloat = evaluatescale(fraction, scalePercent(startHeight), 1);
                        mImage.setScaleX(aFloat);
                        mImage.setScaleY(aFloat); //执行动画过程中缩放回到初始状态
                    }
                });
                mValueAnim.setDuration(300);
                mValueAnim.start();
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 估值器
     * @param fraction 比例
     * @param startValue 开始值
     * @param endValue 结束值
     * @return 根据比例计算的中间值
     */
    public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
        int startInt = startValue;
        return (int)(startInt + fraction * (endValue - startInt));
    }

    public Float evaluatescale(float fraction, Number startValue, Number endValue) {
        float startFloat = startValue.floatValue();
        return startFloat + fraction * (endValue.floatValue() - startFloat);
    }

}
