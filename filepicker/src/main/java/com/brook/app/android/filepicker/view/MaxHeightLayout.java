package com.brook.app.android.filepicker.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.brook.app.android.filepicker.R;
import com.brook.app.android.filepicker.util.DisplayUtil;

/**
 * @author Brook
 * @time 2020/5/25 11:31
 */
public class MaxHeightLayout extends FrameLayout {

    private int mMaxHeight;

    public MaxHeightLayout(Context context) {
        this(context, null);
    }

    public MaxHeightLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaxHeightLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();


            int childWidthSpec = 0;
            int childHeightSpec = 0;
            if (widthMode == MeasureSpec.EXACTLY) {
                childWidthSpec = widthMeasureSpec;
            } else {
                childWidthSpec = MeasureSpec.makeMeasureSpec(DisplayUtil.getScreenWidth(getContext()), MeasureSpec.EXACTLY);
            }

            if (heightMode == MeasureSpec.EXACTLY) {
                if (layoutParams.maxHeight > 1) {
                    childHeightSpec = MeasureSpec.makeMeasureSpec((int) layoutParams.maxHeight, MeasureSpec.AT_MOST);
                } else {
                    childHeightSpec = MeasureSpec.makeMeasureSpec((int) (heightSize * layoutParams.maxHeight), MeasureSpec.AT_MOST);
                }
            } else {
                if (layoutParams.maxHeight > 1) {
                    childHeightSpec = MeasureSpec.makeMeasureSpec((int) layoutParams.maxHeight, MeasureSpec.AT_MOST);
                } else {
                    childHeightSpec = MeasureSpec.makeMeasureSpec((int) (DisplayUtil.getScreenWidth(getContext()) * layoutParams.maxHeight), MeasureSpec.AT_MOST);
                }
            }

            child.measure(childWidthSpec, childHeightSpec);

            layoutParams.height = child.getMeasuredHeight();

            //            if (layoutParams.maxHeight > 0 && child.getMeasuredHeight() > layoutParams.maxHeight) {
            //                if (layoutParams.maxHeight > 1) {
            //                    layoutParams.height = (int) layoutParams.maxHeight;
            //                } else {
            //                    layoutParams.height = (int) ((getMeasuredHeight() > 0 ? getMeasuredHeight() : DisplayUtil.getScreenHeight(getContext())) * layoutParams.maxHeight);
            //                }
            //            }
        }

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    public static class LayoutParams extends FrameLayout.LayoutParams {

        float maxHeight;

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MaxHeightLayout_Layout);
            maxHeight = typedArray.getLayoutDimension(R.styleable.MaxHeightLayout_Layout_android_maxHeight, LayoutParams.WRAP_CONTENT);
            if (maxHeight <= 0) {
                maxHeight = typedArray.getFloat(R.styleable.MaxHeightLayout_Layout_maxHeightPercent, 0);
            }
            typedArray.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }
    }
}