package com.mitash.quicknote.editor;

import android.support.annotation.NonNull;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class TinnyMceCallback {

    private static final String TAG = "TinnyMceCallback:";

    private TinnyMceListener mTinnyMceListener;
    private Gson mGson = new Gson();

    TinnyMceCallback(TinnyMceListener tinnyMceListener) {
        this.mTinnyMceListener = tinnyMceListener;
    }

    interface TinnyMceListener {
        void onFormatChanged(Map<Editor.Format, Object> formats);

        void linkTo(String url);

        void onClickedImage(String url);

        void onCursorChanged(Map<Editor.Format, Object> enabledFormats);

        void onTitleChanged(String title);

        void onContentChanged(String content);
    }

    @JavascriptInterface
    public void onFormatChanged(String data) {
        if (null != mTinnyMceListener) {
            Map<Editor.Format, Object> format = parseFormat(data);
            mTinnyMceListener.onFormatChanged(format);
        }
    }

    @JavascriptInterface
    public void onTitleChanged(String title) {
        if (mTinnyMceListener != null) {
            mTinnyMceListener.onTitleChanged(title);
        }
    }

    @JavascriptInterface
    public void onContentChanged(String content) {
        if (mTinnyMceListener != null) {
            mTinnyMceListener.onContentChanged(content);
        }
    }

    @JavascriptInterface
    public void linkTo(String url) {
        if (mTinnyMceListener != null) {
            mTinnyMceListener.linkTo(url);
        }
    }

    @JavascriptInterface
    public void onClickedImage(String url) {
        if (mTinnyMceListener != null) {
            mTinnyMceListener.onClickedImage(url);
        }
    }

    @JavascriptInterface
    public void onCursorChanged(String data) {
        Log.i(TAG, "Data: " + data);
        if (mTinnyMceListener == null) {
            return;
        }
        Map<Editor.Format, Object> enabledFormats = parseFormat(data);
        mTinnyMceListener.onCursorChanged(enabledFormats);
    }

    @SuppressWarnings("unchecked")
    @NonNull
    private Map<Editor.Format, Object> parseFormat(String data) {
        Map<String, Object> formats = mGson.fromJson(data, Map.class);
        Map<Editor.Format, Object> enabledFormats = new HashMap<>();
        for (Map.Entry<String, Object> format : formats.entrySet()) {
            Editor.Format formatEnum = stringToFormat(format.getKey());
            if (formatEnum != null) {
                enabledFormats.put(formatEnum, format.getValue());
            }
        }
        return enabledFormats;
    }

    private static Editor.Format stringToFormat(String val) {
        switch (val) {
            case "bold":
                return Editor.Format.BOLD;
            case "italic":
                return Editor.Format.ITALIC;
            case "ul":
                return Editor.Format.BULLET_LIST;
            case "ol":
                return Editor.Format.ORDERED_LIST;
            case "blockquote":
                return Editor.Format.BLOCK_QUOTE;
            case "link":
                return Editor.Format.LINK;
            case "strikethrough":
                return Editor.Format.STRIKE_THROUGH;
            default:
                return null;
        }
    }
}
