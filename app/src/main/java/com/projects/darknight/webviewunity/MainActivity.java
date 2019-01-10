package com.projects.darknight.webviewunity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.darknight.rsb.UnityPlayerActivity;
import com.projects.darknight.webviewunity.api.RetrofitClientInstance;
import com.projects.darknight.webviewunity.api.ServerApi;
import com.projects.darknight.webviewunity.pojo.ServerPermission;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    final String GAME_PERMISSION = "start_game";
    WebView webView;
    String serverResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getPreferences(MODE_PRIVATE);
        editor = preferences.edit();
        webView = findViewById(R.id.webView);

        serverResponse = preferences.getString(GAME_PERMISSION, "0");
        if (serverResponse.equals("0")) {
            Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
            ServerApi serverApi = retrofit.create(ServerApi.class);
            Call<ServerPermission> call = serverApi.allowGame();
            call.enqueue(new Callback<ServerPermission>() {
                @Override
                public void onResponse(@NonNull Call<ServerPermission> call, @NonNull Response<ServerPermission> response) {
                    if (response.body() != null) {
                        editor.putString(GAME_PERMISSION, response.body().getPermission().toString());
                        editor.apply();
                        chooseWay();
                    } else {
                        simpleDialogShow("Server error!");
                    }

                }

                @Override
                public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                    simpleDialogShow("Network problems.\nPlease check your internet connection!");
                }
            });
        } else {
            chooseWay();
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void startWithWebView() {
        if(checkInternetConnection()) {
            webView.setVisibility(View.VISIBLE);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new MyWebViewClient());
            webView.loadUrl("https://html5test.com/");
        }else{
            simpleDialogShow("Please check your internet connection!");
        }
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
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }


    public void simpleDialogShow(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .create().show();
    }

    public void chooseWay() {
        switch (preferences.getString(GAME_PERMISSION, "0")) {
            case "false":
                startWithWebView();
                break;
            case "true": {
                Intent intent = new Intent(MainActivity.this, UnityPlayerActivity.class);
                startActivity(intent);
                finish();
                break;
            }
            default:
                simpleDialogShow("Something gone wrong(");
        }
    }

    public boolean checkInternetConnection() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }
}
