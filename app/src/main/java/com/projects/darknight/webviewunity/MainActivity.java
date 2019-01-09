package com.projects.darknight.webviewunity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.projects.darknight.webviewunity.api.RetrofitClientInstance;
import com.projects.darknight.webviewunity.api.ServerApi;
import com.projects.darknight.webviewunity.pojo.ServerPermission;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    TextView serverResponseText;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serverResponseText = findViewById(R.id.serverResponseText);
        webView = findViewById(R.id.webView);

        Button askServer = findViewById(R.id.btnAskServer);
        askServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askServerPermission();
            }
        });

        Button openWebView = findViewById(R.id.btnWebView);
        openWebView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public void onClick(View v) {
                webView.getSettings().setJavaScriptEnabled(true);
                webView.setWebViewClient(new MyWebViewClient());
                webView.loadUrl("https://html5test.com/");
            }
        });
    }

    private void askServerPermission(){
        //Obtain an instance of Retrofit by calling the static method.
        Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        /*
        The main purpose of Retrofit is to create HTTP calls from the Java interface based on the annotation associated with each method. This is achieved by just passing the interface class as parameter to the create method
        */
        ServerApi serverApi = retrofit.create(ServerApi.class);
        /*
        Invoke the method corresponding to the HTTP request which will return a Call object. This Call object will used to send the actual network request with the specified parameters
        */
        Call<ServerPermission> call = serverApi.allowGame();
        /*
        This is the line which actually sends a network request. Calling enqueue() executes a call asynchronously. It has two callback listeners which will invoked on the main thread
        */
        call.enqueue(new Callback<ServerPermission>() {
            @Override
            public void onResponse(@NonNull Call<ServerPermission> call, @NonNull Response<ServerPermission> response) {
                /*This is the success callback. Though the response type is JSON, with Retrofit we get the response in the form of WResponse POJO class
                 */
                if (response.body() != null) {
                    ServerPermission serverResponse = new ServerPermission(response.body().getName(), response.body().getPermission());
                    serverResponseText.setText(serverResponse.toString());
                }
            }
            @Override
            public void onFailure(Call call, Throwable t) {
                serverResponseText.setText("Something gone wrong");
            }
        });
    }

    private void startWithWebView(){

    }

    private class MyWebViewClient extends WebViewClient {
        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());
            return true;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
