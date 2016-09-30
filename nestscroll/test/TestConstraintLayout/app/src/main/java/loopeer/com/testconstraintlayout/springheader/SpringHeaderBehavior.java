package loopeer.com.testconstraintlayout.springheader;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import loopeer.com.testconstraintlayout.R;

public class SpringHeaderBehavior extends ViewOffsetBehavior<View> {

    private static final int UNSET = Integer.MIN_VALUE;

    public static final int STATE_COLLAPSED = 1;
    public static final int STATE_HOVERING = 2;
    public static final int STATE_DRAGGING = 3;
    public static final int STATE_SETTLING = 4;

    private int mState = STATE_COLLAPSED;

    private SpringHeaderCallback mCallback;

    private float mTotalUnconsumed;

    private int mOriginalOffset = UNSET;
    private int mHoveringRange = UNSET;
    private int mMaxRange = UNSET;
    private int mHoveringOffset;

    private boolean mOriginalOffsetSet;

    private ValueAnimator mAnimator;
    private EndListener mEndListener;

    public SpringHeaderBehavior() {
    }

    public SpringHeaderBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SpringHeaderBehavior_Params);
        setOriginalOffset(a.getDimensionPixelSize(
                R.styleable.SpringHeaderBehavior_Params_behavior_originalOffset, UNSET));
        setHoveringRange(a.getDimensionPixelSize(
                R.styleable.SpringHeaderBehavior_Params_behavior_hoveringRange, UNSET));
        setMaxRange(a.getDimensionPixelSize(
                R.styleable.SpringHeaderBehavior_Params_behavior_maxRange, UNSET));
        a.recycle();
    }

    public void setOriginalOffset(int originalOffset) {
        mOriginalOffset = originalOffset;
    }

    public void setHoveringRange(int hoveringRange) {
        mHoveringRange = hoveringRange;
        mHoveringOffset = mOriginalOffset + mHoveringRange;
    }

    public void setMaxRange(int maxRange) {
        mMaxRange = maxRange;
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, View child, int layoutDirection) {
        boolean handled = super.onLayoutChild(parent, child, layoutDirection);

        int parentHeight = parent.getHeight();
        int childHeight = child.getHeight();

        if (mOriginalOffset == UNSET) {
            setOriginalOffset(-childHeight);
        }
        if (mHoveringRange == UNSET) {
            setHoveringRange(childHeight);
        }
        if (mMaxRange == UNSET) {
            setMaxRange(parentHeight);
        }

        if (!mOriginalOffsetSet) {
            super.setTopAndBottomOffset(mOriginalOffset);
            mOriginalOffsetSet = true;
        }

        return handled;
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child,
                                       View directTargetChild, View target, int nestedScrollAxes) {
        boolean started = (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0
                && mState != STATE_HOVERING;
        if (started && mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
        }
        return started;
    }

    @Override
    public void onNestedScrollAccepted(CoordinatorLayout coordinatorLayout, View child,
                                       View directTargetChild, View target, int nestedScrollAxes) {
        mTotalUnconsumed = calculateScrollUnconsumed();
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target,
                                  int dx, int dy, int[] consumed) {
        if (dy > 0 && mTotalUnconsumed > 0) {
            if (dy > mTotalUnconsumed) {
                consumed[1] = dy - (int) mTotalUnconsumed;
                mTotalUnconsumed = 0;
            } else {
                mTotalUnconsumed -= dy;
                consumed[1] = dy;
            }
            setTopAndBottomOffset(calculateScrollOffset());
            setStateInternal(STATE_DRAGGING);
        }
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target,
                               int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        if (dyUnconsumed < 0) {
            mTotalUnconsumed -= dyUnconsumed;
            setTopAndBottomOffset(calculateScrollOffset());
            setStateInternal(STATE_DRAGGING);
        }
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target) {
        animateOffsetToState(getTopAndBottomOffset() >= mHoveringOffset
                ? STATE_HOVERING : STATE_COLLAPSED);
    }

    private void animateOffsetToState(int endState) {
        int from = getTopAndBottomOffset();
        int to = endState == STATE_HOVERING ? mHoveringOffset : mOriginalOffset;
        if (from == to) {
            setStateInternal(endState);
            return;
        } else {
            setStateInternal(STATE_SETTLING);
        }

        if (mAnimator == null) {
            mAnimator = new ValueAnimator();
            mAnimator.setDuration(200);
            mAnimator.setInterpolator(new DecelerateInterpolator());
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    setTopAndBottomOffset((int) animation.getAnimatedValue());
                }
            });
            mEndListener = new EndListener(endState);
            mAnimator.addListener(mEndListener);
        } else {
            if (mAnimator.isRunning()) {
                mAnimator.cancel();
            }
            mEndListener.setEndState(endState);
        }
        mAnimator.setIntValues(from, to);
        mAnimator.start();
    }

    @Override
    public boolean setTopAndBottomOffset(int offset) {
        if (mCallback != null) {
            mCallback.onScroll(offset, (float) (offset - mOriginalOffset) / mHoveringRange);
        }
        return super.setTopAndBottomOffset(offset);
    }

    private void setStateInternal(int state) {
        if (state == mState) {
            return;
        }
        mState = state;
        if (mCallback != null) {
            mCallback.onStateChanged(state);
        }
    }

    public void setState(int state) {
        if (state != STATE_COLLAPSED && state != STATE_HOVERING) {
            throw new IllegalArgumentException("Illegal state argument: " + state);
        } else if (state != mState) {
            animateOffsetToState(state);
        }
    }

    private int calculateScrollOffset() {
        return (int) (mMaxRange * (1 - Math.exp(-(mTotalUnconsumed / mMaxRange / 2))))
                + mOriginalOffset;
    }

    private int calculateScrollUnconsumed() {
        return (int) (-Math.log(1 - (float) getCurrentRange() / mMaxRange) * mMaxRange * 2);
    }

    public int getCurrentRange() {
        return getTopAndBottomOffset() - mOriginalOffset;
    }

    public void setSpringHeaderCallback(SpringHeaderCallback callback) {
        mCallback = callback;
    }

    public interface SpringHeaderCallback {
        void onScroll(int offset, float fraction);

        void onStateChanged(int newState);
    }

    private class EndListener extends AnimatorListenerAdapter {

        private int mEndState;
        private boolean mCanceling;

        public EndListener(int endState) {
            mEndState = endState;
        }

        public void setEndState(int finalState) {
            mEndState = finalState;
        }

        @Override
        public void onAnimationStart(Animator animation) {
            mCanceling = false;
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            mCanceling = true;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (!mCanceling) {
                setStateInternal(mEndState);
            }
        }
    }
}
