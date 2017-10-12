package com.mitash.quicknote.utils;

import android.support.v4.view.GravityCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.mitash.quicknote.R;
import com.mitash.quicknote.databinding.LayoutLinkPopupBinding;
import com.mitash.quicknote.editor.Editor;
import com.mitash.quicknote.view.widget.ToggleImageButton;

/**
 * Created by Mitash Gaurh on 9/19/2017.
 */

public final class DialogUtils {

    /**
     * @param btnLink link button in the editor
     * @param editor editor
     */
    public static void showEditLinkPanel(final ToggleImageButton btnLink, final Editor editor) {
        View contentView = LayoutInflater.from(btnLink.getContext()).inflate(R.layout.layout_link_popup, null);
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        final LayoutLinkPopupBinding layoutLinkPopupBinding = LayoutLinkPopupBinding.bind(contentView);

        final PopupWindow popupWindow = new PopupWindow(contentView);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setAnimationStyle(R.style.ComposeTheme_PopUpAnimation);

        if (btnLink.isChecked()) {
            Object status = btnLink.getTag();
            if (null == status) {
                return;
            }
            boolean canEdit = status instanceof String;
            layoutLinkPopupBinding.etLink.setVisibility(canEdit ? View.VISIBLE : View.GONE);
            layoutLinkPopupBinding.tvConfirm.setVisibility(canEdit ? View.VISIBLE : View.GONE);
            layoutLinkPopupBinding.tvMultipleLinks.setVisibility(canEdit ? View.GONE : View.VISIBLE);
            if (canEdit) {
                layoutLinkPopupBinding.etLink.setText((String) btnLink.getTag());
                layoutLinkPopupBinding.etLink.setSelection(layoutLinkPopupBinding.etLink.getText().length());
            }
        }

        layoutLinkPopupBinding.tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                editor.updateLink("", layoutLinkPopupBinding.etLink.getText().toString());
            }
        });
        layoutLinkPopupBinding.tvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                editor.removeLink();
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                btnLink.setChecked(false);
            }
        });

        int[] tempLocation = new int[2];
        btnLink.getLocationOnScreen(tempLocation);
        int measure = contentView.getMeasuredHeight();
        int offsetY = tempLocation[1] - measure;
        popupWindow.showAtLocation(btnLink, Gravity.TOP | GravityCompat.START, 0, offsetY);
    }
}
