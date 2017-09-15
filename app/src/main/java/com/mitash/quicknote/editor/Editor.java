package com.mitash.quicknote.editor;


import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Map;

public abstract class Editor {

    public enum Format {
        BOLD,
        ITALIC,
        BULLET_LIST,
        ORDERED_LIST,
        BLOCK_QUOTE,
        LINK
    }

    protected EditorListener mListener;

    public Editor(EditorListener listener) {
        mListener = listener;
    }

    public abstract void init(WebView view);

    public abstract void setEditingEnabled(boolean enabled);

    public abstract void setTitle(String title);

    public abstract String getTitle();

    public abstract void setContent(String content);

    public abstract String getContent();

    public abstract void insertImage(String title, String url);

    public abstract void insertLink(String title, String url);

    public abstract void updateLink(String title, String url);

    public abstract void redo();

    public abstract void undo();

    public abstract void toggleOrderList();

    public abstract void toggleUnOrderList();

    public abstract void toggleBold();

    public abstract void toggleItalic();

    public abstract void toggleQuote();

    public void removeLink() {
    }

    public String getSelection() {
        return "";
    }

    public interface EditorListener {
        void onPageLoaded();

        void onClickedLink(String title, String url);

        void onStyleChanged(Format style, boolean enabled);

        void onFormatChanged(Map<Format, Object> enabledFormats);

        void onCursorChanged(Map<Format, Object> enabledFormats);

        void linkTo(String url);

        void onClickedImage(String url);
    }

    class EditorClient extends WebViewClient {

        private static final String TAG = "WebViewClient:";

        @SuppressWarnings("deprecation")
        @Deprecated
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            Uri uri = Uri.parse(url);
            Log.i(TAG, "shouldInterceptRequest(), request= " + url + ", scheme= " + uri.getScheme() + ", authority= " + uri.getAuthority());
            /*if (NoteFileService.isLocalImageUri(uri)) {
                Log.i(TAG, "get image");
                return new WebResourceResponse("image/png", "utf-8", NoteFileService.getImage(uri.getQueryParameter("id")));
            } else {
                return null;
            }*/
            return null;
        }

        @SuppressWarnings("deprecation")
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            Uri uri = Uri.parse(request.getUrl().toString());
            Log.i(TAG, "shouldInterceptRequest(), request= " + request.getUrl().toString() + ", scheme= " + uri.getScheme() + ", authority= " + uri.getAuthority());
            /*if (NoteFileService.isLocalImageUri(uri)) {
                Log.i(TAG, "get image");
                return new WebResourceResponse("image/png", "utf-8", NoteFileService.getImage(uri.getQueryParameter("id")));
            } else {
                return super.shouldInterceptRequest(view, request.getUrl().toString());
            }*/
            return super.shouldInterceptRequest(view, request.getUrl().toString());
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
            Log.i(TAG, "onLoadResource(), rul=" + url);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Log.i(TAG, "shouldOverrideUrlLoading(), url=" + request.getUrl().toString());
            return super.shouldOverrideUrlLoading(view, request);
        }

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.i(TAG, "shouldOverrideUrlLoading(), url=" + url);
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.i(TAG, "onPageFinished()");
            mListener.onPageLoaded();
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            Log.i(TAG, "onReceivedError(), code=" + error.getErrorCode() + ", desc=" + error.getDescription() + ", url=" + request.getUrl().toString());
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            Log.i(TAG, "onReceivedError(), code=" + errorCode + ", desc=" + description + ", url=" + failingUrl);
        }
    }

    class EditorChromeClient extends WebChromeClient {

        private static final String TAG = "ChromeClient:";

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            Log.i(TAG, String.format("source=%s, line=%d, msg=%s",
                    consoleMessage.sourceId(),
                    consoleMessage.lineNumber(),
                    consoleMessage.message()));
            return true;
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            Log.i(TAG, "alert: url=" + url + ", msg=" + message);
            return true;
        }
    }
}
