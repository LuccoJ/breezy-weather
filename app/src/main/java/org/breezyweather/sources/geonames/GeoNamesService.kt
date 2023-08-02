package org.breezyweather.sources.geonames

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.core.Observable
import org.breezyweather.BuildConfig
import org.breezyweather.R
import org.breezyweather.common.basic.models.Location
import org.breezyweather.common.exceptions.ApiKeyMissingException
import org.breezyweather.common.exceptions.ApiLimitReachedException
import org.breezyweather.common.exceptions.LocationSearchException
import org.breezyweather.common.preference.EditTextPreference
import org.breezyweather.common.preference.Preference
import org.breezyweather.common.source.ConfigurableSource
import org.breezyweather.common.source.HttpSource
import org.breezyweather.common.source.LocationSearchSource
import org.breezyweather.settings.SettingsManager
import org.breezyweather.settings.SourceConfigStore
import retrofit2.Retrofit
import javax.inject.Inject

class GeoNamesService @Inject constructor(
    @ApplicationContext context: Context,
    client: Retrofit.Builder
) : HttpSource(), LocationSearchSource, ConfigurableSource {

    override val id = "geonames"
    override val name = "GeoNames"
    override val privacyPolicyUrl = ""

    override val locationSearchAttribution = "GeoNames CC BY 4.0"

    private val mApi by lazy {
        client
            .baseUrl(GEO_NAMES_BASE_URL)
            .build()
            .create(GeoNamesApi::class.java)
    }

    override fun requestLocationSearch(
        context: Context,
        query: String
    ): Observable<List<Location>> {
        if (!isConfigured) {
            return Observable.error(ApiKeyMissingException())
        }
        val apiKey = getApiKeyOrDefault()
        val languageCode = SettingsManager.getInstance(context).language.codeAlt
        return mApi.getLocation(
            query,
            fuzzy = 0.8,
            maxRows = 20,
            apiKey,
            style = "FULL"
        ).map { results ->
            if (results.status != null) {
                when (results.status.value) {
                    15 -> emptyList() // No result
                    18, 19, 20 -> throw ApiLimitReachedException() // Hourly, daily, weekly limit
                    else -> throw LocationSearchException()
                }
            } else {
                val locationList = mutableListOf<Location>()
                results.geonames?.forEach {
                    val locationConverted = convert(it, languageCode)
                    if (locationConverted != null) {
                        locationList.add(locationConverted)
                    }
                }
                locationList
            }
        }
    }

    // CONFIG
    private val config = SourceConfigStore(context, id)
    private var apikey: String
        set(value) {
            config.edit().putString("apikey", value).apply()
        }
        get() = config.getString("apikey", null) ?: ""

    private fun getApiKeyOrDefault(): String {
        return apikey.ifEmpty { BuildConfig.GEO_NAMES_KEY }
    }

    override val isConfigured
        get() = getApiKeyOrDefault().isNotEmpty()

    override fun getPreferences(context: Context): List<Preference> {
        return listOf(
            EditTextPreference(
                titleId = R.string.settings_source_geonames_api_key,
                summary = { c, content ->
                    content.ifEmpty {
                        c.getString(R.string.settings_source_default_value)
                    }
                },
                content = apikey,
                onValueChanged = {
                    apikey = it
                }
            )
        )
    }

    companion object {
        private const val GEO_NAMES_BASE_URL = "https://secure.geonames.org/"
    }
}