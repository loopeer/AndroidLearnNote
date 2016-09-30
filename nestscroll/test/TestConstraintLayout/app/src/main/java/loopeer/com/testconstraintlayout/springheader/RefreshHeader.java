package loopeer.com.testconstraintlayout.springheader;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

@CoordinatorLayout.DefaultBehavior(SpringHeaderBehavior.class)
public class RefreshHeader extends FrameLayout implements SpringHeaderBehavior.SpringHeaderCallback {

    private SpringHeaderBehavior mBehavior;

    private OnRefreshListener mOnRefreshListener;

    public RefreshHeader(Context context) {
        this(context, null);
    }

    public RefreshHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        ViewGroup.LayoutParams lp = getLayoutParams();
        if (lp instanceof CoordinatorLayout.LayoutParams) {
            CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) lp).getBehavior();
            if (behavior instanceof SpringHeaderBehavior) {
                mBehavior = (SpringHeaderBehavior) behavior;
                mBehavior.setSpringHeaderCallback(this);
            }
        }
    }

    @Override
    public void onScroll(int offset, float fraction) {
    }

    @Override
    public void onStateChanged(int newState) {
        if (newState == SpringHeaderBehavior.STATE_HOVERING) {
            if (mOnRefreshListener != null) {
                mOnRefreshListener.onRefresh();
            }
        }
    }

    public void setRefreshing(boolean refreshing) {
        if (mBehavior != null) {
            mBehavior.setState(refreshing ? SpringHeaderBehavior.STATE_HOVERING
                    : SpringHeaderBehavior.STATE_COLLAPSED);
        }
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mOnRefreshListener = listener;
    }

    public interface OnRefreshListener {
        void onRefresh();
    }
}
