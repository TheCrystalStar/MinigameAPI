package com.florianwoelki.minigameapi.command;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandHandler {

	private JavaPlugin javaPlugin;

	public CommandHandler(JavaPlugin javaPlugin) {
		this.javaPlugin = javaPlugin;
	}

	public <T extends CommandExecutor> void register(Class<?> commandClass, T commandClassInstance) {
		for(Method method : commandClass.getDeclaredMethods()) {
			register(method, method.getAnnotation(Command.class), commandClassInstance);
		}
	}

	private void register(Method method, Command command, CommandExecutor commandExecutor) {
		if(command == null) {
			return;
		}

		System.out.println("[MinigameAPI] Register Command: " + command.command()); // TODO: Add custom logger.

		String name = command.command();
		List<String> alias = new ArrayList<>();
		alias.add(name);
		if(!ArrayUtils.isEmpty(command.alias())) {
			Collections.addAll(alias, command.alias());
		}

		CommandMap commandMap = getCommandMap();
		commandMap.register(name, new DynamicCommand(javaPlugin, commandExecutor, name, name, method, alias.toArray(new String[alias.size()]), "/" + name, command.permissions(), command.permissionMessage(), command.sender()));
		setCommandMap(commandMap);
	}

	private void setCommandMap(CommandMap commandMap) {
		Field map;
		try {
			map = javaPlugin.getServer().getClass().getDeclaredField("commandMap");
			map.setAccessible(true);
			map.set(javaPlugin.getServer(), commandMap);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private CommandMap getCommandMap() {
		Field map;
		try {
			map = javaPlugin.getServer().getClass().getDeclaredField("commandMap");
			map.setAccessible(true);
			return (CommandMap) map.get(javaPlugin.getServer());
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
