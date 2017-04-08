package vlatko.ivetic.androidrssreader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class RSSWebViewActivity extends AppCompatActivity {

    private WebView webView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Intent intent = getIntent();
        final String URL = intent.getStringExtra(RSSReader.EXTRA_LINK);

        webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient());

        if (!webView.post(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl(URL);
            }
        })) {
            Toast.makeText(RSSWebViewActivity.this, "Something went Wrong\nPlease try again.", Toast.LENGTH_LONG).show();
        }
    }
}
