package com.example.levelplaygd3;

import android.util.ArraySet;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.SignalInfo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HelloWorld extends GodotPlugin {
    public HelloWorld(Godot godot) {
        super(godot);
    }

    @NonNull
    @Override
    public String getPluginName() {
        return "HelloWorld";
    }//Oh the name is setup here
    @NonNull
    @Override
    public List<String> getPluginMethods()
    {
        return Arrays.asList("hello", "TestToast", "ShowToastMessage");
    }

    public String hello()
    {
        return "Hello World. Godot call to Android";
    }

    public void sendSignal()
    {
        emitSignal("hello", "hello this worked");
    }

    public Set<SignalInfo> getPluginSignals()
    {
        Set<SignalInfo> signals = new HashSet<>();
        signals.add(new SignalInfo("hello", String.class));
        return signals;
    }

    public void TestToast()
    {
        ShowToastMessage("Non Parameters");
    }

    public void ShowToastMessage(String message)
    {
        //How can I get this?
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
