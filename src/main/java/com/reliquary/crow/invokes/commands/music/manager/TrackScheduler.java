package com.reliquary.crow.invokes.commands.music.manager;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class provides overrides methods from AudioEventAdapter to control how tracks are queued and played
 *
 * @version 2.2 2022-07-02
 * @since 1.0
 */
public class TrackScheduler extends AudioEventAdapter {

	public final AudioPlayer player;
	public final BlockingQueue<AudioTrack> queue;
	public boolean repeating = false;
	public boolean repeatingQueue = false;

	public TrackScheduler(AudioPlayer player) {
		this.player = player;
		this.queue = new LinkedBlockingQueue<>();
	}

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {

		// Check if the audio player can play the next track
		if (endReason.mayStartNext) {
			if (this.repeating) {
				this.player.startTrack(track.makeClone(), false);
				return;
			} else if (this.repeatingQueue) {
				this.queue.offer(track.makeClone());
				return;
			}

			nextTrack();
		}
	}

	/**
	 * This method queues a given track
	 * @param track Provides the track to queue
	 */
	public void queue(AudioTrack track) {

		// If no track has started, queue the next track
		if (!this.player.startTrack(track, true)) {
			this.queue.offer(track);
		}
	}

	/**
	 * This method gets the track at the start of the queue and plays it
	 */
	public void nextTrack() {
		this.player.startTrack(this.queue.poll(), false);
	}
}
