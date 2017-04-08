package vlatko.ivetic.androidrssreader;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vlatko on 7.4.2017..
 * Bla
 */

public class RSSViewAdapter extends RecyclerView.Adapter<RSSViewAdapter.ViewHolder> {

    private OnItemClickListener _listener;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this._listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView newsDesc;
        ImageView newsImg;
        TextView newsDate;

        public ViewHolder(final LinearLayout itemView) {
            super(itemView);

            newsDesc = (TextView) itemView.findViewById(R.id.newsItemDesc);
            newsImg = (ImageView) itemView.findViewById(R.id.newsItemImg);
            newsDate = (TextView) itemView.findViewById(R.id.newsItemDate);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (_listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            _listener.onItemClick(itemView, position);
                        }
                    }
                }
            });
        }

        public void configureView(Context context, RSS_NewsModel.FeedItem item) {
            Glide
                    .with(context)
                    .load(item.getImgURL())
                    .into(newsImg);
            newsDesc.setText(item.getDescription());
            newsDate.setText(item.getPubDate());
        }
    }

    private List<RSS_NewsModel.FeedItem> _feedItems = new ArrayList<>();
    private Context _context;

    public RSSViewAdapter(Context context) {
        this._context = context;
    }

    private Context getContext() {
        return _context;
    }

    @Override
    public RSSViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        LinearLayout newsView;
        newsView = (LinearLayout) inflater.inflate(R.layout.news_item_view, parent, false);

        return new ViewHolder(newsView);
    }

    @Override
    public void onBindViewHolder(RSSViewAdapter.ViewHolder viewHolder, int position) {
        Context context = getContext();
        RSS_NewsModel.FeedItem feedItem = _feedItems.get(position);

        viewHolder.configureView(context, feedItem);
    }

    @Override
    public int getItemCount() {
        return _feedItems.size();
    }

    public void clear() {
        _feedItems.clear();
    }

    public void addAll(List<RSS_NewsModel.FeedItem> list) {
        _feedItems.addAll(list);
        notifyDataSetChanged();
    }
}
