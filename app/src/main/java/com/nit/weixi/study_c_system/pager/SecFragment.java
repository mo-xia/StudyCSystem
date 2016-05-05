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
 * Created by weixi on 2016/4/16.
 */
public class SecFragment extends Fragment implements View.OnClickListener {

    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int RESIZE_REQUEST_CODE = 2;
    private static final String LOGINSUCCESS="right";
    private static final String LOGINFAILURE="wrong";
    public static String tag;

    View view;
    EditText etPassword;
    EditText etUsername;
    EditText etUsernum;
    Button btnLogin;
    Button btnStartUse;
    ImageView userIcon;
    Bitmap bitmap;
    SharedPreferences.Editor editor;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //System.out.println("tag: "+tag);
        if (tag.equals("student")) {
            view = inflater.inflate(R.layout.yindao_student, container, false);
            userIcon = (ImageView) view.findViewById(R.id.iv_user_icon);
            etUsername = (EditText) view.findViewById(R.id.et_username);
            etUsernum = (EditText) view.findViewById(R.id.et_usernumber);
            btnStartUse = (Button) view.findViewById(R.id.btn_startuse);
            userIcon.setOnClickListener(this);
            btnStartUse.setOnClickListener(this);
        } else {
            view = inflater.inflate(R.layout.yindao_teacher, container, false);
            etPassword = (EditText) view.findViewById(R.id.et_password);
            btnLogin = (Button) view.findViewById(R.id.btn_login);
            btnLogin.setOnClickListener(this);
        }
        editor = Tool.getEditor(getActivity(), MyConstants.SPNAME);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case IMAGE_REQUEST_CODE:
                System.out.println("返回成功***************");
                //System.out.println(data.getData());
                resizeImage(data.getData());
                break;

            case RESIZE_REQUEST_CODE:
                if (data != null) {
                    System.out.println("剪裁成功*********************");
                    showResizeImage(data);
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

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

    private void showResizeImage(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            bitmap = Tool.toRoundBitmap(photo);
            userIcon.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                String password = String.valueOf(etPassword.getText());
                RequestParams params=new RequestParams();
                params.put("password",password);
                loginTeacher(params);
                break;
            case R.id.iv_user_icon:
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, IMAGE_REQUEST_CODE);
                break;

            case R.id.btn_startuse:
                String userName = String.valueOf(etUsername.getText());
                String userNum = String.valueOf(etUsernum.getText());
                Intent intent = new Intent(getActivity(), MainActivity.class);
                if (userName.isEmpty()||userNum.isEmpty()){
                    Toast.makeText(getActivity(),"姓名或学号为空，请输入",Toast.LENGTH_SHORT).show();
                }else {

                    if (bitmap==null){
                        BitmapDrawable bd = (BitmapDrawable) userIcon.getDrawable();
                        bitmap=Tool.toRoundBitmap(bd.getBitmap());
                    }
                    SharedPreferences.Editor userEditor = Tool.getEditor(getActivity(), "user");
                    userEditor.putString("username",userName);
                    userEditor.putString("usernum",userNum);
                    userEditor.commit();
                    Tool.saveBitmap(getActivity(),bitmap);

                editor.putString("shenfen","student").commit();
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
        RestClient.get("/login", password, new AsyncHttpResponseHandler() {

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
                if (loginCode.equals(LOGINSUCCESS)){
                    Toast.makeText(getActivity(),"登陆成功",Toast.LENGTH_SHORT).show();
                    editor.putString("shenfen","teacher").commit();
                    startActivity(new Intent(getActivity(), TeacherClientActivity.class));
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
