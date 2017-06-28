package com.opentext.appworks.developer.lab.services;

import com.opentext.otag.sdk.client.v3.SettingsClient;
import com.opentext.otag.sdk.handlers.AWServiceContextHandler;
import com.opentext.otag.sdk.handlers.AbstractSettingChangeHandler;
import com.opentext.otag.sdk.types.v3.api.SDKResponse;
import com.opentext.otag.sdk.types.v3.api.error.APIException;
import com.opentext.otag.sdk.types.v3.message.SettingsChangeMessage;
import com.opentext.otag.sdk.types.v3.settings.Setting;
import com.opentext.otag.sdk.types.v3.settings.SettingType;
import com.opentext.otag.sdk.types.v3.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DefaultSearchTermSettingManager extends AbstractSettingChangeHandler implements AWServiceContextHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultSearchTermSettingManager.class);

    private static final String SERVICE_NAME = "twitter-proxy-service";
    private static final String DEFAULT_SEARCH_TERM_SETTING = SERVICE_NAME + ".defaultSearchTerm";
    private static final String SETTING_NAME = "Default Twitter Search Term";
    private static final String SETTING_CREATED_ERR_MSG = "Failed to create \"" + SETTING_NAME +
            "\" setting, default value will be used unless supplied by client";

    private String defaultSearchTerm = "Wayne Gretzky";

    private SettingsClient settingsClient;

    @Override
    public void onStart(String appName) {
        try {
            // create the client when it is safe to do so
            settingsClient = new SettingsClient();
            if (!isSettingDefined()) {
                LOG.info("Attempting to create ");
                Setting defaultSearchTermSetting = new Setting(
                        DEFAULT_SEARCH_TERM_SETTING, SERVICE_NAME, SettingType.string,
                        SETTING_NAME, defaultSearchTerm, defaultSearchTerm,
                        "The term used by the API when no search term query parameter is provided",
                        false, "1");
                SDKResponse setting = settingsClient.createSetting(defaultSearchTermSetting);
                if (!setting.isSuccess()) {
                    LOG.error(SETTING_CREATED_ERR_MSG + " - {}", setting.getSdkCallInfo());
                }
            }
        } catch (APIException e) {
            LOG.error(SETTING_CREATED_ERR_MSG, e);
        }
    }

    @Override
    public String getSettingKey() {
        return DEFAULT_SEARCH_TERM_SETTING;
    }

    @Override
    public void onSettingChanged(SettingsChangeMessage message) {
        String settingVal = message.getNewValue();
        LOG.info("Updated setting value received - {}", settingVal);
        this.defaultSearchTerm = settingVal;
    }

    public String getDefaultSearchTerm() {
        return defaultSearchTerm;
    }

    @Override
    public void onStop(String appName) {
        // no-op
    }

    private boolean isSettingDefined() {
        Settings settings = settingsClient.getSettings();
        return settings.getSettings()
                .stream()
                .anyMatch(setting -> DEFAULT_SEARCH_TERM_SETTING.equals(setting.getKey()));
    }

}
