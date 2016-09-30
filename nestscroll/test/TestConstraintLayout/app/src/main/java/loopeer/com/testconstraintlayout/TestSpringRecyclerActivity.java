package loopeer.com.testconstraintlayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import loopeer.com.testconstraintlayout.springheader.RefreshHeader;
import loopeer.com.testconstraintlayout.springheader.SimpleRefreshHeader;

public class TestSpringRecyclerActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_spring_recycler_view);

        final SimpleRefreshHeader header = (SimpleRefreshHeader) findViewById(R.id.header);
        header.setOnRefreshListener(new RefreshHeader.OnRefreshListener() {
            @Override
            public void onRefresh() {
                header.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        header.setRefreshing(false);
                    }
                }, 2000);
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
                    return new TestSwipeRecyclerActivity.ItemRecyclerHolder(view);
                } else {
                    view = layoutInflater.inflate(R.layout.list_item_swipe_recycler_normal, parent, false);
                    return new TestSwipeRecyclerActivity.ItemNormalHolder(view);
                }
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                if (holder instanceof TestSwipeRecyclerActivity.ItemRecyclerHolder) {
                    ((TestSwipeRecyclerActivity.ItemRecyclerHolder) holder).bind();
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
                    return new TestSwipeRecyclerActivity.ItemRecyclerHolder.TestHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_recycler_in_item, parent, false));
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
