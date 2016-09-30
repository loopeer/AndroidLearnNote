package loopeer.com.testconstraintlayout;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TestSwipeRecyclerActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.swipe_recycler_activity);

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresher);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_content);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view;
                LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                if (viewType == R.layout.list_item_swipe_recycler) {
                    view = layoutInflater.inflate(R.layout.list_item_swipe_recycler, parent, false);
                    return new ItemRecyclerHolder(view);
                } else {
                    view = layoutInflater.inflate(R.layout.list_item_swipe_recycler_normal, parent, false);
                    return new ItemNormalHolder(view);
                }
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                if (holder instanceof ItemRecyclerHolder) {
                    ((ItemRecyclerHolder) holder).bind();
                }
            }

            @Override
            public int getItemViewType(int position) {
                if (position == 0) return R.layout.list_item_swipe_recycler;
                return R.layout.list_item_swipe_recycler_normal;
            }

            @Override
            public int getItemCount() {
                return 20;
            }
        });
    }

    public static class ItemRecyclerHolder extends RecyclerView.ViewHolder {

        RecyclerView itemRecyclerView;

        public ItemRecyclerHolder(View itemView) {
            super(itemView);
            itemRecyclerView = (RecyclerView) itemView.findViewById(R.id.recycler_item);
        }

        public void bind() {
            itemRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            itemRecyclerView.setAdapter(new RecyclerView.Adapter() {
                @Override
                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    return new TestHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_recycler_in_item, parent, false));
                }

                @Override
                public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

                }

                @Override
                public int getItemCount() {
                    return 20;
                }
            });
        }

        public static class TestHolder extends RecyclerView.ViewHolder {

            public TestHolder(View itemView) {
                super(itemView);
            }
        }

    }

    public static class ItemNormalHolder extends RecyclerView.ViewHolder {

        public ItemNormalHolder(View itemView) {
            super(itemView);
        }
    }
}
