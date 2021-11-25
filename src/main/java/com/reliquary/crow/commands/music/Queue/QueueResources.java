package com.reliquary.crow.commands.music.Queue;

import com.reliquary.crow.commands.music.manager.GuildMusicManager;
import com.reliquary.crow.commands.music.manager.PlayerManager;
import com.reliquary.crow.resources.other.DateAndTime;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class helps the queue command by editing the queue command from a button click event. Methods
 * control whether the left or right button are clicked
 *
 * @version 2.0 2021-04-11
 * @since 1.0
 */
public class QueueResources {

	public static final int NUM_TRACKS_PAGE = 5;

	public static void queueHandler(ButtonClickEvent event, String type) {

		final GuildMusicManager musicManager = PlayerManager.getInstance()
			.getMusicManager(event.getTextChannel().getGuild());

		int numTracks = musicManager.scheduler.queue.size();
		int numPages = (int) Math.ceil((numTracks + 1) / (double) NUM_TRACKS_PAGE);

		// Get embed from the event (there will always be 1 embed from queue command)
		MessageEmbed currentEmbed = event.getMessage().getEmbeds().get(0);

		// Get current page number
		String pageString = Objects.requireNonNull(Objects.requireNonNull(
			currentEmbed.getAuthor()).getName()).split("/")[0];
		int currentPage = Integer.parseInt(pageString.substring(pageString.length() - 1));

		// Handle moving left or right on a page
		int newPage = currentPage;

		switch (type) {
			case "queueleft":

				// Check if you're on the left-most page
				if (currentPage != 1)
					newPage = currentPage - 1;
				else
					event.deferEdit().queue();
				break;

			case "queueright":

				// Check if you're on the right-most page
				if (currentPage != numPages)
					newPage = currentPage + 1;
				else
					event.deferEdit().queue();
				break;
		}

		// Handle button embeds
		handleButton(event, newPage, numPages, musicManager);
	}

	/**
	 * This method handles editing the message embed from the event
	 * @param event Gives method event information
	 * @param pageNum Controls what page the embed should be on
	 * @param totalPages The total number of pages to show on the embed
	 * @param musicManager Provides the music manager to get the queue
	 */
	public static void handleButton(ButtonClickEvent event, int pageNum, int totalPages,
			GuildMusicManager musicManager) {

		// Base Queue embed on specific page
		EmbedBuilder builder = new EmbedBuilder()
			.setAuthor("Page " + pageNum + "/" + totalPages);
		StringBuilder descriptionBuilder =  builder.getDescriptionBuilder();

		// Build description with current queue based on page the user's on
		QueueResources.buildDescription(
			descriptionBuilder,
			getPageTracks(new ArrayList<>(musicManager.scheduler.queue), pageNum),
			musicManager.audioPlayer.getPlayingTrack().getInfo(),
			pageNum
		);

		// Edit the embed
		event.editMessageEmbeds(builder.build()).queue();
	}

	/**
	 * This method calculates the tracks depending on the page the embed is on
	 * @param trackList Provides the music manager queue in list form
	 * @param pageNum Uses page number to calculate what tracks display on the page
	 * @return Returns an audio track list
	 */
	public static List<AudioTrack> getPageTracks(List<AudioTrack> trackList, int pageNum) {

		// Get the starting track number based on page the user's on
		int startTrackNum = (pageNum - 1) * NUM_TRACKS_PAGE;

		// Pages after base page must start on the -1 track, as base page has currently playing track
		if (pageNum > 1)
			startTrackNum -= 1;

		// Track list to add tracks from the queue list
		List<AudioTrack> finalTrackList = new ArrayList<>();

		// End track is based on page number, getting the last track number of the page
		int endTrackNum = startTrackNum + (trackList.size() - startTrackNum);

		// Forces first page to only show NUM_TRACKS_PAGE - 1 queued tracks
		if (pageNum == 1)
			endTrackNum = NUM_TRACKS_PAGE;

		// Loop between startTrackNum and endTrackNum and add them to the new list
		for (int i = startTrackNum; i < endTrackNum; i++) {
			finalTrackList.add(trackList.get(i));
		}

		return finalTrackList;
	}

	/**
	 * This method builds the description of the embed given the track list and page number
	 * @param embedBuilder Uses the embed builder to append tracks to the page
	 * @param trackList Gets the list of tracks to display on the embed page
	 * @param currentTrackInfo Gets the currently playing track's info
	 * @param pageNum Gets the current page number of the embed
	 */
	public static void buildDescription(StringBuilder embedBuilder, List<AudioTrack> trackList,
			AudioTrackInfo currentTrackInfo, int pageNum) {

		AudioTrackInfo trackInfo;
		int trackSize = trackList.size();

		// Handles the current playing track
		if (pageNum == 1) {
			trackSize -= 1;

			// Appends playing track for first page
			embedBuilder.append(String.format(
				"`Playing:` [%s](%s) | `%s`\n\n",
				currentTrackInfo.title,
				currentTrackInfo.uri,
				DateAndTime.formatTime(currentTrackInfo.length)
			));
		}

		// Check if the queue is empty
		if (trackList.isEmpty())
			return;

		int songNum;
		for (int i = 0; i < trackSize; i++) {

			// Handles base page song numbers
			if (pageNum == 1) {
				songNum = i + 1;

				// Base page has current playing track, so queue must be reduced by 1
				if (i == NUM_TRACKS_PAGE - 1)
					break;
			} else
				songNum = i + (NUM_TRACKS_PAGE * (pageNum - 1));

			// Get track info and append to the builder
			trackInfo = trackList.get(i).getInfo();
			embedBuilder.append(String.format(
				"`%d.` [%s](%s) | `%s`\n\n",
				songNum,
				trackInfo.title,
				trackInfo.uri,
				DateAndTime.formatTime(trackInfo.length)
			));
		}
	}
}
