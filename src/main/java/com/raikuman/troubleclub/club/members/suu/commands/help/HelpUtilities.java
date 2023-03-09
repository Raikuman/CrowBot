package com.raikuman.troubleclub.club.members.suu.commands.help;

import com.raikuman.botutilities.buttons.pagination.manager.Pagination;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.context.EventContext;
import com.raikuman.botutilities.help.HelpBuilder;
import com.raikuman.botutilities.help.HelpManager;
import com.raikuman.botutilities.selectmenus.manager.SelectContext;
import com.raikuman.botutilities.slashcommands.manager.SlashInterface;
import com.raikuman.troubleclub.club.category.FunCategory;
import com.raikuman.troubleclub.club.category.OtherCategory;
import com.raikuman.troubleclub.club.category.RoleplayingCategory;
import com.raikuman.troubleclub.club.members.crow.listener.handler.CrowInvokeInterfaceProvider;
import com.raikuman.troubleclub.club.members.des.listener.handler.DesInvokeInterfaceProvider;
import com.raikuman.troubleclub.club.members.suu.commands.help.selects.FunSelect;
import com.raikuman.troubleclub.club.members.suu.commands.help.selects.OtherSelect;
import com.raikuman.troubleclub.club.members.suu.commands.help.selects.RoleplayingSelect;
import com.raikuman.troubleclub.club.members.suu.listener.handler.SuuInvokeInterfaceProvider;
import net.dv8tion.jda.api.interactions.components.ItemComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Provides utility functions for the help command
 *
 * @version 1.0 2023-08-03
 * @since 1.0
 */
public class HelpUtilities {

	/**
	 * Creates a help manager for the help command
	 * @param ctx The event context to use in the help manager
	 * @return The built help manager
	 */
	public static HelpManager getHelpManager(EventContext ctx) {
		HelpBuilder builder = new HelpBuilder(
			new Help().pageName(),
			ctx,
			Arrays.asList(
				new RoleplayingCategory(),
				new FunCategory(),
				new OtherCategory()
			),
			Arrays.asList(
				new RoleplayingSelect(),
				new FunSelect(),
				new OtherSelect()
			)
		);

		List<CommandInterface> commands = new ArrayList<>();
		List<SlashInterface> slashes = new ArrayList<>();

		// Get commands
		commands.addAll(CrowInvokeInterfaceProvider.provideCommands());
		commands.addAll(DesInvokeInterfaceProvider.provideCommands());
		//commands.addAll(InoriInvokeInterfaceProvider.provideCommands());
		commands.addAll(SuuInvokeInterfaceProvider.provideCommands());

		// Get slashes
		//slashes.addAll(CrowInvokeInterfaceProvider.provideSlashes());
		//slashes.addAll(DesInvokeInterfaceProvider.provideSlashes());
		//slashes.addAll(InoriInvokeInterfaceProvider.provideSlashes());
		slashes.addAll(SuuInvokeInterfaceProvider.provideSlashes());

		builder.setCommands(commands);
		builder.setSlashes(slashes);

		return builder.build();
	}

	/**
	 * Handles the functionality of help selects
	 * @param ctx The select context to build pagination from
	 * @param menuValue The menu value of the select
	 * @param pagename The name of the select page
	 * @param pageStrings The strings to create pagination for
	 * @param itemsPerPage The number of items on each page
	 * @param loopPagination Whether to loop the pagination
	 */
	public static void handleHelpSelect(SelectContext ctx, String menuValue, String pagename,
		List<String> pageStrings, int itemsPerPage, boolean loopPagination) {
		Pagination pagination = new Pagination(
			ctx.getEventMember(),
			menuValue,
			pagename,
			pageStrings,
			itemsPerPage,
			loopPagination
		);

		List<ItemComponent> componentList = Arrays.asList(
			pagination.provideLeft(),
			pagination.provideHome(),
			pagination.provideFirst(),
			pagination.provideRight()
		);

		ctx.getEvent().getHook().editOriginalEmbeds(pagination.buildEmbeds().get(0).build())
			.setActionRow(componentList)
			.queue();

		ctx.getEvent().deferEdit().queue();
	}
}
