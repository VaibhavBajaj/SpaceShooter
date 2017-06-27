package com.keepkoding;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;

class MusicLoader {

    static AudioClip loadClip(String name) {
        AudioClip clip;
        URL url = MusicLoader.class.getResource("/music/" + name);
        try {
             clip = Applet.newAudioClip(url);

        } catch (Exception e) {
            throw new RuntimeException("Could not load music clip '"
                    + name + "': '" + e.getMessage() + ".");
        }
        return clip;
    }

}