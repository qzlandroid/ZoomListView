package cn.jit.zoomlistview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import cn.jit.zoomlistview.ui.ZoomListView;
import cn.jit.zoomlistview.util.Cheeses;

public class MainActivity extends AppCompatActivity {

    private ZoomListView mList;
    private View mHeaderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mList = (ZoomListView) findViewById(R.id.lv);
        mList.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mHeaderView = View.inflate(this, R.layout.view_header, null);
        final ImageView mImage = (ImageView) mHeaderView.findViewById(R.id.iv);
        mList.addHeaderView(mHeaderView);
        mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                mHeaderView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mList.setZoomImage(mImage);
            }
        });
        mList.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, Cheeses.NAMES));
    }
}
