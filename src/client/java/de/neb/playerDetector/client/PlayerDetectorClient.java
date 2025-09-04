package de.neb.playerDetector.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class PlayerDetectorClient implements ClientModInitializer {

    public static boolean AfkDector = false;
    public static ArrayList<String> FoundPlayers = new ArrayList<>();

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("afk")
                .executes(commandContext -> {
                    if (AfkDector) {
                        AfkDector = false;
                        MinecraftClient.getInstance().player.sendMessage(Text.literal("AFK Detector now off!").setStyle(Style.EMPTY.withColor(Formatting.LIGHT_PURPLE)),true);
                    }else {
                        AfkDector = true;
                    }

                    DectsPlayer();
                    return 1;
                })

        ));
    }

    public void DectsPlayer(){

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null || !AfkDector) return;

            client.player.sendMessage(Text.literal("Player Dector now on!").setStyle(Style.EMPTY.withColor(Formatting.DARK_PURPLE)),true);

            checkIfPlayerMoved();


            for (Entity entity : client.world.getEntities()){
                if (entity instanceof PlayerEntity playerEntity && playerEntity != client.player){
                    if (!FoundPlayers.contains(playerEntity.getName().getString())){
                        sendDiscordMessage(playerEntity);
                        sendMessage(playerEntity);
                        FoundPlayers.add(playerEntity.getName().getString());
                    }



                }
            }
        });
    }

    public void checkIfPlayerMoved(){
        KeyBinding forwardKey = MinecraftClient.getInstance().options.forwardKey;
        KeyBinding leftKey = MinecraftClient.getInstance().options.leftKey;
        KeyBinding rightKey = MinecraftClient.getInstance().options.rightKey;
        KeyBinding backKey = MinecraftClient.getInstance().options.backKey;

        if (forwardKey.isPressed() || leftKey.isPressed() || rightKey.isPressed() || backKey.isPressed()){
            AfkDector = false;
            MinecraftClient.getInstance().player.sendMessage(Text.literal("AFK Detector now off!").setStyle(Style.EMPTY.withColor(Formatting.LIGHT_PURPLE)),true);
        }
    }

    public void sendMessage(PlayerEntity playerEntity){
        MinecraftClient.getInstance().player.sendMessage(Text.literal("Player Found: ")
                        .setStyle(Style.EMPTY.withColor(Formatting.GRAY))
                        .append(Text.literal(playerEntity.getName().getString())
                                .setStyle(Style.EMPTY.withColor(Formatting.DARK_PURPLE)))
                        .append(Text.literal(", UUID: ")
                                .setStyle(Style.EMPTY.withColor(Formatting.GRAY)))
                        .append(Text.literal(String.valueOf(playerEntity.getUuid()))
                                .setStyle(Style.EMPTY.withColor(Formatting.DARK_PURPLE)))

                , false);
    }


    public void sendDiscordMessage(PlayerEntity player) {
        try {

            HttpClient client = HttpClient.newHttpClient();


            String message = "Player gefunden: " + player.getName().getString();
            String avatarUrl = "https://mc-heads.net/avatar/" + player.getName().getString();

            String json = "{\n" +
                    "  \"username\": \"MinecraftBot\",\n" +
                    "  \"embeds\": [\n" +
                    "    {\n" +
                    "      \"title\": \"Spieler gefunden\",\n" +
                    "      \"description\": \"" + message + "\",\n" +
                    "      \"color\": 16711680,\n" +
                    "      \"thumbnail\": {\n" +
                    "        \"url\": \"" + avatarUrl + "\"\n" +
                    "      }\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://discordapp.com/api/webhooks/1410895607131930624/jhcaoMYoXhQT-wPqk-869tWehdL71yBBSCqxgbmZ2ufdEfF3k-1i4GLOnrUkBO2NcVBF"))
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