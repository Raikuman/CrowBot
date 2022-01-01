package com.reliquary.crow.invokes.commands.dnd;

import com.reliquary.crow.invokes.slashcommands.dnd.DnD.DnD;
import com.reliquary.crow.managers.commands.CommandContext;
import com.reliquary.crow.managers.commands.CommandInterface;
import com.reliquary.crow.resources.apis.googlesheets.SheetResources;
import com.reliquary.crow.resources.dnd.CharacterFetchInfo;
import com.reliquary.crow.resources.dnd.CharacterManager;
import com.reliquary.crow.resources.jda.MessageResources;
import com.reliquary.crow.resources.other.RandomColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

/**
 * This class lets the user add a character to their user profile directory using a Google Sheets link
 *
 * @version 1.1 2021-31-12
 * @since 1.1
 */
public class AddCharacter implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {

		final TextChannel textChannel = ctx.getChannel();
		final String userId = ctx.getMember().getId();

		if (ctx.getArgs().size() != 1) {
			MessageResources.timedMessage(
				"Please provide a valid Google Sheet link of your character sheet with the command!",
				textChannel,
				10
			);
			return;
		}

		String sheetsLink = ctx.getArgs().get(0);

		// Check if the sheet is unlisted
		if (!SheetResources.checkUnlisted(sheetsLink)) {
			MessageResources.timedMessage(
				"Could not access your Google Sheet. Make sure to enable anyone with the link can view",
				textChannel,
				10
			);
			return;
		}

		if (!CharacterManager.validateCharacterLink(sheetsLink)) {
			MessageResources.timedMessage(
				"This character sheet is invalid! Make sure to use the recommended sheet linked in `/" +
				new DnD().getInvoke() + "`",
				textChannel,
				10
			);
			return;
		}

		CharacterFetchInfo fetchInfo = new CharacterFetchInfo(SheetResources.convertToId(sheetsLink));

		if (CharacterManager.checkDuplicateCharacter(userId, sheetsLink)) {
			MessageResources.timedMessage(
				"You already have a character with this Google Sheet! (" +
					fetchInfo.characterName() + ")",
				textChannel,
				10
			);
			return;
		}

		if (!CharacterManager.addCharacter(userId, sheetsLink)) {
			MessageResources.timedMessage(
				"An error occurred while creating your character profile. Contact developer for help.",
				textChannel,
				10
			);
			return;
		}

		String characterName = fetchInfo.characterName();

		EmbedBuilder builder = new EmbedBuilder()
			.setAuthor(characterName + " has been added to your profile!", null,
				ctx.getMember().getUser().getAvatarUrl())
			.setColor(RandomColor.getRandomColor());
		StringBuilder descriptionBuilder = builder.getDescriptionBuilder();

		String characterPortrait = fetchInfo.characterPortrait();
		if (!characterPortrait.isEmpty())
			builder.setImage(characterPortrait);

		String characterPlurality = "character";
		if (CharacterManager.getCharacterList(userId).size() > 1)
			characterPlurality += "s";

		descriptionBuilder
			.append("You now have ")
			.append(CharacterManager.getCharacterList(userId).size())
			.append(" ")
			.append(characterPlurality);

		ctx.getMessage().delete().queue();
		ctx.getChannel().sendMessageEmbeds(builder.build()).queue();
	}

	@Override
	public String getInvoke() {
		return "addcharacter";
	}

	@Override
	public String getHelp() {
		return "Add a character to your profile";
	}

	@Override
	public String getUsage() {
		return "<Google Sheets link>";
	}

	@Override
	public String getCategory() {
		return "dnd";
	}

	@Override
	public List<String> getAliases() {
		return List.of("addchar");
	}
}
