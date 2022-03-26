package com.y54.bdao;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;


public class ImageActivity extends AppCompatActivity {
    private ImageView imageView;
    private String imgUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);//需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.setStatusBarColor(Color.parseColor("#fa7298"));//设置状态栏颜色

        imageView = findViewById(R.id.img_show);
        Intent i = getIntent();
        imgUrl = i.getStringExtra("imgUrl");
        Log.d("mmmmmmmmi", imgUrl);
        Glide.with(this).load(imgUrl).into(imageView);
    }
}
