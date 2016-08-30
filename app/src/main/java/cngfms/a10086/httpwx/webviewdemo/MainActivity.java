package cngfms.a10086.httpwx.webviewdemo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private WebView webview;

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webview = (WebView) findViewById(R.id.webview);        // 启用javascript
        webview.getSettings().setJavaScriptEnabled(true);        // 从assets目录下面的加载html
        //方案一
        webview.addJavascriptInterface(MainActivity.this, "android");
        //方案二
        webview.addJavascriptInterface(new DemoJavaScriptInterface(), "demo");
        webview.loadUrl("file:///android_asset/web.html");
        //Button按钮 无参调用HTML js方法
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                // 无参数调用 JS的方法
                webview.loadUrl("javascript:javacalljs()");

            }
        });

        //Button按钮 有参调用HTML js方法
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String par = "这是参数";
                // 传递参数调用JS的方法
                webview.loadUrl("javascript:javacalljswith('" + par + "')");
            }
        });
    }

    //由于安全原因 targetSdkVersion>=17需要加 @JavascriptInterface
    //JS调用Android JAVA方法名和HTML中的按钮 onclick后的别名后面的名字对应
    @JavascriptInterface
    public void startFunction() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "js调用了java无参方法", Toast.LENGTH_LONG).show();

            }
        });
    }

    @JavascriptInterface
    public void startFunction(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show();
            }
        });
    }


    final class DemoJavaScriptInterface {
        DemoJavaScriptInterface(){}
        /**
         * 注意：targetSdkVersion>=17被调用的方法上必须加上@JavascriptInterface
         * 否则无法正常调用会提示找不到该方法
         */
        @JavascriptInterface
        public void method() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "js调用了java无参方法method", Toast.LENGTH_LONG).show();
                }
            });
        }
        @JavascriptInterface
        public void method(final String text) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}