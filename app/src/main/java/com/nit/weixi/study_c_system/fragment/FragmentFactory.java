package com.nit.weixi.study_c_system.fragment;

import android.app.Fragment;

/**
 * Created by weixi on 2016/3/30.
 */
public class FragmentFactory extends Fragment {

    public static Fragment createFragment(String name) {
        Fragment f = null;
        switch (name) {
            case "home":
                f = new HomeFragment();
                break;
            case "question":
                f = new QuestionFragment();
                break;
            case "record":
                f = new RecordFragment();
                break;
            case "answer":
                f = new AnswerFragment();
                break;
            case "setting":
                f = new SettingFragment();
                break;
            case "training":
                f = new TrainingFragment();
                break;
            case "test":
                f = new TestFragment();
                break;
            case "data":
                f = new DataFragment();
                break;
            case "task":
                f = new TaskFragment();
                break;
        }

        return f;
    }
}
