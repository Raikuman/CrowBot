package com.raikuman.troubleclub.club.members.suu.commands.help.selects;

import com.raikuman.botutilities.buttons.pagination.manager.PageInvokeInterface;
import com.raikuman.botutilities.context.EventContext;
import com.raikuman.botutilities.selectmenus.manager.SelectContext;
import com.raikuman.botutilities.selectmenus.manager.SelectInterface;
import com.raikuman.troubleclub.club.category.FunCategory;
import com.raikuman.troubleclub.club.members.suu.commands.help.HelpUtilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.components.ActionRow;

import java.util.List;

/**
 * Handles showing the description of fun commands
 *
 * @version 1.0 2023-08-03
 * @since 1.0
 */
public class FunSelect implements SelectInterface, PageInvokeInterface {

	@Override
	public void handle(SelectContext ctx) {
		HelpUtilities.handleHelpSelect(
			ctx,
			getMenuValue(),
			pageName(),
			pageStrings(ctx),
			itemsPerPage(),
			loopPagination()
		);
	}

	@Override
	public String getMenuValue() {
		return "helpfun";
	}

	@Override
	public String getLabel() {
		return "Fun";
	}

	@Override
	public String pageName() {
		return "Fun Commands";
	}

	@Override
	public List<String> pageStrings(EventContext ctx) {
		return HelpUtilities.getHelpManager(ctx).provideHelpCategoryPageStrings(new FunCategory().getName());
	}

	@Override
	public int itemsPerPage() {
		return 1;
	}

	@Override
	public boolean loopPagination() {
		return true;
	}

	@Override
	public boolean addHomeBtn() {
		return true;
	}

	@Override
	public boolean addFirstPageBtn() {
		return true;
	}

	@Override
	public List<ActionRow> homeActionRows(EventContext ctx) {
		return HelpUtilities.getHelpManager(ctx).provideHomeActionRows();
	}

	@Override
	public List<EmbedBuilder> homePages(EventContext ctx) {
		return HelpUtilities.getHelpManager(ctx).getHomePagination().buildEmbeds();
	}
}
