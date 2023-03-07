package com.raikuman.troubleclub.club.members.suu.listener;

import com.raikuman.troubleclub.club.config.member.MemberDB;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides an event listener for member joining and leaving
 *
 * @version 1.0 2023-05-03
 * @since 1.0
 */
public class MemberEventListener extends ListenerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(MemberEventListener.class);

	@Override
	public void onReady(@NotNull ReadyEvent event) {
		logger.info("{}" + MemberEventListener.class.getName() + " is initialized",
			event.getJDA().getSelfUser().getAsTag());
	}

	@Override
	public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
		Member member = event.getMember();

		if (member.getUser().isBot())
			return;

		MemberDB.addMember(member);
	}
}