package vlatko.ivetic.androidrssreader;

import android.content.Context;
import android.content.Intent;
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
}

//    public class NewsListAdapter extends BaseAdapter {
//
//        private List<RSS_NewsModel.FeedItem> feedItemList;
//        private Context context;
//
//        public NewsListAdapter(Context context, List<RSS_NewsModel.FeedItem> feedItemList) {
//            this.feedItemList = feedItemList;
//            this.context = context;
//        }
//
//        @Override
//        public int getCount() {
//            return feedItemList.size();
//        }
//
//        @Override
//        public RSS_NewsModel.FeedItem getItem(int position) {
//            return feedItemList.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            //NewsItemView view;
//            LayoutInflater infl = LayoutInflater.from(getApplication());
//            LinearLayout layout;
//
//            /*if (convertView == null) {
//                view = new NewsItemView(context, getItem(position));
//            } else {
//                view = (NewsItemView) convertView;
//            }
//
//            view.configure(layout, feedItemList.get(position));
//            return view;*/
//
//            if (convertView == null) {
//                layout = (LinearLayout) infl.inflate(R.layout.news_item_view, null);
//            } else {
//                layout = (LinearLayout) convertView;
//            }
//
//            return configureView(context, layout, feedItemList.get(position));
//
//        }
//    }
//
//    public static View configureView(Context context, LinearLayout layout, RSS_NewsModel.FeedItem item) {
//        TextView newsDesc = (TextView) layout.findViewById(R.id.newsItemDesc);
//        ImageView newsImg = (ImageView) layout.findViewById(R.id.newsItemImg);
//        TextView newsDate = (TextView) layout.findViewById(R.id.newsItemDate);
//
//        Glide
//                .with(context)
//                .load(item.getImgURL())
//                .into(newsImg);
//        newsImg.setImageDrawable(item.getImage());
//        newsDesc.setText(item.getDescription());
//        newsDate.setText(item.getPubDate());
//
//        return layout;
//    }
//}
