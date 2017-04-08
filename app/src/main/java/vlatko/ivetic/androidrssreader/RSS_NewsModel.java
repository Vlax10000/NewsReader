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
 * Class rapresents a data model for the RSS reader.
 */
public class RSS_NewsModel {

    private ArrayList<FeedItem> _newsFeed = null;

    private String _channel = null;
    private String _title = null;
    private String _link = null;
    private String _description = null;
    private String _publishDate = null;
    private String _urlString = null;
    private XmlPullParserFactory _xmlFactoryObject = null;

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
        return _newsFeed.size();
    }

    public FeedItem getItem(int itemIndex) {
        return _newsFeed.get(itemIndex);
    }

    public List<FeedItem> updateNews() {
        this._newsFeed = new ArrayList<>();
        fetchAndParseXML();

        for (FeedItem item : _newsFeed) {
            extractDescription(item);
            extractPublishDate(item);
        }

        return _newsFeed;
    }

    private void extractDescription(FeedItem feedItem) {
        // full description tag text is logged under regex "desc"
        // this method splits its contents into FeedItem class attributes
        // Log.i("desc", feedItem.description);

        String[] descrStrParts = feedItem.description.split(" /> ");
        String[] imgProperties = descrStrParts[0].split(" ");

        feedItem.imgURL = imgProperties[1].substring(5, imgProperties[1].length() - 1);
        feedItem.description = descrStrParts[1];
    }

    private void extractPublishDate(FeedItem feedItem) {
        // full pubDate tag text is logged under regex "date"
        // this method splits its contents into FeedItem class attributes
        // Log.i("date", feedItem.pubDate);

        String[] tmpDate = feedItem.pubDate.split(":");
        int length = tmpDate[0].length();

        feedItem.pubDate = tmpDate[0].substring(0, length-2);

        int hours = Integer.parseInt(tmpDate[0].substring(length-2, length)) + 2;
        feedItem.pubDate += String.format(", %d:%s", hours, tmpDate[1]);
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
                                _newsFeed.add(item);
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
