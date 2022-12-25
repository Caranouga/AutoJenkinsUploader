package fr.caranouga.autojenkinsuploader;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public final class AutoJenkinsUploader extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                String baseUrl = getConfig().getString("config.jenkins-url");
                String username = getConfig().getString("config.jenkins-username");
                String password = getConfig().getString("config.jenkins-password");
                String token = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
                for (String key : getConfig().getConfigurationSection("config.jenkins-jobs").getKeys(false)) {
                    String jobName = getConfig().getString("config.jenkins-jobs." + key + ".job-name");
                    try {
                        URL url = new URL(baseUrl + "/job/" + jobName + "/api/json?pretty=true");
                        HttpURLConnection http = (HttpURLConnection) url.openConnection();
                        http.setRequestProperty("Authorization", "Basic " + token);
                        if(http.getResponseCode() == 200) {
                            //Convert the input stream to a string
                            String response = new java.util.Scanner(http.getInputStream()).nextLine();
                            JsonObject json = new JsonParser().parse(response).getAsJsonObject();
                            int lastSuccessfulBuildNumber = json.get("lastSuccessfulBuild").getAsJsonObject().get("number").getAsInt();
                            if(getConfig().getInt("config.jenkins-jobs." + key + ".last-successful-build-number") != lastSuccessfulBuildNumber) {
                                getConfig().set("config.jenkins-jobs." + key + ".last-successful-build-number", lastSuccessfulBuildNumber);
                                saveConfig();
                                getServer().dispatchCommand(getServer().getConsoleSender(), getConfig().getString("config.jenkins-jobs." + key + ".command"));
                            }

                        }

                        http.disconnect();
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 0, 20 * 10);
    }

    @Override
    public void onDisable() {
    }
}
