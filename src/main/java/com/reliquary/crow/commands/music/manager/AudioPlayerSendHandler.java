package com.reliquary.crow.commands.music.manager;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;

import java.nio.ByteBuffer;

/**
 * This class gets information from the guild's audio handler and provides an object to the guild for audio
 *
 * @version 2.0 2021-04-11
 * @since 1.0
 */
public class AudioPlayerSendHandler implements AudioSendHandler {

	private final AudioPlayer audioPlayer;
	private final ByteBuffer buffer;
	private final MutableAudioFrame frame;

	public AudioPlayerSendHandler(AudioPlayer audioPlayer) {
		this.audioPlayer = audioPlayer;
		this.buffer = ByteBuffer.allocate(1024);
		this.frame = new MutableAudioFrame();
		this.frame.setBuffer(buffer);
	}

	@Override
	public boolean canProvide() {
		return this.audioPlayer.provide(this.frame);
	}

	@Override
	public ByteBuffer provide20MsAudio() {
		return this.buffer.flip();
	}

	@Override
	public boolean isOpus() {
		return true;
	}
}
