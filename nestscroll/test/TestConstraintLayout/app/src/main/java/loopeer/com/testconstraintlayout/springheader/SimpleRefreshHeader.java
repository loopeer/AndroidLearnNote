package loopeer.com.testconstraintlayout.springheader;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import loopeer.com.testconstraintlayout.R;

public class SimpleRefreshHeader extends RefreshHeader {

    private TextView mText;
    private MaterialProgressDrawable mProgress;

    private CharSequence mTextBelowThreshold;
    private CharSequence mTextAboveThreshold;
    private CharSequence mTextRefreshing;

    private int mOldState;

    private boolean mBelowThreshold;

    public SimpleRefreshHeader(Context context) {
        this(context, null);
    }

    public SimpleRefreshHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleRefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        inflate(context, R.layout.refresh_header_simple, this);

        ImageView icon = (ImageView) findViewById(android.R.id.icon);
        mText = (TextView) findViewById(android.R.id.text1);

        mProgress = new MaterialProgressDrawable(getContext(), this);
        mProgress.setAlpha(255);
        icon.setImageDrawable(mProgress);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SimpleRefreshHeader, defStyleAttr, 0);
        CharSequence text = a.getText(R.styleable.SimpleRefreshHeader_textBelowThreshold);
        if (text != null) {
            setTextBelowThreshold(text);
        } else {
            setTextBelowThreshold(R.string.simple_refresh_header_text_below_threshold);
        }
        text = a.getText(R.styleable.SimpleRefreshHeader_textAboveThreshold);
        if (text != null) {
            setTextAboveThreshold(text);
        } else {
            setTextAboveThreshold(R.string.simple_refresh_header_text_above_threshold);
        }
        text = a.getText(R.styleable.SimpleRefreshHeader_textRefreshing);
        if (text != null) {
            setTextRefreshing(text);
        } else {
            setTextRefreshing(R.string.simple_refresh_header_text_refreshing);
        }
        a.recycle();
    }

    public void setColorSchemeResources(@ColorRes int... colorResIds) {
        int[] colorRes = new int[colorResIds.length];
        for (int i = 0; i < colorResIds.length; i++) {
            colorRes[i] = ContextCompat.getColor(getContext(), colorResIds[i]);
        }
        setColorSchemeColors(colorRes);
    }

    public void setColorSchemeColors(@ColorInt int... colors) {
        mProgress.setColorSchemeColors(colors);
    }

    public void setTextBelowThreshold(CharSequence textBelowThreshold) {
        mTextBelowThreshold = textBelowThreshold;
    }

    public void setTextBelowThreshold(@StringRes int textBelowThreshold) {
        mTextBelowThreshold = getResources().getText(textBelowThreshold);
    }

    public void setTextAboveThreshold(CharSequence textAboveThreshold) {
        mTextAboveThreshold = textAboveThreshold;
    }

    public void setTextAboveThreshold(@StringRes int textAboveThreshold) {
        mTextAboveThreshold = getResources().getText(textAboveThreshold);
    }

    public void setTextRefreshing(CharSequence textRefreshing) {
        mTextRefreshing = textRefreshing;
    }

    public void setTextRefreshing(@StringRes int textRefreshing) {
        mTextRefreshing = getResources().getText(textRefreshing);
    }

    @Override
    public void onScroll(int offset, float fraction) {
        super.onScroll(offset, fraction);

        boolean belowThreshold = fraction < 1;
        if (belowThreshold != mBelowThreshold) {
            mBelowThreshold = belowThreshold;
            mText.setText(belowThreshold ? mTextBelowThreshold : mTextAboveThreshold);
        }

        mProgress.showArrow(true);
        float clampedFraction = Math.min(1, fraction);
        mProgress.setStartEndTrim(0, 0.8f * clampedFraction);
        mProgress.setArrowScale(clampedFraction);
        mProgress.setProgressRotation(fraction + 0.1f);
    }

    @Override
    public void onStateChanged(int newState) {
        super.onStateChanged(newState);
        if (newState == SpringHeaderBehavior.STATE_HOVERING) {
            mText.setText(mTextRefreshing);
            mProgress.start();
        } else if (mOldState == SpringHeaderBehavior.STATE_HOVERING) {
            mProgress.stop();
        }
        mOldState = newState;
    }
}
