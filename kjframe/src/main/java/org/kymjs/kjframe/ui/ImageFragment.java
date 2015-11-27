package org.kymjs.kjframe.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.kymjs.kjframe.R;
import org.kymjs.kjframe.widget.photoview.PhotoView;

import org.kymjs.kjframe.tools.ToolImage;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.ui.SupportFragment;
import org.kymjs.kjframe.universalimageloader.core.assist.FailReason;
import org.kymjs.kjframe.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

/**
 * 图片查看器
 * Created by xiezuoyuan on 15/11/27.
 */
public class ImageFragment extends SupportFragment {

    @BindView(id = R.id.viewPager)
    private ViewPager viewPager;
    @BindView(id = R.id.viewPagerDots)
    private LinearLayout viewPagerDots;
    private ArrayList<String> urls;
    private int currentItem = 0;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        urls = getActivity().getIntent().getStringArrayListExtra("urls");
        currentItem = getActivity().getIntent().getIntExtra("currentItem",0);
        return View.inflate(getActivity(), R.layout.fragment_imageview, null);
    }

    @Override
    protected void initWidget(View parentView) {
        super.initWidget(parentView);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter();
        viewPager.setPageMargin((int) (getResources().getDisplayMetrics().density * 15));
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int t = 0; t < urls.size(); t++) {
                    ImageView iv_dot = (ImageView) viewPagerDots.getChildAt(t);
                    if (position == t) {
                        iv_dot.setBackgroundResource(R.drawable.shape_dots_h);
                    } else {
                        iv_dot.setBackgroundResource(R.drawable.shape_dots_n);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(currentItem);
        viewPagerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void initData() {
        super.initData();
        viewPagerDots.removeAllViews();
        for (int i = 0;i < urls.size();i++){
            LinearLayout parent = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.item_viewpager_dot, null);
            ImageView iv_dot = (ImageView) parent.findViewById(R.id.iv_dot);
            parent.removeAllViews();
            viewPagerDots.addView(iv_dot);
            if (i == 0){
                iv_dot.setBackgroundResource(R.drawable.shape_dots_h);
            }
        }
    }

    class ViewPagerAdapter extends PagerAdapter {

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int i) {
            final PhotoView photoView = new PhotoView(getActivity());
            photoView.enable();
            ToolImage.getImageLoader().loadImage(urls.get(i), new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    photoView.setImageBitmap(loadedImage);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
            container.addView(photoView);
            return photoView;
        }

        @Override
        public int getCount() {
            return urls.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }
    }
}
