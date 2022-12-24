package fr.caranouga.autojenkinsuploader;

import org.bukkit.plugin.java.JavaPlugin;

public final class AutoJenkinsUploader extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
    }
}
