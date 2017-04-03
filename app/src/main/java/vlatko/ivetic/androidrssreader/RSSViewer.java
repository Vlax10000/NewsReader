package vlatko.ivetic.androidrssreader;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class RSSViewer extends AppCompatActivity {

    private final String _rssURL = "http://www.24sata.hr/feeds/najnovije.xml";
    private RSS_NewsModel _newsModel = new RSS_NewsModel(_rssURL);

    private Button refreshBtn = null;

    private ListView newsView = null;
    private NewsListAdapter listAdapter = null;
    private List<RSS_NewsModel.FeedItem> data = null;

    private RSSViewer creator = this;

    public static final String EXTRA_LINK = "vlatko.ivetic.androidrssreader.LINK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rssviewer);

        refreshBtn = (Button) findViewById(R.id.refresh_btn);

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NewsFeedLoader().execute(_rssURL);
            }
        });
        refreshBtn.callOnClick();


    }


    public class NewsFeedLoader extends AsyncTask<String, Void, List> {

        @Override
        protected List<RSS_NewsModel.FeedItem> doInBackground(String... params) {
            return _newsModel.updateNews();
        }

        @Override
        protected void onPostExecute(List list) {
            data = list;

            if (!newsFeedEmpty()) {
                listAdapter = new NewsListAdapter(RSSViewer.this, list);

                newsView = (ListView) findViewById(R.id.newsList);
                newsView.setAdapter(listAdapter);
                newsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(creator, RSSViewActivity.class);

                        intent.putExtra(EXTRA_LINK, parent.getItemAtPosition(position).toString());
                        startActivity(intent);
                    }
                });
            }
        }
    }

    public boolean newsFeedEmpty() {
        return _newsModel.getSize() == 0;
    }


    public class NewsListAdapter extends BaseAdapter {

        private List<RSS_NewsModel.FeedItem> feedItemList;
        private Context context;

        public NewsListAdapter(Context context, List<RSS_NewsModel.FeedItem> feedItemList) {
            this.feedItemList = feedItemList;
            this.context = context;
        }

        @Override
        public int getCount() {
            return feedItemList.size();
        }

        @Override
        public RSS_NewsModel.FeedItem getItem(int position) {
            return feedItemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //NewsItemView view;
            LayoutInflater infl = LayoutInflater.from(getApplication());
            LinearLayout layout;

            /*if (convertView == null) {
                view = new NewsItemView(context, getItem(position));
            } else {
                view = (NewsItemView) convertView;
            }

            view.configure(layout, feedItemList.get(position));
            return view;*/

            if(convertView==null){
                layout = (LinearLayout) infl.inflate(R.layout.news_item_view, null);
            } else {
                layout=(LinearLayout) convertView;
            }

            return configureView(layout, feedItemList.get(position));

        }
    }


    public class NewsItemView extends LinearLayout {

        TextView newsDesc;
        ImageView newsImg;
        TextView newsDate;

        public NewsItemView(Context context, RSS_NewsModel.FeedItem item) {
            super(context);
            //LayoutParams descParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            //LayoutParams imgParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            //LayoutParams dateParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

            newsDesc = (TextView) findViewById(R.id.newsItemDesc);
            newsImg = (ImageView) findViewById(R.id.newsItemImg);
            newsDate = (TextView) findViewById(R.id.newsItemDate);

            /*newsDesc.setLayoutParams(descParams);
            newsDate.setLayoutParams(dateParams);
            newsImg.setLayoutParams(imgParams);*/

            //newsDesc.setText(item.getDescription());
            //newsDate.setText(item.getPubDate());
            //newsImg.setImageDrawable(item.getImage());

            Log.d("overlap", Boolean.toString(hasOverlappingRendering()));
            addView(newsDesc);
            addView(newsDate);
            addView(newsImg);
        }

        public void configure(LinearLayout layout, RSS_NewsModel.FeedItem item) {
            newsImg.setImageDrawable(item.getImage());
            newsDesc.setText(item.getDescription());
            newsDate.setText(item.getPubDate());
        }
    }

    public static View configureView(LinearLayout layout, RSS_NewsModel.FeedItem item) {
        TextView newsDesc = (TextView) layout.findViewById(R.id.newsItemDesc);
        ImageView newsImg = (ImageView) layout.findViewById(R.id.newsItemImg);
        TextView newsDate = (TextView) layout.findViewById(R.id.newsItemDate);

        newsImg.setImageDrawable(item.getImage());
        newsDesc.setText(item.getDescription());
        newsDate.setText(item.getPubDate());

        return layout;
    }
}
