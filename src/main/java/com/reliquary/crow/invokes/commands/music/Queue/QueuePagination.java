package com.reliquary.crow.invokes.commands.music.Queue;

import com.reliquary.crow.invokes.commands.music.manager.GuildMusicManager;
import com.reliquary.crow.invokes.commands.music.manager.PlayerManager;
import com.reliquary.crow.managers.componentmanagers.buttons.ButtonContext;
import com.reliquary.crow.managers.componentmanagers.buttons.ButtonInterface;
import com.reliquary.crow.resources.other.DateAndTime;
import com.reliquary.crow.resources.pagination.Pagination;
import com.reliquary.crow.resources.pagination.buttons.PageLeft;
import com.reliquary.crow.resources.pagination.buttons.PageRight;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class provide the pagination resources for the queue command
 *
 * @version 1.0 2021-16-12
 * @since 1.0
 */
public class QueuePagination {

	/**
	 * This method gets the pagination button interfaces with updated embeds
	 * @return Returns pagination buttons
	 */
	public static List<ButtonInterface> provideButtons() {

		String invoke = new Queue().getInvoke();
		int itemsPerPage = 10;

		return Arrays.asList(

			new PageLeft(invoke) {

				@Override
				public List<EmbedBuilder> setEmbedBuilderList(ButtonContext ctx) {

					if (ctx.getEvent().getGuild() == null)
						return null;

					return Pagination.buildEmbedList(
						invoke,
						buildStrings(PlayerManager.getInstance().getMusicManager(ctx.getEvent().getGuild())),
						itemsPerPage
					);
				}
			},

			new PageRight(invoke) {

				@Override
				public List<EmbedBuilder> setEmbedBuilderList(ButtonContext ctx) {

					if (ctx.getEvent().getGuild() == null)
						return null;

					return Pagination.buildEmbedList(
						invoke,
						buildStrings(PlayerManager.getInstance().getMusicManager(ctx.getEvent().getGuild())),
						itemsPerPage
					);
				}
			}
		);
	}

	/**
	 * This method builds the strings of the current music manager state
	 * @param musicManager Provides the music manager to get tracks from
	 * @return Returns a list of strings
	 */
	public static List<String> buildStrings(GuildMusicManager musicManager) {

		List<String> stringList = new ArrayList<>();

		// Check if there is nothing in the queue
		if (musicManager.audioPlayer.getPlayingTrack() == null && musicManager.scheduler.queue.isEmpty()) {

			stringList.add("The queue is currently empty!");
			return stringList;
		}

		List<String> repeatStrings = new ArrayList<>();
		if (musicManager.scheduler.repeating)
			repeatStrings.add("\uD83D\uDD01 Repeating current track");

		if (musicManager.scheduler.repeatingQueue)
			repeatStrings.add("\uD83D\uDD03 Repeating queue");

		if (repeatStrings.size() == 2)
			stringList.add(repeatStrings.get(0) + "\n" + repeatStrings.get(1));
		else if (repeatStrings.size() == 1)
			stringList.add(repeatStrings.get(0));

		// Handle the current track
		String playerState;
		if (musicManager.audioPlayer.isPaused()) {
			playerState = "`Paused:` ";
		} else {
			playerState = "`Playing:` ";
		}

		AudioTrackInfo currentTrackInfo = musicManager.audioPlayer.getPlayingTrack().getInfo();
		stringList.add(String.format(
			playerState + "[%s](%s) | `%s`",
			currentTrackInfo.title,
			currentTrackInfo.uri,
			DateAndTime.formatTime(currentTrackInfo.length)
		));

		int songNum = 1;
		for (AudioTrack track : musicManager.scheduler.queue) {

			stringList.add(String.format(
				"`%d.` [%s](%s) | `%s`",
				songNum,
				track.getInfo().title,
				track.getInfo().uri,
				DateAndTime.formatTime(track.getInfo().length)
			));

			songNum++;
		}

		return stringList;
	}
}
