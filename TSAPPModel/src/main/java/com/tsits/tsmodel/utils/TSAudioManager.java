package com.tsits.tsmodel.utils;

import android.content.Context;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 音频管理
 */
public class TSAudioManager {

    private AudioManager audioManager;

    public TSAudioManager(Context context){
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * 获取外放设备
     * @return
     */
    public List<String> getOutPutDevice(){
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
            return null;
        }

        AudioDeviceInfo[] devList = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
        List<String> waveOutList = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();
        for (AudioDeviceInfo audioDeviceInfo : devList) {
            ids.add(audioDeviceInfo.getId());
            String aka = "";
            switch (audioDeviceInfo.getType()) {
                case AudioDeviceInfo.TYPE_BUILTIN_EARPIECE:
                    aka = "听筒";
                    break;
                case AudioDeviceInfo.TYPE_BUILTIN_SPEAKER:
                    aka = "扬声器";
                    break;
                case AudioDeviceInfo.TYPE_WIRED_HEADSET:
                    aka = "耳机设备";
                    break;
                case AudioDeviceInfo.TYPE_BLUETOOTH_A2DP:
                    aka = "蓝牙设备";
                    break;
                case AudioDeviceInfo.TYPE_LINE_ANALOG:
                    aka = "TYPE_LINE_ANALOG";
                    break;
                case AudioDeviceInfo.TYPE_USB_ACCESSORY:
                    aka = "TYPE_USB_ACCESSORY";
                    break;
                default:
                    break;
            }
            waveOutList.add(aka + " id[" + audioDeviceInfo.getId() + "]");
        }
        return waveOutList;
    }

    /**
     * 获取输入设备
     * @return
     */
    public List<String> getIntPutDevice(){
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
            return null;
        }
        AudioDeviceInfo[] devList =  audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS);
        List<String> waveOutList = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();
        for (AudioDeviceInfo audioDeviceInfo : devList) {
            ids.add(audioDeviceInfo.getId());
            String aka = "";
            switch (audioDeviceInfo.getType()) {
                case AudioDeviceInfo.TYPE_BUILTIN_MIC:
                    aka = "麦克风";
                    break;
                case AudioDeviceInfo.TYPE_WIRED_HEADSET:
                    aka = "耳机设备";
                    break;
                case AudioDeviceInfo.TYPE_BLUETOOTH_A2DP:
                    aka = "蓝牙设备";
                    break;
                case AudioDeviceInfo.TYPE_TELEPHONY:
                    aka = "TYPE_TELEPHONY";
                    break;
                default:
                    aka = "其他输入设备";
                    break;
            }
            waveOutList.add(aka + " id[" + audioDeviceInfo.getId() + "]");
        }
        return waveOutList;
    }

    /**
     * 调节系统音量
     * @param isAdd
     */
    public void adjustTheVolume(boolean isAdd){
        int sysMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int sysCurrent = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int flag = AudioManager.FLAG_SHOW_UI;
        if (isAdd) {
            if (sysCurrent == sysMax) {
                return;
            }
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, sysCurrent + 1, flag);
        } else {
            if (sysCurrent < 1) {
                return;
            }
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, sysCurrent - 1, flag);
        }
    }

}
