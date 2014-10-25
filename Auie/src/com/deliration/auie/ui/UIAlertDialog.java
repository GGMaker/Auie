package com.deliration.auie.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UIAlertDialog{

    private final int MATCH_PARENT = LinearLayout.LayoutParams.MATCH_PARENT;
    private final int WRAP_CONTENT = LinearLayout.LayoutParams.WRAP_CONTENT;
    private final int radius = 8;
    private final float[] outerR = new float[] { radius, radius, radius, radius, radius, radius, radius, radius };

    private String title = "标题文字可使用setTitle()设置";
    private String cancelTitle = "取消";
    private String actionTitle = "执行";
    private Context context;
    private AlertDialog dialog;
    private Typeface typeface;
    private float scale;
    private TextView titleTextView;
    private Button cancelButton;
    private Button actionButton;
    private View.OnClickListener cancelClickListener;
    private View.OnClickListener actionClickListener;

    private int backgroundColor = Color.parseColor("#FFFFFFFF");
    private int titleColor = Color.parseColor("#CC444444");
    private int lineHorizontalColor = Color.parseColor("#55888888");
    private int lineVerticalColor = Color.parseColor("#55888888");
    private int cancelTitleColor = Color.parseColor("#CC444444");
    private int actionTitleColor = Color.parseColor("#CC444444");
    private int cancelBackgroundColor = Color.parseColor("#00000000");
    private int actionBackgroundColor = Color.parseColor("#00000000");

    public UIAlertDialog(Context context){
        this.context = context;
        this.scale = context.getResources().getDisplayMetrics().density;
    }

    public UIAlertDialog setTitle(String title){
        this.title = title;
        return this;
    }

    public UIAlertDialog setTypeface(Typeface typeface){
        this.typeface = typeface;
        return this;
    }

    public UIAlertDialog setCancelButton(String title){
        this.cancelTitle = title;
        return this;
    }

    public UIAlertDialog setActionTitleColor(int actionTitleColor) {
        this.actionTitleColor = actionTitleColor;
        return this;
    }

    public UIAlertDialog setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public UIAlertDialog setTitleColor(int titleColor) {
        this.titleColor = titleColor;
        return this;
    }

    public UIAlertDialog setLineHorizontalColor(int lineHorizontalColor) {
        this.lineHorizontalColor = lineHorizontalColor;
        return this;
    }

    public UIAlertDialog setLineVerticalColor(int lineVerticalColor) {
        this.lineVerticalColor = lineVerticalColor;
        return this;
    }

    public UIAlertDialog setCancelTitleColor(int cancelTitleColor) {
        this.cancelTitleColor = cancelTitleColor;
        return this;
    }

    public UIAlertDialog setCancelBackgroundColor(int cancelBackgroundColor) {
        this.cancelBackgroundColor = cancelBackgroundColor;
        return this;
    }

    public UIAlertDialog setActionBackgroundColor(int actionBackgroundColor) {
        this.actionBackgroundColor = actionBackgroundColor;
        return this;
    }

    public UIAlertDialog setCancelButton(String title, View.OnClickListener listener){
        this.cancelTitle = title;
        this.cancelClickListener = listener;
        return this;
    }

    public UIAlertDialog setActionButton(String title, View.OnClickListener listener){
        this.actionTitle = title;
        this.actionClickListener = listener;
        return this;
    }

    @SuppressWarnings("deprecation")
	public void show(){
        dialog = new AlertDialog.Builder(context).create();
        RoundRectShape roundRectShape = new RoundRectShape(outerR, null, null);
        ShapeDrawable shapeDrawable = new ShapeDrawable(roundRectShape);
        shapeDrawable.getPaint().setColor(backgroundColor);
        shapeDrawable.getPaint().setStyle(Paint.Style.FILL);
        LinearLayout windowLayout  = new LinearLayout(context);
        windowLayout.setLayoutParams(getParams(MATCH_PARENT, WRAP_CONTENT));
        windowLayout.setMinimumWidth((int) (280 * scale + 0.5f));
        windowLayout.setOrientation(LinearLayout.VERTICAL);
        windowLayout.setBackgroundDrawable(shapeDrawable);
        titleTextView = new TextView(context);
        titleTextView.setLayoutParams(getParams(MATCH_PARENT, dp2px(120f)));
        titleTextView.setTextColor(titleColor);
        titleTextView.setGravity(Gravity.CENTER);
        titleTextView.setTextSize(16f);
        titleTextView.setText(title);
        View lineHorizontal = new View(context);
        lineHorizontal.setLayoutParams(getParams(MATCH_PARENT, dp2px(0.5f)));
        lineHorizontal.setBackgroundColor(lineHorizontalColor);
        LinearLayout buttonLayout  = new LinearLayout(context);
        buttonLayout.setLayoutParams(getParams(MATCH_PARENT, dp2px(48f)));
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp2px(0f), MATCH_PARENT, 1);
        cancelButton = new Button(context);
        cancelButton.setLayoutParams(params);
        cancelButton.setTextColor(cancelTitleColor);
        cancelButton.setTextSize(16);
        cancelButton.setText(cancelTitle);
        cancelButton.setBackgroundColor(cancelBackgroundColor);
        if (cancelClickListener == null){
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }else{
            cancelButton.setOnClickListener(cancelClickListener);
        }
        View lineVertical = new View(context);
        lineVertical.setLayoutParams(getParams(dp2px(0.5f), MATCH_PARENT));
        lineVertical.setBackgroundColor(lineVerticalColor);
        actionButton = new Button(context);
        actionButton.setLayoutParams(params);
        actionButton.setTextColor(actionTitleColor);
        actionButton.setTextSize(16);
        actionButton.setText(actionTitle);
        actionButton.setBackgroundColor(actionBackgroundColor);
        if (actionClickListener == null){
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "未设置动作", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            actionButton.setOnClickListener(actionClickListener);
        }
        buttonLayout.addView(cancelButton);
        buttonLayout.addView(lineVertical);
        buttonLayout.addView(actionButton);
        windowLayout.addView(titleTextView);
        windowLayout.addView(lineHorizontal);
        windowLayout.addView(buttonLayout);
        if (typeface != null){
            titleTextView.setTypeface(typeface);
            cancelButton.setTypeface(typeface);
            actionButton.setTypeface(typeface);
        }
        dialog.show();
        dialog.setContentView(windowLayout);
    }

    private LinearLayout.LayoutParams getParams(int width, int height){
        return new LinearLayout.LayoutParams(width, height);
    }

    private int dp2px(float dp){
        return (int) (dp * scale + 0.5f);
    }

    public void dismiss(){
        if (dialog != null){
            dialog.dismiss();
        }
    }
}