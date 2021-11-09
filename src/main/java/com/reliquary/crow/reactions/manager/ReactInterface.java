package com.reliquary.crow.reactions.manager;

public interface ReactInterface {

	void handleAdd(ReactContext ctx);

	void handleRemove(ReactContext ctx);

	String getInvoke();
}
