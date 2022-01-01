package com.reliquary.crow.invokes.commands.dnd;

import com.reliquary.crow.invokes.slashcommands.dnd.DnD.DnD;
import com.reliquary.crow.managers.commands.CommandContext;
import com.reliquary.crow.managers.commands.CommandInterface;
import com.reliquary.crow.resources.dnd.CharacterFetchInfo;
import com.reliquary.crow.resources.dnd.CharacterManager;
import com.reliquary.crow.resources.jda.MessageResources;
import com.reliquary.crow.resources.other.RandomColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Arrays;
import java.util.List;

/**
 * This class lets the user delete a character from their user profile directory
 *
 * @version 1.1 2021-31-12
 * @since 1.1
 */
public class DeleteCharacter implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {

		final TextChannel textChannel = ctx.getChannel();
		final String userId = ctx.getMember().getId();

		if (!CharacterManager.hasProfile(userId)) {
			MessageResources.timedMessage(
				"You don't have any characters yet! Refer to `/" + new DnD().getInvoke() + "` for help",
				textChannel,
				10
			);
			return;
		}

		if (ctx.getArgs().size() != 1) {
			MessageResources.timedMessage(
				"Please provide a valid character number to remove from your profile",
				textChannel,
				10
			);
			return;
		}

		int characterNum;
		int characterListNum = CharacterManager.getCharacterList(userId).size();
		try {
			characterNum = Integer.parseInt(ctx.getArgs().get(0));

			if (characterNum < 0)
				throw new NumberFormatException("Number must be greater than 0");

			if (characterNum > characterListNum)
				throw new NumberFormatException("Number must be a number that exists on user's profile");
		} catch (NumberFormatException e) {
			MessageResources.timedMessage(
				"Please provide a valid character number to remove from your profile",
				textChannel,
				10
			);
			return;
		}

		CharacterFetchInfo fetchInfo = new CharacterFetchInfo(CharacterManager.getSheetId(userId,
			characterNum));

		String characterName = fetchInfo.characterName();

		if (!CharacterManager.deleteCharacter(ctx.getMember().getId(), characterNum)) {
			MessageResources.timedMessage(
				"An error occurred while deleting your character. Contact developer for help.",
				textChannel,
				10
			);
			return;
		}

		EmbedBuilder builder = new EmbedBuilder()
			.setAuthor(characterName + " has been removed from your profile!", null,
				ctx.getMember().getUser().getAvatarUrl())
			.setColor(RandomColor.getRandomColor());
		StringBuilder descriptionBuilder = builder.getDescriptionBuilder();

		characterListNum = CharacterManager.getCharacterList(userId).size();

		String characterPlurality = "character";
		if (characterListNum != 1)
			characterPlurality += "s";

		descriptionBuilder
			.append("You now have ")
			.append(characterListNum)
			.append(" ")
			.append(characterPlurality);

		if (characterListNum == 0) {
			descriptionBuilder
				.append("\n\nYour profile will be deleted (for now!)");

			if (!CharacterManager.deleteProfile(userId)) {
				MessageResources.timedMessage(
					"An error occurred while deleting your profile. Contact developer for help.",
					textChannel,
					10
				);
				return;
			}
		} else {
			if (characterNum == 1) {
				fetchInfo = new CharacterFetchInfo(CharacterManager.getSheetId(userId, 1));

				descriptionBuilder
					.append("\n\nYour new selected character is ")
					.append(fetchInfo.characterName());

				String characterPortrait = fetchInfo.characterPortrait();

				if (!characterPortrait.isEmpty()) {
					builder
						.setImage(characterPortrait);
				}
			}
		}

		ctx.getMessage().delete().queue();
		ctx.getChannel().sendMessageEmbeds(builder.build()).queue();
	}

	@Override
	public String getInvoke() {
		return "deletecharacter";
	}

	@Override
	public String getHelp() {
		return "Deletes your current selected character";
	}

	@Override
	public String getUsage() {
		return "<# of your character>";
	}

	@Override
	public String getCategory() {
		return "dnd";
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("deletechar", "delchar");
	}
}
