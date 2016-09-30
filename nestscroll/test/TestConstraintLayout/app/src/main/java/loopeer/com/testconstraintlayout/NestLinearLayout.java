package loopeer.com.testconstraintlayout;


import android.content.Context;
import android.support.v4.view.NestedScrollingParent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class NestLinearLayout extends LinearLayout implements NestedScrollingParent{
    public NestLinearLayout(Context context) {
        super(context);
    }

    public NestLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NestLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return true;
    }
}
