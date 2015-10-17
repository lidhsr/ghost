package com.ytx.activity;

import android.view.View;
import android.widget.RadioButton;

import com.ytx.R;

import org.kymjs.kjframe.ui.BindView;

/**
 * Created by Augustus on 15/10/17.
 */
public class HomeActivity extends TitleBarActivity {

    @BindView(id = R.id.bottombar_content1, click = true)
    private RadioButton mRbtnContent1;
    @BindView(id = R.id.bottombar_content2, click = true)
    private RadioButton mRbtnContent2;
    @BindView(id = R.id.bottombar_content3, click = true)
    private RadioButton mRbtnContent3;
    @BindView(id = R.id.bottombar_content4, click = true)
    private RadioButton mRbtnContent4;
    @BindView(id = R.id.bottombar_content5, click = true)
    private RadioButton mRbtnContent5;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_home);
    }

    @Override
    public void initData() {
        super.initData();
    }

    @Override
    public void initWidget() {
        super.initWidget();
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
        switch(v.getId()) {

        }
    }
}
