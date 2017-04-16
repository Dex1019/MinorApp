package com.orpheusdroid.screenrecorder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.orpheusdroid.screenrecorder.folderpicker.FolderChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;


public class SettingsPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener
        , PermissionResultListener {

    SharedPreferences prefs;
    private CheckBoxPreference recaudio;
    private CheckBoxPreference floatingControl;
    private FolderChooser dirChooser;
    private MainActivity activity;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        //init permission listener callback
        setPermissionListener();


        //Get Default save location from shared preference
        String defaultSaveLoc = (new File(Environment
                .getExternalStorageDirectory() + File.separator + Const.APPDIR)).getPath();


        //Get instances of all preferences
        prefs = getPreferenceScreen().getSharedPreferences();
//        Resolution
        ListPreference res = (ListPreference) findPreference(getString(R.string.res_key));
//

        ListPreference fps = (ListPreference) findPreference(getString(R.string.fps_key));

        //   ListPreference bitrate = (ListPreference) findPreference(getString(R.string.bitrate_key));

        recaudio = (CheckBoxPreference) findPreference(getString(R.string.audiorec_key));

//        ListPreference filenameFormat = (ListPreference) findPreference(getString(R.string.filename_key));

        EditTextPreference filenamePrefix = (EditTextPreference) findPreference(getString(R.string.fileprefix_key));

        dirChooser = (FolderChooser) findPreference(getString(R.string.savelocation_key));

        floatingControl = (CheckBoxPreference) findPreference(getString(R.string.preference_floating_control_key));


        //Set previously chosen directory as initial directory
        dirChooser.setCurrentDir(getValue(getString(R.string.savelocation_key), defaultSaveLoc));

        //Set the summary of preferences dynamically with user choice or default if no user choice is made
        updateResolution(res);
        fps.setSummary(getValue(getString(R.string.fps_key), "30"));
//
//        float bps = bitsToMb(Integer.parseInt(getValue(getString(R.string.bitrate_key), "7130317")));
        //    bitrate.setSummary(bps + " Mbps");

        dirChooser.setSummary(getValue(getString(R.string.savelocation_key), defaultSaveLoc));


        //  filenameFormat.setSummary(getFileSaveFormat());

        filenamePrefix.setSummary(getValue(getString(R.string.fileprefix_key), "recording"));

        //If record audio checkbox is checked, check for record audio permission
        if (recaudio.isChecked())
            requestAudioPermission();

        //If floating controls is checked, check for system windows permission
        if (floatingControl.isChecked())
            requestSystemWindowsPermission();

    }


    private void updateResolution(ListPreference res) {
        String resolution = getResolution(getValue(getString(R.string.res_key), "1440x2560"));
        res.setValue(resolution);
        res.setSummary(resolution);
    }


    private void updateFps(ListPreference fps) {
        String fps_video = getValue(getString(R.string.fps_key), "30");
        fps.setValue(fps_video);
        fps.setSummary(fps_video);

    }


    //Prevent upscaling of resolution which mediarecorder could not handle
    private String getResolution(String res) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager window = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        window.getDefaultDisplay().getMetrics(metrics);
        String[] widthHeight = res.split("x");
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        if (width < Integer.parseInt(widthHeight[0]) && height < Integer.parseInt(widthHeight[1])) {
            ArrayList<String> resolutions = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.resolutionValues)));
            for (String resolution : resolutions) {
                if (resolution.contains(String.valueOf(width))) {
                    Toast.makeText(getActivity(), getString(R.string.large_resolution_selected_toast, resolution)
                            , Toast.LENGTH_SHORT).show();
                    return resolution;
                }
            }
            return resolutions.get(0);
        } else
            return res;
    }


    //Set permissionListener in MainActivity
    private void setPermissionListener() {
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            activity = (MainActivity) getActivity();
            activity.setPermissionResultListener(this);
        }
    }


    //method to return string from SharedPreferences
    private String getValue(String key, String defVal) {
        return prefs.getString(key, defVal);
    }


    //Method to convert bits to MB
    private float bitsToMb(float bps) {
        return bps / (1024 * 1024);
    }


    //Register for OnSharedPreferenceChangeListener when the fragment resumes
    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }


    //Unregister for OnSharedPreferenceChangeListener when the fragment pauses
    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    //When user changes preferences, update the summary accordingly
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Preference pref = findPreference(s);
        if (pref == null) return;
        switch (pref.getTitleRes()) {
            case R.string.preference_resolution_title:
                updateResolution((ListPreference) pref);
                break;

//            case R.string.preference_bit_title:
//                float bps = bitsToMb(Integer.parseInt(getValue(getString(R.string.bitrate_key), "7130317")));
//                pref.setSummary(bps + " Mbps");
//                break;

//            case R.string.preference_filename_format_title:
//                pref.setSummary(getFileSaveFormat());
//                break;
            case R.string.preference_fps_title:
                updateFps((ListPreference) pref);
                break;

            case R.string.preference_audio_record_title:
                requestAudioPermission();
                break;

            case R.string.preference_filename_prefix_title:
                EditTextPreference etp = (EditTextPreference) pref;
                etp.setSummary(etp.getText());
//                ListPreference filename = (ListPreference) findPreference(getString(R.string.filename_key));
//                filename.setSummary(getFileSaveFormat());
                break;

            case R.string.preference_floating_control_title:
                requestSystemWindowsPermission();
                break;
        }
    }


    //Method to concat file prefix with dateTime format
    public String getFileSaveFormat() {
//        String filename = prefs.getString(getString(R.string.filename_key), "yyyyMMdd_hhmmss");
        String prefix = prefs.getString(getString(R.string.fileprefix_key), "recording");
//        return prefix + "_" + filename;
        return prefix;
    }


    public void requestAudioPermission() {
        if (activity != null) {
            activity.requestPermissionAudio();
        }
    }


    private void requestSystemWindowsPermission() {
        if (activity != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestSystemWindowsPermission();
        } else {
            Log.d(Const.TAG, "API is < 23");
        }
    }


    //Show snackbar with permission Intent when the user rejects write storage permission
    private void showSnackbar() {
        Snackbar.make(getActivity().findViewById(R.id.fab), R.string.snackbar_storage_permission_message,
                Snackbar.LENGTH_INDEFINITE).setAction(R.string.snackbar_storage_permission_action_enable,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (activity != null) {
                            activity.requestPermissionStorage();
                        }
                    }
                }).show();
    }


    private void showPermissionDeniedDialog() {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.alert_permission_denied_title)
                .setMessage(R.string.alert_permission_denied_message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (activity != null) {
                            activity.requestPermissionStorage();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showSnackbar();
                    }
                })
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setCancelable(false)
                .create().show();
    }


    //Permission result callback to process the result of Marshmallow style permission request
    @Override
    public void onPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Const.EXTDIR_REQUEST_CODE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_DENIED)) {
                    Log.d(Const.TAG, "Storage permission denied. Requesting again");
                    dirChooser.setEnabled(false);
                    showPermissionDeniedDialog();
                } else if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    dirChooser.setEnabled(true);
                }
                return;
            case Const.AUDIO_REQUEST_CODE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.d(Const.TAG, "Record audio permission granted.");
                    recaudio.setChecked(true);
                } else {
                    Log.d(Const.TAG, "Record audio permission denied");
                    recaudio.setChecked(false);
                }
                return;
            case Const.SYSTEM_WINDOWS_CODE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.d(Const.TAG, "System Windows permission granted");
                    floatingControl.setChecked(true);
                } else {
                    Log.d(Const.TAG, "System Windows permission denied");
                    floatingControl.setChecked(false);
                }
            default:
                Log.d(Const.TAG, "Unknown permission request with request code: " + requestCode);
        }
    }
}
