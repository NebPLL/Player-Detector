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

public class PlayerDetector implements ClientModInitializer {

    public static boolean AfkDector = false;
    public static ArrayList<String> FoundPlayers = new ArrayList<>();

    @Override
    public void onInitializeClient() {



        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("afk")
                .executes(commandContext -> {



                        AfkDector = true;
                        new DetectPlayer();

                    return 1;
                })



        ));


    }

    public static void setAfkDector(boolean set){
        AfkDector = set;
    }
}