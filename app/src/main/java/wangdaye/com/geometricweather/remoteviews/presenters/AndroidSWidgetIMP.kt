package wangdaye.com.geometricweather.remoteviews.presenters

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.util.SizeF
import android.widget.RemoteViews
import androidx.annotation.LayoutRes
import wangdaye.com.geometricweather.GeometricWeather
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.background.receiver.widget.WidgetAndroidSProvider
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.options.NotificationTextColor
import wangdaye.com.geometricweather.settings.SettingsManager
import wangdaye.com.geometricweather.theme.resource.ResourceHelper
import wangdaye.com.geometricweather.theme.resource.ResourcesProviderFactory

class AndroidSWidgetIMP: AbstractRemoteViewsPresenter() {

    companion object {

        @JvmStatic
        fun isEnable(context: Context): Boolean {
            return AppWidgetManager.getInstance(
                context
            ).getAppWidgetIds(
                ComponentName(
                    context,
                    WidgetAndroidSProvider::class.java
                )
            ).isNotEmpty()
        }

        @JvmStatic
        fun updateWidgetView(context: Context, location: Location) {
            AppWidgetManager.getInstance(context).updateAppWidget(
                ComponentName(context, WidgetAndroidSProvider::class.java),
                buildWeatherWidget(context, location)
            )
        }
    }
}

private fun buildWeatherWidget(
    context: Context,
    location: Location
): RemoteViews = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    RemoteViews(
        mapOf(
            SizeF(1.0f, 1.0f) to buildRemoteViews(
                context,
                location,
                R.layout.widget_s_card_1x1
            ),
            SizeF (100.0f, 100.0f) to buildRemoteViews(
                context,
                location,
                R.layout.widget_s_card_2x1
            ),
            SizeF (156.0f, 156.0f) to buildRemoteViews(
                context,
                location,
                R.layout.widget_s_card_2x2
            ),
            SizeF (192.0f, 98.0f) to buildRemoteViews(
                context,
                location,
                R.layout.widget_s_card_3x1
            ),
            SizeF (148.0f, 198.0f) to buildRemoteViews(
                context,
                location,
                R.layout.widget_s_card_3x2
            ),
            SizeF (216.0f, 100.0f) to buildRemoteViews(
                context,
                location,
                R.layout.widget_s_card_4x1
            ),
            SizeF (216.0f, 198.0f) to buildRemoteViews(
                context,
                location,
                R.layout.widget_s_card_4x2
            ),
            SizeF (216.0f, 312.0f) to buildRemoteViews(
                context,
                location,
                R.layout.widget_s_card_4x3
            ),
            SizeF (298.0f, 198.0f) to buildRemoteViews(
                context,
                location,
                R.layout.widget_s_card_5x2
            ),
            SizeF (298.0f, 312.0f) to buildRemoteViews(
                context,
                location,
                R.layout.widget_s_card_5x3
            ),
        )
    )
} else {
    buildRemoteViews(context, location, R.layout.widget_s_card_4x3)
}

private fun buildRemoteViews(
    context: Context,
    location: Location,
    @LayoutRes layoutId: Int,
): RemoteViews {

    val views = RemoteViews(
        context.packageName,
        layoutId
    )

    val weather = location.weather
    val dayTime = location.isDaylight

    val provider = ResourcesProviderFactory.getNewInstance()

    val settings = SettingsManager.getInstance(context)
    val temperatureUnit = settings.temperatureUnit

    views.setTextViewText(
        R.id.widget_s_card_background_city,
        location.getCityName(context)
    )
    if (weather == null) {
        return views
    }

    // current.

    views.setImageViewUri(
        R.id.widget_s_card_background_currentIcon,
        ResourceHelper.getWidgetNotificationIconUri(
            provider,
            weather.current.weatherCode,
            dayTime,
            false,
            NotificationTextColor.LIGHT
        )
    )

    views.setTextViewText(
        R.id.widget_s_card_background_currentTemperature,
        weather.current.temperature.getShortTemperature(context, temperatureUnit)
    )
    views.setTextViewText(
        R.id.widget_s_card_background_daytimeTemperature,
        weather.dailyForecast[0].day().temperature.getShortTemperature(context, temperatureUnit)
    )
    views.setTextViewText(
        R.id.widget_s_card_background_nighttimeTemperature,
        weather.dailyForecast[0].night().temperature.getShortTemperature(context, temperatureUnit)
    )

    views.setTextViewText(
        R.id.widget_s_card_background_weatherText,
        location.weather.current.weatherText
    )

    if (weather.current.airQuality.isValid) {
        views.setTextViewText(
            R.id.widget_s_card_background_aqiOrWind,
             "AQI - " + weather.current.airQuality.aqiText
        )
    } else {
        views.setTextViewText(
            R.id.widget_s_card_background_aqiOrWind,
            context.getString(R.string.wind) + " - "
                    + location.weather.current.wind.shortWindDescription
        )
    }

    // hourly.

    views.setTextViewText(
        R.id.widget_s_card_background_hour_1,
        weather.hourlyForecast[0].getHour(context)
    )
    views.setTextViewText(
        R.id.widget_s_card_background_hour_2,
        weather.hourlyForecast[1].getHour(context)
    )
    views.setTextViewText(
        R.id.widget_s_card_background_hour_3,
        weather.hourlyForecast[2].getHour(context)
    )
    views.setTextViewText(
        R.id.widget_s_card_background_hour_4,
        weather.hourlyForecast[3].getHour(context)
    )
    views.setTextViewText(
        R.id.widget_s_card_background_hour_5,
        weather.hourlyForecast[4].getHour(context)
    )
    views.setTextViewText(
        R.id.widget_s_card_background_hour_6,
        weather.hourlyForecast[5].getHour(context)
    )

    views.setImageViewUri(
        R.id.widget_s_card_background_hourlyIcon_1,
        ResourceHelper.getWidgetNotificationIconUri(
            provider,
            weather.hourlyForecast[0].weatherCode,
            weather.hourlyForecast[0].isDaylight,
            false,
            NotificationTextColor.LIGHT
        )
    )
    views.setImageViewUri(
        R.id.widget_s_card_background_hourlyIcon_2,
        ResourceHelper.getWidgetNotificationIconUri(
            provider,
            weather.hourlyForecast[1].weatherCode,
            weather.hourlyForecast[1].isDaylight,
            false,
            NotificationTextColor.LIGHT
        )
    )
    views.setImageViewUri(
        R.id.widget_s_card_background_hourlyIcon_3,
        ResourceHelper.getWidgetNotificationIconUri(
            provider,
            weather.hourlyForecast[2].weatherCode,
            weather.hourlyForecast[2].isDaylight,
            false,
            NotificationTextColor.LIGHT
        )
    )
    views.setImageViewUri(
        R.id.widget_s_card_background_hourlyIcon_4,
        ResourceHelper.getWidgetNotificationIconUri(
            provider,
            weather.hourlyForecast[3].weatherCode,
            weather.hourlyForecast[3].isDaylight,
            false,
            NotificationTextColor.LIGHT
        )
    )
    views.setImageViewUri(
        R.id.widget_s_card_background_hourlyIcon_5,
        ResourceHelper.getWidgetNotificationIconUri(
            provider,
            weather.hourlyForecast[4].weatherCode,
            weather.hourlyForecast[4].isDaylight,
            false,
            NotificationTextColor.LIGHT
        )
    )
    views.setImageViewUri(
        R.id.widget_s_card_background_hourlyIcon_6,
        ResourceHelper.getWidgetNotificationIconUri(
            provider,
            weather.hourlyForecast[5].weatherCode,
            weather.hourlyForecast[5].isDaylight,
            false,
            NotificationTextColor.LIGHT
        )
    )

    views.setTextViewText(
        R.id.widget_s_card_background_hourlyTemperature_1,
        weather.hourlyForecast[0].temperature.getShortTemperature(context, temperatureUnit)
    )
    views.setTextViewText(
        R.id.widget_s_card_background_hourlyTemperature_2,
        weather.hourlyForecast[1].temperature.getShortTemperature(context, temperatureUnit)
    )
    views.setTextViewText(
        R.id.widget_s_card_background_hourlyTemperature_3,
        weather.hourlyForecast[2].temperature.getShortTemperature(context, temperatureUnit)
    )
    views.setTextViewText(
        R.id.widget_s_card_background_hourlyTemperature_4,
        weather.hourlyForecast[3].temperature.getShortTemperature(context, temperatureUnit)
    )
    views.setTextViewText(
        R.id.widget_s_card_background_hourlyTemperature_5,
        weather.hourlyForecast[4].temperature.getShortTemperature(context, temperatureUnit)
    )
    views.setTextViewText(
        R.id.widget_s_card_background_hourlyTemperature_6,
        weather.hourlyForecast[5].temperature.getShortTemperature(context, temperatureUnit)
    )

    // daily.

    views.setTextViewText(
        R.id.widget_s_card_background_week_1,
        if (weather.dailyForecast[0].isToday(location.timeZone)) {
            context.getString(R.string.today)
        } else {
            weather.dailyForecast[0].getWeek(context)
        }
    )
    views.setTextViewText(
        R.id.widget_s_card_background_week_2,
        if (weather.dailyForecast[1].isToday(location.timeZone)) {
            context.getString(R.string.today)
        } else {
            weather.dailyForecast[1].getWeek(context)
        }
    )
    views.setTextViewText(
        R.id.widget_s_card_background_week_3,
        weather.dailyForecast[2].getWeek(context)
    )
    views.setTextViewText(
        R.id.widget_s_card_background_week_4,
        weather.dailyForecast[3].getWeek(context)
    )
    views.setTextViewText(
        R.id.widget_s_card_background_week_5,
        weather.dailyForecast[4].getWeek(context)
    )
    views.setTextViewText(
        R.id.widget_s_card_background_week_6,
        weather.dailyForecast[5].getWeek(context)
    )

    views.setImageViewUri(
        R.id.widget_s_card_background_dayIcon_1,
        ResourceHelper.getWidgetNotificationIconUri(
            provider,
            weather.dailyForecast[0].day().weatherCode,
            true,
            false,
            NotificationTextColor.LIGHT
        )
    )
    views.setImageViewUri(
        R.id.widget_s_card_background_dayIcon_2,
        ResourceHelper.getWidgetNotificationIconUri(
            provider,
            weather.dailyForecast[1].day().weatherCode,
            true,
            false,
            NotificationTextColor.LIGHT
        )
    )
    views.setImageViewUri(
        R.id.widget_s_card_background_dayIcon_3,
        ResourceHelper.getWidgetNotificationIconUri(
            provider,
            weather.dailyForecast[2].day().weatherCode,
            true,
            false,
            NotificationTextColor.LIGHT
        )
    )
    views.setImageViewUri(
        R.id.widget_s_card_background_dayIcon_4,
        ResourceHelper.getWidgetNotificationIconUri(
            provider,
            weather.dailyForecast[3].day().weatherCode,
            true,
            false,
            NotificationTextColor.LIGHT
        )
    )
    views.setImageViewUri(
        R.id.widget_s_card_background_dayIcon_5,
        ResourceHelper.getWidgetNotificationIconUri(
            provider,
            weather.dailyForecast[4].day().weatherCode,
            true,
            false,
            NotificationTextColor.LIGHT
        )
    )
    views.setImageViewUri(
        R.id.widget_s_card_background_dayIcon_6,
        ResourceHelper.getWidgetNotificationIconUri(
            provider,
            weather.dailyForecast[5].day().weatherCode,
            true,
            false,
            NotificationTextColor.LIGHT
        )
    )

    views.setTextViewText(
        R.id.widget_s_card_background_dayTemperature_1,
        weather.dailyForecast[0].day().temperature.getShortTemperature(context, temperatureUnit)
    )
    views.setTextViewText(
        R.id.widget_s_card_background_dayTemperature_2,
        weather.dailyForecast[1].day().temperature.getShortTemperature(context, temperatureUnit)
    )
    views.setTextViewText(
        R.id.widget_s_card_background_dayTemperature_3,
        weather.dailyForecast[2].day().temperature.getShortTemperature(context, temperatureUnit)
    )
    views.setTextViewText(
        R.id.widget_s_card_background_dayTemperature_4,
        weather.dailyForecast[3].day().temperature.getShortTemperature(context, temperatureUnit)
    )
    views.setTextViewText(
        R.id.widget_s_card_background_dayTemperature_5,
        weather.dailyForecast[4].day().temperature.getShortTemperature(context, temperatureUnit)
    )
    views.setTextViewText(
        R.id.widget_s_card_background_dayTemperature_6,
        weather.dailyForecast[5].day().temperature.getShortTemperature(context, temperatureUnit)
    )

    views.setTextViewText(
        R.id.widget_s_card_background_nightTemperature_1,
        weather.dailyForecast[0].night().temperature.getShortTemperature(context, temperatureUnit)
    )
    views.setTextViewText(
        R.id.widget_s_card_background_nightTemperature_2,
        weather.dailyForecast[1].night().temperature.getShortTemperature(context, temperatureUnit)
    )
    views.setTextViewText(
        R.id.widget_s_card_background_nightTemperature_3,
        weather.dailyForecast[2].night().temperature.getShortTemperature(context, temperatureUnit)
    )
    views.setTextViewText(
        R.id.widget_s_card_background_nightTemperature_4,
        weather.dailyForecast[3].night().temperature.getShortTemperature(context, temperatureUnit)
    )
    views.setTextViewText(
        R.id.widget_s_card_background_nightTemperature_5,
        weather.dailyForecast[4].night().temperature.getShortTemperature(context, temperatureUnit)
    )
    views.setTextViewText(
        R.id.widget_s_card_background_nightTemperature_6,
        weather.dailyForecast[5].night().temperature.getShortTemperature(context, temperatureUnit)
    )

    views.setImageViewUri(
        R.id.widget_s_card_background_nightIcon_1,
        ResourceHelper.getWidgetNotificationIconUri(
            provider,
            weather.dailyForecast[0].night().weatherCode,
            false,
            false,
            NotificationTextColor.LIGHT
        )
    )
    views.setImageViewUri(
        R.id.widget_s_card_background_nightIcon_2,
        ResourceHelper.getWidgetNotificationIconUri(
            provider,
            weather.dailyForecast[1].night().weatherCode,
            false,
            false,
            NotificationTextColor.LIGHT
        )
    )
    views.setImageViewUri(
        R.id.widget_s_card_background_nightIcon_3,
        ResourceHelper.getWidgetNotificationIconUri(
            provider,
            weather.dailyForecast[2].night().weatherCode,
            false,
            false,
            NotificationTextColor.LIGHT
        )
    )
    views.setImageViewUri(
        R.id.widget_s_card_background_nightIcon_4,
        ResourceHelper.getWidgetNotificationIconUri(
            provider,
            weather.dailyForecast[3].night().weatherCode,
            false,
            false,
            NotificationTextColor.LIGHT
        )
    )
    views.setImageViewUri(
        R.id.widget_s_card_background_nightIcon_5,
        ResourceHelper.getWidgetNotificationIconUri(
            provider,
            weather.dailyForecast[4].night().weatherCode,
            false,
            false,
            NotificationTextColor.LIGHT
        )
    )
    views.setImageViewUri(
        R.id.widget_s_card_background_nightIcon_6,
        ResourceHelper.getWidgetNotificationIconUri(
            provider,
            weather.dailyForecast[5].night().weatherCode,
            false,
            false,
            NotificationTextColor.LIGHT
        )
    )

    // pending intent.
    views.setOnClickPendingIntent(
        android.R.id.background,
        AbstractRemoteViewsPresenter.getWeatherPendingIntent(
            context,
            location,
            GeometricWeather.WIDGET_ANDROID_S_PENDING_INTENT_CODE_WEATHER
        )
    )

    return views
}