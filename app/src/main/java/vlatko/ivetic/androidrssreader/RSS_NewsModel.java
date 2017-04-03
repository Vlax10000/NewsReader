package vlatko.ivetic.androidrssreader;


import android.graphics.drawable.Drawable;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vlatko on 26.3.2017..
 */
public class RSS_NewsModel {

    private ArrayList<FeedItem> newsFeed = null;

    private String _channel = null;
    private String _title = null;
    private String _link = null;
    private String _description = null;
    private String _publishDate = null;
    private String _urlString = null;
    private XmlPullParserFactory _xmlFactoryObject = null;
    private volatile boolean _parsingComplete = false;
    private static final int URL_BEGIN = 5;
    private static final int URL_END = 5;

    public RSS_NewsModel(String url) {
        this._urlString = url;
    }

    public String get_channel() {
        return _channel;
    }

    public String getTitle() {
        return _title;
    }

    public String getLink() {
        return _link;
    }

    public String getDescription() {
        return _description;
    }

    public int getSize() {
        return newsFeed.size();
    }

    public FeedItem getItem(int itemIndex) {
        return newsFeed.get(itemIndex);
    }

    public List<FeedItem> updateNews() {
        this.newsFeed = new ArrayList<>();
        _parsingComplete = false;
        fetchAndParseXML();

        for (FeedItem item : newsFeed) {
            extractDescription(item);
            loadImage(item);
        }

        return newsFeed;
    }

    private void extractDescription(FeedItem feedItem) {
        Log.i("desc", feedItem.description);
        // full description tag text is logged under regex "desc"
        // this method splits its contents into FeedItem class attributes

        String[] descrStrParts = feedItem.description.split(" /> ");
        String[] imgProperties = descrStrParts[0].split(" ");

        // all magic numbers are calculated by optical examination of the log that is,
        // unfortunately, specific for this feed
        feedItem.imgURL = imgProperties[1].substring(5, imgProperties[1].length() - 1);
        feedItem.description = descrStrParts[1];

        String tmpDate = feedItem.pubDate.split(":")[0];
        feedItem.pubDate = tmpDate.substring(0, tmpDate.length()-2);
    }

    private void loadImage(FeedItem item) {
        try {
            InputStream is = (InputStream) new URL(item.imgURL).getContent();
            item.img = Drawable.createFromStream(is, "src");
        } catch (Exception e) {
            Log.d("img", "Loading image failed!");
        }
    }


    public void fetchAndParseXML() {
        try {
            URL url = new URL(_urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            // Starts the query
            conn.connect();
            InputStream stream = conn.getInputStream();

            _xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser myparser = _xmlFactoryObject.newPullParser();

            myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            myparser.setInput(stream, null);

            parseXML(myparser);
            stream.close();
        } catch (Exception e) {
            Log.d("Exception raised.", "Parsing failed");
            e.printStackTrace();
        }
    }

    public void parseXML(XmlPullParser myParser) {
        int event;
        String text = null;
        FeedItem item = null;

        try {
            event = myParser.getEventType();

            while (event != XmlPullParser.END_DOCUMENT) {
                String name = myParser.getName();

                switch (event) {
                    case XmlPullParser.START_TAG:
                        if (name.equals("item")) {
                            item = new FeedItem();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = myParser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (item != null) {
                            if (name.equals("title")) {
                                item.title = text;
                            } else if (name.equals("link")) {
                                item.link = text;
                            } else if (name.equals("pubDate")) {
                                item.pubDate = text;
                            } else if (name.equals("description")) {
                                item.description = text;
                            } else if (name.equals("item")) {
                                newsFeed.add(item);
                            } else {
                            }
                        } else {
                            if (name.equals("title")) {
                                _title = text;
                            } else if (name.equals("link")) {
                                _link = text;
                            } else if (name.equals("pubDate")) {
                                _publishDate = text;
                            } else if (name.equals("description")) {
                                _description = text;
                            } else {
                            }
                        }

                        break;
                }

                event = myParser.next();
            }

            _parsingComplete = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Class representing one item in the feed
     */
    public class FeedItem {
        private String title = null;
        private String link = null;
        private String description = null;
        private String pubDate = null;
        private String imgURL = null;

        private Drawable img = null;

        public String getTitle() {
            return title;
        }

        public String getLink() {
            return link;
        }

        public String getDescription() {
            return description;
        }

        public String getPubDate() {
            return pubDate;
        }

        public String getImgURL() {
            return imgURL;
        }

        public Drawable getImage() {
            return img;
        }

        @Override
        public String toString() {
            return link;
        }
    }
}
