package de.neb.playerDetector.client;

import net.minecraft.entity.player.PlayerEntity;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class sendDiscrodMessage {

    public sendDiscrodMessage(PlayerEntity player, String currentPlayer, SavaData savaData){
        try {

            HttpClient client = HttpClient.newHttpClient();


            String playerName = player.getName().getString();
            String profileUrl = "https://namemc.com/profile/" + player.getUuid();
            String avatarUrl = "https://mc-heads.net/avatar/" + playerName;

            String messanger = currentPlayer;

            String json = "{\n" +
                    "  \"username\": \"MinecraftBot\",\n" +
                    "  \"embeds\": [\n" +
                    "    {\n" +
                    "      \"author\": {\n" +
                    "        \"name\": \"" + playerName + "\",\n" +
                    "        \"url\": \"" + profileUrl + "\",\n" +
                    "        \"icon_url\": \"" + avatarUrl + "\"\n" +
                    "      },\n" +
                    "      \"color\": 16711680,\n" +
                    "      \"fields\": [\n" +
                    "        {\n" +
                    "          \"name\": \"NameMC\",\n" +
                    "          \"value\": \"[Profil ansehen](" + profileUrl + ")\",\n" +
                    "          \"inline\": true\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"name\": \"Gemeldet von\",\n" +
                    "          \"value\": \"" + messanger + "\",\n" +
                    "          \"inline\": true\n" +
                    "        }\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(savaData.getDataKey("discordWebhook")))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());

            System.out.println("Status Code: " + response.statusCode());
            System.out.println("Response Code: " + response.body());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
