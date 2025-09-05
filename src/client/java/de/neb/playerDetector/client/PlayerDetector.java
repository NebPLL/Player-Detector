package de.neb.playerDetector.client;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
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

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

public class PlayerDetector implements ClientModInitializer {

    public static boolean AfkDector = false;
    public static ArrayList<String> FoundPlayers = new ArrayList<>();

    @Override
    public void onInitializeClient() {

        SavaData savaData = new SavaData();
        DetectPlayer detectPlayer = new DetectPlayer(savaData);

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {

            dispatcher.register(
                    ClientCommandManager.literal("afk")
                            .executes(context -> {
                                System.out.println("AFK Code ausgeführt!");
                                if (AfkDector) {
                                    detectPlayer.setDisable();
                                    AfkDector = false;
                                } else {
                                    detectPlayer.setEnable();
                                    detectPlayer.loadScript(savaData);
                                    AfkDector = true;
                                }
                                return 1;
                            })
                            .then(ClientCommandManager.argument("action", StringArgumentType.word())
                                    .suggests((context, builder) -> {
                                        builder.suggest("disableOnMove");
                                        builder.suggest("discordWebhook");
                                        return builder.buildFuture();
                                    })
                                    // disableOnMove-Kette
                                    .then(ClientCommandManager.argument("value", StringArgumentType.word())
                                            .suggests((context, builder) -> {
                                                builder.suggest("true");
                                                builder.suggest("false");
                                                return builder.buildFuture();
                                            })
                                            .executes(context -> {
                                                String action = StringArgumentType.getString(context, "action");
                                                String valueStr = StringArgumentType.getString(context, "value");
                                                if (action.equalsIgnoreCase("disableOnMove")) {
                                                    boolean boolValue = Boolean.parseBoolean(valueStr);
                                                    detectPlayer.setDisableOnMove(savaData, boolValue);
                                                    System.out.println("disableOnMove gesetzt auf " + boolValue);
                                                }
                                                return 1;
                                            })
                                    )
                                    // discordWebhook-Kette
                                    .then(ClientCommandManager.argument("webhook", StringArgumentType.greedyString())
                                            .executes(context -> {
                                                String webhook = StringArgumentType.getString(context, "webhook");
                                                savaData.saveDataKey("discordWebhook", webhook);
                                                System.out.println("Discord Webhook gesetzt auf: " + webhook);
                                                return 1;
                                            })
                                    )
                            )
            );
        });
    }

    public static void setAfkDector(boolean set){
        AfkDector = set;
    }
}