package model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Wrapperclass for game audio
 * @author antje
 *
 */

public class Audio {
	private Clip clip;

    protected Audio() {
    }

    public Audio(File source) throws LineUnavailableException, MalformedURLException, IOException, UnsupportedAudioFileException {
        this(source.toURI().toURL());
    }

    public Audio(URL source) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        this(source.openStream());
    }

    public Audio(InputStream source) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        init(source);
    }

    protected void init(File source) throws LineUnavailableException, MalformedURLException, IOException, UnsupportedAudioFileException {
        init(source.toURI().toURL());
    }

    protected void init(URL source) throws IOException, LineUnavailableException, UnsupportedAudioFileException {
        init(source.openStream());
    }

    protected void init(InputStream source) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        clip = AudioSystem.getClip();
        clip.open(AudioSystem.getAudioInputStream(source));
    }

    public void setRepeats(boolean repeats) {
        clip.loop(repeats ? Clip.LOOP_CONTINUOUSLY : 1);
    }

    public void reset() {
        clip.stop();
        clip.setFramePosition(0);
    }

    public void play() {
        clip.start();
    }

    public void stop() {
        clip.stop();
    }

    public boolean isPlaying() {
        return clip.isActive();
    }
}
