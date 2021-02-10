package wangdaye.com.geometricweather.remoteviews.config;

import android.view.View;
import android.widget.RemoteViews;

import wangdaye.com.geometricweather.R;
import wangdaye.com.geometricweather.remoteviews.presenter.TextWidgetIMP;

/**
 * Text widget config activity.
 * */

public class TextWidgetConfigActivity extends AbstractWidgetConfigActivity {

    @Override
    public void initView() {
        super.initView();
        mTextColorContainer.setVisibility(View.VISIBLE);
        mTextSizeContainer.setVisibility(View.VISIBLE);
        mAlignEndContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public RemoteViews getRemoteViews() {
        return TextWidgetIMP.getRemoteViews(this, getLocationNow(), textColorValueNow, textSize, alignEnd);
    }

    @Override
    public String getSharedPreferencesName() {
        return getString(R.string.sp_widget_text_setting);
    }
}