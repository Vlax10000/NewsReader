package vlatko.ivetic.androidrssreader;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

public class RSSReader extends AppCompatActivity {

    private final String _rssURL = "http://www.24sata.hr/feeds/najnovije.xml";
    private RSS_NewsModel _newsModel = null;

    private SwipeRefreshLayout _container;
    private RecyclerView _newsView = null;
    private RSSViewAdapter _newsAdapter = null;

    public static final String EXTRA_LINK = "vlatko.ivetic.androidrssreader.LINK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rssviewer);

        _newsModel = new RSS_NewsModel(_rssURL);

        _container = (SwipeRefreshLayout) findViewById(R.id.main_layout);
        _container.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new NewsFeedLoader().execute();
            }
        });

        _newsAdapter = new RSSViewAdapter(RSSReader.this);
        _newsView = (RecyclerView) findViewById(R.id.recycledNews);
        _newsView.setAdapter(_newsAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.scrollToPosition(0);
        _newsView.setLayoutManager(layoutManager);

        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        _newsView.addItemDecoration(itemDecoration);

        _newsAdapter.setOnItemClickListener(new RSSViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(RSSReader.this, RSSWebViewActivity.class);

                intent.putExtra(EXTRA_LINK, _newsModel.getItem(position).getLink());
                startActivity(intent);
            }
        });

        new NewsFeedLoader().execute();

    }


    public class NewsFeedLoader extends AsyncTask<Void, Void, List<RSS_NewsModel.FeedItem>> {

        @Override
        protected List<RSS_NewsModel.FeedItem> doInBackground(Void... params) {
            return _newsModel.updateNews();
        }

        @Override
        protected void onPostExecute(List<RSS_NewsModel.FeedItem> list) {
            if (!newsFeedEmpty()) {
                _newsAdapter.clear();
                _newsAdapter.addAll(list);
                _container.setRefreshing(false);
            } else {
                Toast.makeText(RSSReader.this, "Network error!", Toast.LENGTH_LONG).show();
            }
        }
    }

    public boolean newsFeedEmpty() {
        return _newsModel.getSize() == 0;
    }

    public class DividerItemDecoration extends RecyclerView.ItemDecoration {

        private final int[] ATTRS = new int[]{
                android.R.attr.listDivider
        };

        public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;

        public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

        private Drawable mDivider;

        private int mOrientation;

        public DividerItemDecoration(Context context, int orientation) {
            final TypedArray a = context.obtainStyledAttributes(ATTRS);
            mDivider = a.getDrawable(0);
            a.recycle();
            setOrientation(orientation);
        }

        public void setOrientation(int orientation) {
            if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
                throw new IllegalArgumentException("invalid orientation");
            }
            mOrientation = orientation;
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent) {
            if (mOrientation == VERTICAL_LIST) {
                drawVertical(c, parent);
            } else {
                drawHorizontal(c, parent);
            }
        }

        public void drawVertical(Canvas c, RecyclerView parent) {
            final int left = parent.getPaddingLeft();
            final int right = parent.getWidth() - parent.getPaddingRight();

            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int top = child.getBottom() + params.bottomMargin;
                final int bottom = top + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }

        public void drawHorizontal(Canvas c, RecyclerView parent) {
            final int top = parent.getPaddingTop();
            final int bottom = parent.getHeight() - parent.getPaddingBottom();

            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int left = child.getRight() + params.rightMargin;
                final int right = left + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }

        @Override
        public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
            if (mOrientation == VERTICAL_LIST) {
                outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
            } else {
                outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
            }
        }
    }
}
