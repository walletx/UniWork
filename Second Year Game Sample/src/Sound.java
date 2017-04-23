import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;


public class Sound {
	
	private String filename;
	private Clip clip;
	
	/**
	 * Constructs a Sound object.
	 * @param filename - filename of the sound file. Only works with .wav files.
	 */
	public Sound(String filename) {
		this.filename = filename;
	}
	
	/**
	 * Plays a Sound object.
	 */
	public void playSound() {
	    try {
	        AudioInputStream soundStream = AudioSystem.getAudioInputStream(new File(filename));
	        clip = AudioSystem.getClip();
	        clip.open(soundStream);
	        clip.start(); 
	    } catch(Exception e) {

	    }
	}
	
	/**
	 * Clear audio stream resources.
	 */
	public void clearAudioStream() {
		try {
			clip.stop();
			clip.close();
		} catch(Exception e) {
			
		}
	}
	

	/**
	 * Delays the clearing of audio stream resources for a certain amount of time.
	 * @param delay - time for delay in milliseconds
	 */
	public void clearAfterDelay(int delay) {
		try {
			Thread.sleep(delay);
			clip.stop();
			clip.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
