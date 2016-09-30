package loopeer.com.testconstraintlayout.springheader;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

public class DependentViewBehavior extends ViewOffsetBehavior<View> {

    public DependentViewBehavior() {
    }

    public DependentViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        CoordinatorLayout.Behavior behavior = getBehavior(dependency);
        return behavior instanceof SpringHeaderBehavior;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        int offset = ((SpringHeaderBehavior) getBehavior(dependency)).getCurrentRange();
        return setTopAndBottomOffset(offset);
    }

    private CoordinatorLayout.Behavior getBehavior(View view) {
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) view.getLayoutParams();
        return lp.getBehavior();
    }
}
