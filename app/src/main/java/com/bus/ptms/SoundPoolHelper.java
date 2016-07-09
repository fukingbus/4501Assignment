package com.bus.ptms;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xeonyan on 9/7/2016.
 */
public class SoundPoolHelper implements SoundPool.OnLoadCompleteListener {
    private SoundPool sPool;
    private Context context;
    private Map<String,Integer> soundMap = new HashMap<>();

    public SoundPoolHelper(Context context){
        this.context = context;
        sPool = new SoundPool(1,AudioManager.STREAM_MUSIC,0);
        sPool.setOnLoadCompleteListener(this);
        loadSoundEffects();
    }
    private void loadSoundEffects(){
        // load sound tracks resources into the HashMap with keys
        soundMap.put("click",sPool.load(context,R.raw.click,1));
        soundMap.put("correct",sPool.load(context,R.raw.correct,1));
        soundMap.put("game_over",sPool.load(context,R.raw.game_over,1));
        soundMap.put("pop",sPool.load(context,R.raw.pop,1));
        soundMap.put("time_blip",sPool.load(context,R.raw.time_blip,1));
        soundMap.put("wrong",sPool.load(context,R.raw.wrong,1));
    }

    public void playSound(String sid){
        // play sounds with id
        try {
            int id = soundMap.get(sid);
            sPool.play(id,1,1,1,0,1);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
        if(status!=0)
            Log.e("soundPool",sampleId+" not correctly loaded");
    }
}
