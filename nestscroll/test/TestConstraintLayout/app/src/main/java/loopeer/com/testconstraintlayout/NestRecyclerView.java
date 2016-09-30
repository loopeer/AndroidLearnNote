package loopeer.com.testconstraintlayout;


import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class NestRecyclerView extends RecyclerView {
    public NestRecyclerView(Context context) {
        super(context);
    }

    public NestRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NestRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
/*

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        boolean result = super.dispatchTouchEvent(e);
        if (e.getAction() == MotionEvent.ACTION_CANCEL) {
            startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
        }
        return result;
    }
*/

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        boolean result = super.onTouchEvent(e);
        if (e.getAction() == MotionEvent.ACTION_CANCEL) {
            startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
        }
        return result;
    }
}
