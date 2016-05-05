package com.nit.weixi.study_c_system.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.nit.weixi.study_c_system.R;
import com.nit.weixi.study_c_system.data.FileTreeAdapter;
import com.nit.weixi.study_c_system.data.FileTreeEntity;
import com.nit.weixi.study_c_system.tools.DownUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件目录
 */
public class FileTreeActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private String root = ""; //根目录
    private String cur_path = "";  //当前路径
    private FileTreeEntity mFileTreeObject;
    private String module; //模块

    ListView lv;
    private List<FileTreeEntity> data;
    private FileTreeAdapter adapter;

    String MD_PATH;  //markdown源文件的路径
    String lang;  //模块的标记 该项目下为“c”
    String rname;  //未转文件名之前的名字 中文的
    String rootRName;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acty_filetree);
        //设置toolbar和返回图标
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lv= (ListView) findViewById(R.id.lv_filetree);
        init(); //初始化数据
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
    }

    /**
     * 初始化数据
     */
    private void init() {
        MD_PATH = DownUtils.getMdPath(this);  //该应用程序md文件根路径
        lang = "c";
        cur_path = root = MD_PATH + File.separator + lang; //设置当前路径和根路径

        getFileTreeInfo(); // 初始化一个文件目录信息 mFileTreeObject

        //把文件目录信息封装成FileTreeEntity对象添加到data里
        data = new ArrayList<FileTreeEntity>();
        if (mFileTreeObject != null) {
            data.addAll(mFileTreeObject.getFiles());
        }
        adapter = new FileTreeAdapter(this, data); //把data设置给adapter

        // 获得该层的目录名称
        module = root.substring(root.lastIndexOf("/") + 1, root.length());
        rname=module.substring(0, 1).toUpperCase() + module.substring(1);
        rootRName=rname;
        getSupportActionBar().setTitle(rname); //将目录名称设置给toolbar的title
    }

    /**
     * 读取当前目录下的info.json文件
     */
    private void getFileTreeInfo() {

        String infoFile = cur_path + File.separator + "info.json";
        if(new File(infoFile).exists()){
            //读取info.json文件并转换成字符串 给FileTreeEntity的create方法
            //然后创建出 FileTreeEntity的一个实例 并赋值给 mFileTreeObject
            mFileTreeObject = FileTreeEntity.create(DownUtils.readFile(infoFile));
        }else{
            Toast.makeText(this,"目录出错:不存在该文件",Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * 更新目录结构
     */
    private void updateTree() {
        data.clear(); //清空保存目录信息的data集合
        if (!cur_path.equals(root)) { // 当前目录非根目录情况下
            //获得其父文件夹
            data.add(FileTreeEntity.getParentDirectory());
        }
        getFileTreeInfo();
        data.addAll(mFileTreeObject.getFiles());
        adapter.notifyDataSetChanged();

        getSupportActionBar().setTitle(rname);
    }

    /**
     * 根据点击的位置 响应事件并刷新ui
     * @param parent AdapterView
     * @param view 当前listView
     * @param position 当前item的位置
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 返回上一级
        if ("..".equals(data.get(position).getName())) {
            cur_path = new File(cur_path).getParent();
            rname=rootRName; //因为只有两级 既父目录为根目录 如果不止两级 这个就会出错
            updateTree();
        } else {
            File f = new File(cur_path + File.separator, data.get(position)
                    .getName());
            if (f.isDirectory()) { //如果当前目录是文件夹
                //则 进入文件夹
                cur_path = f.getAbsolutePath();
                rname= DownUtils.getFileNameWithouExtAndNumber(data.get(
                        position).getRname());
                updateTree();
            } else { //如果当前目录是文件
                // 则 显示这个文件
                /**
                 * link : 当前点击条目的路径
                 * title : 点击条目的名字
                 */
                String link = cur_path + File.separator
                        + data.get(position).getName();
                //System.out.println("link: "+link);
                String title = DownUtils.getResouceTitle(data.get(position).getRname());
                Intent intent = new Intent(this, DetailActivity.class);
                intent.putExtra(DetailActivity.EXTRA_LINK, link);
                intent.putExtra(DetailActivity.EXTRA_TITLE, title);
                //开启详情界面activity
                startActivity(intent);
            }
        }
    }


    /**
     * 处理toolbar上返回图标的响应事件
     * @param item 返回图标
     * @return 处理
     */
    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // 这个id既返回图标的id
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
