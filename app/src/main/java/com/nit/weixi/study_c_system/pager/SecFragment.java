package com.nit.weixi.study_c_system.pager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nit.weixi.study_c_system.R;
import com.nit.weixi.study_c_system.activity.MainActivity;
import com.nit.weixi.study_c_system.activity.TeacherClientActivity;
import com.nit.weixi.study_c_system.tools.MyConstants;
import com.nit.weixi.study_c_system.tools.RestClient;
import com.nit.weixi.study_c_system.tools.Tool;

import cz.msebera.android.httpclient.Header;

/**
 * 登陆的详情界面
 * Created by weixi on 2016/4/16.
 */
public class SecFragment extends Fragment implements View.OnClickListener {

    private final int IMAGE_REQUEST_CODE = 0;
    private final int RESIZE_REQUEST_CODE = 1;
    private String LOGINSUCCESS="right";
    private String LOGINFAILURE="wrong";
    public static String tag; //用户的身份标记

    View view; // fragment的界面展示
    EditText etPassword; //老师端的登陆密码输入框
    EditText etUsername; //学生端的学生姓名输入框
    EditText etUsernum; //学生端的学生学号输入框
    Button btnLogin; //老师端的登陆按钮
    Button btnStartUse; //学生端的登陆按钮
    ImageView userIcon; //学生头像 ，不设置默认为ic_launcher
    Bitmap bitmap; //保存到本地的学生头像
    SharedPreferences.Editor editor; //用户身份的sp的editor
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (tag.equals("student")) { //学生端界面
            view = inflater.inflate(R.layout.yindao_student, container, false);
            userIcon = (ImageView) view.findViewById(R.id.iv_user_icon);
            etUsername = (EditText) view.findViewById(R.id.et_username);
            etUsernum = (EditText) view.findViewById(R.id.et_usernumber);
            btnStartUse = (Button) view.findViewById(R.id.btn_startuse);
            userIcon.setOnClickListener(this);
            btnStartUse.setOnClickListener(this);
        } else { //老师端界面
            view = inflater.inflate(R.layout.yindao_teacher, container, false);
            etPassword = (EditText) view.findViewById(R.id.et_password);
            btnLogin = (Button) view.findViewById(R.id.btn_login);
            btnLogin.setOnClickListener(this);
        }
        editor = Tool.getEditor(getActivity(), MyConstants.LOGIN_SP); //得到身份sp的editor对象
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) { //不是成功啥都不做
            return;
        }

        switch (requestCode) {
            case IMAGE_REQUEST_CODE: //剪裁图片
                resizeImage(data.getData());
                break;

            case RESIZE_REQUEST_CODE: //显示圆形头像
                if (data != null) {
                    showResizeImage(data);
                }
                break;
        }
    }

    /**
     * 调用系统图库剪裁一张合适大小的图片
     * @param uri 图片对应的uri
     */
    public void resizeImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, RESIZE_REQUEST_CODE);
    }

    /**
     * 通过工具把得到的剪裁好的头像剪成圆形，然后设置给ImageView
     * @param data 剪裁好的图像intent
     */
    private void showResizeImage(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            bitmap = Tool.toRoundBitmap(photo);
            userIcon.setImageBitmap(bitmap);
        }
    }

    /**
     * 相应点击事件
     * @param v 控件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login: //根据填写的密码 请求服务器 登陆老师端
                String password = String.valueOf(etPassword.getText());
                RequestParams params=new RequestParams();
                params.put("password",password);
                loginTeacher(params);
                break;
            case R.id.iv_user_icon: //打开系统文件浏览器 得到一张图
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, IMAGE_REQUEST_CODE);
                break;

            case R.id.btn_startuse: //学生登陆
                String userName = String.valueOf(etUsername.getText());
                String userNum = String.valueOf(etUsernum.getText());
                Intent intent = new Intent(getActivity(), MainActivity.class);
                if (userName.isEmpty()||userNum.isEmpty()){
                    Toast.makeText(getActivity(),"姓名或学号为空，请输入",Toast.LENGTH_SHORT).show();
                }else {
                    // 如果没有手动选择头像，系统将把ic_launcher剪成圆形后复制给bitmap
                    if (bitmap==null){
                        BitmapDrawable bd = (BitmapDrawable) userIcon.getDrawable();
                        bitmap=Tool.toRoundBitmap(bd.getBitmap());
                    }
                    SharedPreferences.Editor userEditor = Tool.getEditor(getActivity(), MyConstants.STUDENT_SP);
                    userEditor.putString(MyConstants.STUDENT_SP_NAME,userName);
                    userEditor.putString(MyConstants.STUDENT_SP_NUMBER,userNum);
                    userEditor.commit();
                    Tool.saveBitmap(getActivity(),bitmap); //把学生头像存到应用目录下

                    editor.putString(MyConstants.LOGIN_SP_SHENFEN,"student").commit();
                    startActivity(intent);
                    getActivity().finish();
                }
                break;
        }
    }

    /**
     * 根据传入密码向服务器核对 根据返回的数据做相应的操作
     * @param password 传入的密码
     */
    public void loginTeacher(RequestParams password) {
        RestClient.get(MyConstants.LOGIN_URL, password, new AsyncHttpResponseHandler() {

            /**
             * 重写onSuccess方法 当访问成功时调用
             * 成功后开启teacher活动 并将标识teacher写入sharedPreference里
             * @param statusCode 200
             * @param headers 返回数据的头部
             * @param responseBody 返回数据的正文 既OutputStream里的内容
             */
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String loginCode=new String(responseBody);
                if (loginCode.equals(LOGINSUCCESS)){ //根据传回的字符串 判断密码是否正确
                    Toast.makeText(getActivity(),"登陆成功",Toast.LENGTH_SHORT).show();
                    editor.putString(MyConstants.LOGIN_SP_SHENFEN,"teacher").commit();
                    startActivity(new Intent(getActivity(), TeacherClientActivity.class));
                    getActivity().finish();
                }else {
                    Toast.makeText(getActivity(),"密码错误，登陆失败",Toast.LENGTH_SHORT).show();
                }
            }

            /**
             * 错误时调用
             * @param statusCode 响应码
             * @param headers 返回的头部
             * @param responseBody 返回的正文
             * @param error 错误信息
             */
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Tool.backOnFailure(getActivity(),statusCode);
            }
        });
    }
}
