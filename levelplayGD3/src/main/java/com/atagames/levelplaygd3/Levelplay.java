package com.atagames.levelplaygd3;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.sdk.LevelPlayInterstitialListener;
import com.ironsource.mediationsdk.sdk.LevelPlayRewardedVideoListener;

import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.SignalInfo;
import org.godotengine.godot.plugin.UsedByGodot;

import java.util.HashSet;
import java.util.Set;

public class Levelplay extends GodotPlugin {
    private int instance_id = -1;
    private String godotMethodName = "";

    public boolean Initialize = false;
    private String userCountry;
    //If user is in Europe SetTrue
    private boolean IsGDPRuser;
    //If user is in USA SetTrue
    private boolean IsCOPPAUser;
    public static String Signal_SetInterstitialAvailable = "SetInterstitialAvailable";
    public static String Signal_Interstitial_onAdClosed = "Interstitial_onAdClosed";
    public static String Signal_GetConsentSettings = "GetConsentSettings";

    public Levelplay(Godot godot) {
        super(godot);
        Log.d("Levelplay", "Plugin level play constructor");
        GatherConsentSettings();
    }

    @UsedByGodot
    public void SetupLevelplay(boolean debugMode) {
        if (debugMode)
            IronSource.setMetaData("is_test_suite", "enable");
    }

    @NonNull
    @Override
    public String getPluginName() {
        return "Levelplay";
    }//Oh the name is setup here

    @UsedByGodot
    public void sendSignal() {
        emitSignal("hello", "hello this worked");
    }

    @NonNull
    @Override
    public Set<SignalInfo> getPluginSignals() {
        Set<SignalInfo> signals = new HashSet<>();
        signals.add(new SignalInfo("hello", String.class));
        return signals;
    }

    @UsedByGodot
    private void GatherConsentSettings() {
        String deviceCountry = CountryUtils.getDeviceCountryOrigin();
        CountryUtils.getUserCountry(getActivity(), new CountryUtils.CountryCallback() {
            @Override
            public void onCountryResult(String countryResult) {
                if (countryResult != null) {
                    Log.d("Country", "User countryResult is: " + countryResult);
                    userCountry = countryResult;
                    // Use the countryResult value here
                } else {
                    Log.d("Country", "Could not determine user countryResult");
                    if (CountryUtils.getSimCountryIso(getActivity()) != null) {
                        Log.v("Country", "Using SIM country info");
                        userCountry = CountryUtils.getSimCountryIso(getActivity());
                    } else {
                        Log.v("Country", "Using device Origin Country");
                        userCountry = deviceCountry;
                    }
                    Log.v("LevelPlay", "Country " + userCountry);
                    IsGDPRuser = CountryUtils.isEuropeanCountry(userCountry);
                    IsCOPPAUser = CountryUtils.isCoppaCountry(userCountry);
                }
            }
        });
        //emitSignal(Signal_GetConsentSettings, isGDPR, isCOPPA);
    }

    @UsedByGodot
    public boolean IsGDPRUser() {
        return IsGDPRuser;
    }

    @UsedByGodot
    public boolean IsCOPPAUser() {
        return IsCOPPAUser;
    }

    public void SubmitConsent(int userAge) {
        //COPPA
        if (CountryUtils.isCoppaCountry(this.userCountry) && userAge < 13) {
            IronSource.setMetaData("is_child_directed", "true");
        } else {
            IronSource.setMetaData("is_child_directed", "false");
        }
        //GDPR
        if (CountryUtils.isEuropeanCountry(userCountry)) {
            //SetConsentGDPR(age >= 16);
            IronSource.setConsent(userAge >= 16);
        }
    }

    @UsedByGodot
    public void LaunchTestSuite() {
        IronSource.launchTestSuite(getActivity());
    }

    @Override
    public void onMainResume() {
        super.onMainResume();
        IronSource.onResume(getActivity());
    }

    @Override
    public void onMainPause() {
        super.onMainPause();
        IronSource.onPause(getActivity());
    }

    @UsedByGodot
    private void InitIronSource(int instance_id, String APP_KEY) {
        if (!Initialize) {
            this.instance_id = instance_id;
            IronSource.shouldTrackNetworkState(getActivity(), true);
            runOnUiThread(() -> IronSource.init(getActivity(), APP_KEY, () -> {
                Initialize = true;
                InitializeRewardVideoListener();
                InitializeInterstitialListener();
                LoadInterstitial();
                LoadRewardVideo();
                ShowToastDebug("Iron source Initialized");
            }));
        } else {
            ShowToast("Iron source is ready", Toast.LENGTH_SHORT);
        }
    }

    private void InitializeRewardVideoListener() {
        IronSource.setLevelPlayRewardedVideoListener(new LevelPlayRewardedVideoListener() {
            @Override
            public void onAdAvailable(AdInfo adInfo) {
                ShowToastDebug("Reward video ready");
            }

            @Override
            public void onAdUnavailable() {
            }

            @Override
            public void onAdOpened(AdInfo adInfo) {
            }

            @Override
            public void onAdShowFailed(IronSourceError ironSourceError, AdInfo adInfo) {
            }

            @Override
            public void onAdClicked(Placement placement, AdInfo adInfo) {
            }

            @Override
            public void onAdRewarded(Placement placement, AdInfo adInfo) {
            }

            @Override
            public void onAdClosed(AdInfo adInfo) {
                ShowToast("Android: Reward closed");
            }
        });
    }

    private void InitializeInterstitialListener() {
        IronSource.setLevelPlayInterstitialListener(new LevelPlayInterstitialListener() {
            @Override
            public void onAdReady(AdInfo adInfo) {
                ShowToastDebug("Interstitial is Ready");
                //InterstitialAvailable = true;
                //emitSignal(Signal_SetInterstitialAvailable, Boolean.TRUE);
            }

            @Override
            public void onAdLoadFailed(IronSourceError ironSourceError) {
                //emitSignal(Signal_SetInterstitialAvailable, Boolean.FALSE);
            }

            @Override
            public void onAdOpened(AdInfo adInfo) {
            }

            @Override
            public void onAdShowSucceeded(AdInfo adInfo) {
            }

            @Override
            public void onAdShowFailed(IronSourceError ironSourceError, AdInfo adInfo) {
            }

            @Override
            public void onAdClicked(AdInfo adInfo) {
            }

            @Override
            public void onAdClosed(AdInfo adInfo) {
                //InterstitialAvailable = false;
                //emitSignal(Signal_SetInterstitialAvailable, Boolean.FALSE);
                //emitSignal(Signal_Interstitial_onAdClosed);
                LoadInterstitial();
            }
        });
    }

    public boolean IsInitialize() {
        if (Initialize) return true;
        Log.d("GodotIronSource", "The Plugin is not Initialized");
        return false;
    }

    @UsedByGodot
    private boolean IsInterstitialReady() {
        return IsInitialize() && IronSource.isInterstitialReady();
    }

    @UsedByGodot
    private boolean IsRewardVideoReady() {
        return IsInitialize() && IronSource.isRewardedVideoAvailable();
    }

    private void LoadRewardVideo() {
        if (!IsRewardVideoReady())
            IronSource.loadRewardedVideo();
    }

    private void LoadInterstitial() {
        if (!IsInterstitialReady())
            IronSource.loadInterstitial();
    }

    private void ShowToast(String message, int duration) {
        runOnUiThread(() -> {
            Toast.makeText(getActivity(), message, duration).show();
            Log.v(getPluginName(), message);
        });
    }

    @UsedByGodot
    private void ShowInterstitial() {
        if (IsInitialize() && IsInterstitialReady()) {
            ShowToastDebug("Show Interstitial here");
            IronSource.showInterstitial();
        } else {
            ShowToastDebug("Interstitial not available");
            LoadInterstitial();
        }
    }

    @UsedByGodot
    private void ShowRewardVideAd() {

    }

    @UsedByGodot
    private void ShowToast(String message) {
        ShowToast(message, Toast.LENGTH_SHORT);
    }

    @UsedByGodot
    private void ShowToastDebug(String message) {
        if (org.godotengine.godot.BuildConfig.DEBUG) {
            ShowToast(message);
        }
    }
}
