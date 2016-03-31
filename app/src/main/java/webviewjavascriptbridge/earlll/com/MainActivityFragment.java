package webviewjavascriptbridge.earlll.com;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A placeholder fragment containing a simple view.
 */
@SuppressLint("SetJavaScriptEnabled")
public class MainActivityFragment extends Fragment {
    private WebView webView;
    private WVJBWebViewClient webViewClient;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_main, container, false);
        webView=(WebView) view.findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl("file:///android_asset/ExampleApp.html");

        webViewClient = new MyWebViewClient(webView);
        webViewClient.enableLogging();
        webView.setWebViewClient(webViewClient);

        view.findViewById(R.id.button1).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                webViewClient.send("A string sent from ObjC to JS", new WVJBWebViewClient.WVJBResponseCallback() {
                    @Override
                    public void callback(Object data) {
                        Toast.makeText(getActivity(), "sendMessage got response: " + data, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        view.findViewById(R.id.button2).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    webViewClient.callHandler("testJavascriptHandler", new JSONObject("{\"greetingFromObjC\": \"Hi there, JS!\" }"), new WVJBWebViewClient.WVJBResponseCallback() {

                        @Override
                        public void callback(Object data) {
                            Toast.makeText(getActivity(), "testJavascriptHandler responded: " + data, Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }

    class MyWebViewClient extends WVJBWebViewClient {
        public MyWebViewClient(WebView webView) {
            // support js send
            super(webView, new WVJBWebViewClient.WVJBHandler() {
                @Override
                public void request(Object data, WVJBResponseCallback callback) {
                    Toast.makeText(getActivity(), "ObjC Received message from JS:" + data, Toast.LENGTH_LONG).show();
                    callback.callback("Response for message from ObjC!");
                }
            });

			// not support js send
            registerHandler("testObjcCallback", new WVJBWebViewClient.WVJBHandler() {
                @Override
                public void request(Object data, WVJBResponseCallback callback) {
                    Toast.makeText(getActivity(), "testObjcCallback called:" + data, Toast.LENGTH_LONG).show();
                    callback.callback("Response from testObjcCallback!");
                }
            });

            send("A string sent from ObjC before Webview has loaded.",
                    new WVJBResponseCallback() {
                        @Override
                        public void callback(Object data) {
                            Toast.makeText(getActivity(), "ObjC got response! :" + data, Toast.LENGTH_LONG).show();
                        }
                    });

            try {
                callHandler("testJavascriptHandler",
                        new JSONObject("{\"foo\":\"before ready\" }"),
                        new WVJBResponseCallback() {
                            @Override
                            public void callback(Object data) {
                                Toast.makeText(getActivity(), "ObjC call testJavascriptHandler got response! :" + data, Toast.LENGTH_LONG).show();
                            }
                        });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }
    }
}
