package com.raikuman.troubleclub.club.members.des.yamboard;

/**
 * Holds information for the karma object
 *
 * @version 1.0 2023-07-03
 * @since 1.0
 */
public class KarmaObject {

	private final int upvotes;
	private final int downvotes;
	private final int posts;

	public KarmaObject(int upvotes, int downvotes, int posts) {
		this.upvotes = upvotes;
		this.downvotes = downvotes;
		this.posts = posts;
	}

	public int getUpvotes() {
		return upvotes;
	}

	public int getDownvotes() {
		return downvotes;
	}

	public int getPosts() {
		return posts;
	}

	/**
	 * Get the ratio of upvotes and downvotes in a percentage
	 * @return The ratio of karma in percentage
	 */
	public String getRatioPercent() {
		if (upvotes + downvotes == 0)
			return "-%";

		return String.format("%2f", (double) (upvotes / (upvotes + downvotes))) + "%";
	}
}
