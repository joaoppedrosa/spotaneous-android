package com.es.spotaneous.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.es.spotaneous.MainActivity;
import com.es.spotaneous.R;
import com.es.spotaneous.utils.SharedConfigs;

import me.drakeet.materialdialog.MaterialDialog;

public class LoginFragment extends Fragment {

    private final static String URL = "http://autheserv.ddns.net:4150/auth/api/users/login";
    private Button button;
    private MaterialDialog mMaterialDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        button = (Button) view.findViewById(R.id.googleButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = getActivity().getLayoutInflater().inflate(R.layout.web_view, null);

                WebView webView = (WebView) v.findViewById(R.id.webView);
                WebSettings webSettings = webView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
                webView.loadUrl(URL);
                webView.clearCache(true);
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        Log.e("Load page", "load has finish!");
                    }
                });
                webView.setWebChromeClient(new WebChromeClient() {
                    @Override
                    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                        try {
                            Log.e("Chrome message", consoleMessage.message());
                        } catch (Exception e) {
                            Log.e("Chrome message error", e.getMessage(), e);
                        }
                        return super.onConsoleMessage(consoleMessage);
                    }
                });

                Button done = (Button) v.findViewById(R.id.ok);
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String cookie = getCookie(URL, "id");
                        if (cookie != null) {
                            SharedConfigs.addStringValue("iduser", cookie, getActivity());
                            Log.e("COKKIEIEIEIEIEI", "" + SharedConfigs.getStringValue("iduser", getActivity()));
                            mMaterialDialog.dismiss();
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getActivity(), "Faça o login correctamente!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                mMaterialDialog = new MaterialDialog(getActivity())
                        .setCanceledOnTouchOutside(false)
                        .setContentView(v);
                mMaterialDialog.show();
            }
        });

        return view;
    }

    public String getCookie(String siteName,String CookieName){
        String CookieValue = null;
        CookieManager cookieManager = CookieManager.getInstance();
        String cookies = cookieManager.getCookie(siteName);
        if(cookies == null){
            return null;
        }else{
            String[] temp = cookies.split(";");
            for (String ar1 : temp ){
                if(ar1.contains(CookieName)){
                    String[] temp1=ar1.split("=");
                    CookieValue = temp1[1];
                }
            }
            return CookieValue;
        }
    }
}
