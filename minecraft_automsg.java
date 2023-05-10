package de.Fellurion.auto_msg;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import com.TeamNovus.AutoMessage.Commands.DefaultCommands;
import com.TeamNovus.AutoMessage.Commands.PluginCommands;
import com.TeamNovus.AutoMessage.Commands.Common.BaseCommandExecutor;
import com.TeamNovus.AutoMessage.Commands.Common.CommandManager;
import com.TeamNovus.AutoMessage.Models.Message;
import com.TeamNovus.AutoMessage.Models.MessageList;
import com.TeamNovus.AutoMessage.Models.MessageLists;

public class auto_msg extends JavaPlugin {
	
	public static auto_msg instance;
	public static Logger log;

	@Override
	public void onEnable() {
		instance = this;
		log = getLogger();

		
		getCommand("auto_msg").setExecutor(new BaseCommandExecutor());

		
		CommandManager.register(DefaultCommands.class);
		CommandManager.register(PluginCommands.class);

		// config laden
		loadConfig();
		/*if (loadConfig()) {
			// Start metrics.
			try {
				Metrics metrics = new Metrics(this);

				metrics.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
		log.info(getDescription().getName() + " Erflogreich geladen!");
	}

	@Override
	public void onDisable() {
		MessageLists.unschedule();

		instance = null;
		Bukkit.getScheduler().cancelTasks(this);
		HandlerList.unregisterAll(this);
		log.info(getDescription().getName() + " Deaktiviert!");
	}

	public boolean loadConfig() {
		if (!(new File(getDataFolder() + File.separator + "config.yml").exists())) {
			saveDefaultConfig();
		}


		try {
			new YamlConfiguration().load(new File(getDataFolder() + File.separator + "config.yml"));
		} catch (Exception e) {
			System.out.println("--- --- --- ---");
			System.out.println("Fehler beim laden der config.");
			System.out.println("Ein Fehler log wurde erstellt.");
			System.out.println("--- --- --- ---");
			e.printStackTrace();
			Bukkit.getPluginManager().disablePlugin(this);

			return false;
		}

		reloadConfig();

		MessageLists.clear();

		for (String key : getConfig().getConfigurationSection("msgliste").getKeys(false)) {
			MessageList list = new MessageList();

			if (getConfig().contains("message-lists." + key + ".Aktiv"))
				list.setEnabled(getConfig().getBoolean("msgliste." + key + ".Aktiv"));

			if (getConfig().contains("message-lists." + key + ".interval"))
				list.setInterval(getConfig().getInt("msgliste." + key + ".interval"));

			if (getConfig().contains("message-lists." + key + ".expiry"))
				list.setExpiry(getConfig().getLong("msgliste." + key + ".expiry"));

			if (getConfig().contains("message-lists." + key + ".random"))
				list.setRandom(getConfig().getBoolean("msgliste." + key + ".random"));

			LinkedList<Message> finalMessages = new LinkedList<Message>();

			if (getConfig().contains("msgliste." + key + ".messages")) {
				ArrayList<? extends Object> messages = (ArrayList<? extends Object>) getConfig().getList("msgliste." + key + ".messages");

				for (Object m : messages) {
					if (m instanceof String) {
						finalMessages.add(new Message((String) m));
					} else if (m instanceof Map) {
						@SuppressWarnings("unchecked")
						Map<String, List<String>> message = (Map<String, List<String>>) m;

						for (Entry<String, List<String>> entry : message.entrySet()) {
							finalMessages.add(new Message(entry.getKey()));
						}
					}
				}
			}

			list.setMessages(finalMessages);

			MessageLists.setList(key, list);
		}

		MessageLists.schedule();

		
		saveConfiguration();

		return true;
	}

	public void saveConfiguration() {
		if (!(new File(getDataFolder() + File.separator + "config.yml").exists())) {
			saveDefaultConfig();
		}

		for (String key : getConfig().getConfigurationSection("msgliste").getKeys(false)) {
			getConfig().set("msgliste." + key, null);
		}

		for (String key : MessageLists.getMessageLists().keySet()) {
			MessageList list = MessageLists.getExactList(key);
			getConfig().set("msgliste." + key + ".enabled", list.isEnabled());
			getConfig().set("msgliste." + key + ".interval", list.getInterval());
			getConfig().set("msgliste." + key + ".expiry", list.getExpiry());
			getConfig().set("msgliste." + key + ".random", list.isRandom());

			List<String> messages = new LinkedList<String>();

			for (Message m : list.getMessages()) {
				messages.add(m.getMessage());
			}

			getConfig().set("msgliste." + key + ".messages", messages);
		}

		saveConfig();
	}

	public File getFile() {
		return super.getFile();
	}
}
