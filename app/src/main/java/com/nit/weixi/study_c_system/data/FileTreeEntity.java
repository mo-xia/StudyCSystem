package com.nit.weixi.study_c_system.data;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class FileTreeEntity {

        public static final String MODE_TREE = "tree";
        public static final String MODE_FILE = "file";

        private String path;// 文件路径
        private String rname;//原来名称
        private String name;//拼音后的名称
        private String mode;//类型
        private List<FileTreeEntity> files;

    /**
     * 生成一个上级目录("..")
     *
     * @return
     */
    public static FileTreeEntity getParentDirectory() {
        FileTreeEntity f = new FileTreeEntity();
        f.setPath(null);
        f.setRname("..");
        f.setName("..");
        f.setMode(MODE_TREE);
        return f;
    }

    public static FileTreeEntity create(String json) {
        return new Gson().fromJson(json, FileTreeEntity.class);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRname() {
        return rname;
    }

    public void setRname(String rname) {
        this.rname = rname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public List<FileTreeEntity> getFiles() {
        return files;
    }

    public void setFiles(List<FileTreeEntity> files) {
        this.files = files;
    }

    @Override public String toString() {
        return "FileTreeEntity{" +
                "files=" + files +
                ", mode='" + mode + '\'' +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", rname='" + rname + '\'' +
                '}';
    }
}
