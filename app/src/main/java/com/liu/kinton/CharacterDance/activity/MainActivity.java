package com.liu.kinton.CharacterDance.activity;

import android.Manifest;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.liu.kinton.CharacterDance.R;
import com.liu.kinton.CharacterDance.utils.AyscnConvertUtils;
import com.liu.kinton.CharacterDance.utils.FileUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.model.Picture;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity implements AyscnConvertUtils.Progresslistener {
    private static final int REQUEST_CODE_GALLERY = 0x10;// 图库选取图片标识请求码
    private static final int REQUEST_CODE_VIDEO = 0x20;// 图库选取图片标识请求码

    @BindView(R.id.iv_main_convert_pic)
    ImageView ivConvertPic;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initPremssion();
    }

    private void initPremssion() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (!aBoolean) {
                            Toast.makeText(MainActivity.this, "申请权限失败！请在设置页面开放内存读写权限，否则无法正常使用功能！", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @OnClick({R.id.iv_main_convert_pic, R.id.iv_main_convert_video})
    void onPicClick(View view) {
        switch (view.getId()) {
            case R.id.iv_main_convert_pic:
                Intent picIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(picIntent, REQUEST_CODE_GALLERY);
                break;
            case R.id.iv_main_convert_video:
                Intent vIntent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(vIntent, REQUEST_CODE_VIDEO);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_GALLERY:
                    Log.i("main activity", "uri:" + data.getData().toString());
                    Uri uri = data.getData();
                    //Log.i("main activity", "path:" + FileUtils.getPathByUri(this, data.getData()));
                    AyscnConvertUtils.getInstance().startConvertPic(this, uri, this);
                    break;
                case REQUEST_CODE_VIDEO:
                    String videoPath = FileUtils.getPathByUri(this, data.getData());
                    Log.i("main activity", "path:" + videoPath);
                    AyscnConvertUtils.getInstance().startConvertVideo(this, videoPath, this);
            }

        }

    }

    @Override
    public void onProgress(Integer progress) {
        Log.i("onProgress", "progress :" + progress);
        Toast.makeText(this,""+progress,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCompelete() {
        Log.i("convert_progress", "ok");
        Toast.makeText(this,"Compeleted !!!",Toast.LENGTH_LONG).show();
    }
}