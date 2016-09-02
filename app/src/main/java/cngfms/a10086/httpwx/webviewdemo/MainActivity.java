package cngfms.a10086.httpwx.webviewdemo;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private WebView webview;
    private Button btnNoReturnNoPar;
    private Button btnNoReturnHavePar;
    private Button btnHaveReturnNoPar;
    private Button btnHaveReturnHavePar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initWebSetting();
        initWebChromeClient();



    }

    private void initView() {
        webview = (WebView) findViewById(R.id.webview);
        btnNoReturnNoPar = (Button) findViewById(R.id.btn_noreturn_nopar);
        btnNoReturnHavePar = (Button) findViewById(R.id.btn_noreturn_havepar);
        btnHaveReturnNoPar = (Button) findViewById(R.id.btn_havereturn_nopar);
        btnHaveReturnHavePar = (Button) findViewById(R.id.btn_havereturn_havepar);


        btnNoReturnNoPar.setOnClickListener(this);
        btnNoReturnHavePar.setOnClickListener(this);
        btnHaveReturnNoPar.setOnClickListener(this);
        btnHaveReturnHavePar.setOnClickListener(this);
    }

    private void initWebSetting() {
        webview.getSettings().setJavaScriptEnabled(true);// 启用javascript
        //方案一
        webview.addJavascriptInterface(MainActivity.this, "android");
        //方案二
        webview.addJavascriptInterface(new DemoJavaScriptInterface(), "demo");

        webview.loadUrl("file:///android_asset/web.html"); // 从assets目录下面的加载html
    }

    private void initWebChromeClient() {
        webview.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                if (!TextUtils.isEmpty(message)) {
                    Toast.makeText(MainActivity.this,message,Toast.LENGTH_LONG).show();
                }
                //必须要加否则点击html页面没有反应
                result.cancel();
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                return super.onJsConfirm(view, url, message, result);
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }

        });
    }

    //由于安全原因 targetSdkVersion>=17需要加 @JavascriptInterface
    //JS调用Android JAVA方法名和HTML中的按钮 onclick后的别名后面的名字对应
    @JavascriptInterface
    public void startJavaNoParMethod() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "js调用了java无参方法1", Toast.LENGTH_LONG).show();

            }
        });
    }

    @JavascriptInterface
    public void startJavaHaveParMethod(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_noreturn_nopar://android调用js无参无返回的方法
                webview.loadUrl("javascript:noreturnnopar()");
                break;
            case R.id.btn_noreturn_havepar://有参无返回
                String par = "我是android调用js有参无返回方法的参数";
                webview.loadUrl("javascript:noreturnhavepar('"+ par +"')");
                break;
            case R.id.btn_havereturn_nopar://无参有返回
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    webview.evaluateJavascript("havereturnnopar()", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            Toast.makeText(MainActivity.this, value + "--当前线程："
                                    + Thread.currentThread().getName(), Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    /**
                     *
                     * 当前编译版本小于19时的策略：
                     * 客户端调用js的方法，不等待返回值，
                     * js在客户端调用方法后主动调用客户端的方法，来将返回值作为参数在传递给客户端
                     *
                     */
                    Toast.makeText(MainActivity.this,  "当前编译版本："
                            + Build.VERSION.SDK_INT + "无法获取返回值", Toast.LENGTH_LONG).show();
                    webview.loadUrl("javascript:havereturnnopar()");
                }
                break;
            case R.id.btn_havereturn_havepar://有参有返回
                String par2 = "我是android调用js有参有返回方法的参数";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    webview.evaluateJavascript("havereturnhavepar('" + par2 + "')", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            Toast.makeText(MainActivity.this, value + "--当前线程："
                                    + Thread.currentThread().getName(), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this,  "当前编译版本："
                            + Build.VERSION.SDK_INT + "无法获取返回值", Toast.LENGTH_LONG).show();
                    webview.loadUrl("javascript:havereturnhavepar('" + par2 + "')");
                }
                break;
        }
    }


    final class DemoJavaScriptInterface {
        DemoJavaScriptInterface(){}
        /**
         * 注意：targetSdkVersion>=17被调用的方法上必须加上@JavascriptInterface
         * 否则无法正常调用会提示找不到该方法
         *
         */
        @JavascriptInterface
        public void startJavaNoParMethod() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "js调用了java无参方法2" , Toast.LENGTH_LONG).show();
                }
            });
        }
        @JavascriptInterface
        public void startJavaHaveParMethod(final String text) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}