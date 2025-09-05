package de.neb.playerDetector.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.HashSet;

public class DetectPlayer {

    private HashMap<String, PlayerEntity> foundPlayers = new HashMap<>();

    public boolean Afk;

    public boolean disableOnMove;



    public DetectPlayer(SavaData savaData){
        Afk = true;
    }

    public void loadScript(SavaData savaData){

        disableOnMove = Boolean.parseBoolean(savaData.getDataKey("disableOnMove"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            if (!Afk) return;

            if (checkIfPlayerMoved()) {
                Afk = false;
                PlayerDetector.setAfkDector(false);
                return;
            }

            if (client.player == null || client.world == null) return;

            client.player.sendMessage(Text.literal("AFK Detector now on!").setStyle(Style.EMPTY.withColor(Formatting.DARK_PURPLE)),true);


            HashMap<String, PlayerEntity> currentPlayers = new HashMap<>();
            for (Entity entity : client.world.getEntities()){
                if (entity instanceof PlayerEntity playerEntity && playerEntity != client.player){

                    if (playerEntity.getName().getString().length() == 2) continue;

                    currentPlayers.put(playerEntity.getName().getString(), playerEntity);

                    if (foundPlayers.containsKey(playerEntity.getName().getString())) continue;
                    new sendDiscrodMessage(playerEntity, client.player.getName().getString(),savaData);
                }
            }

            foundPlayers.clear();
            foundPlayers.putAll(currentPlayers);
        });
    }


    public void setEnable(){Afk = true;}

    public void setDisable(){
        MinecraftClient.getInstance().player.sendMessage(Text.literal("AFK Detector now off!").setStyle(Style.EMPTY.withColor(Formatting.LIGHT_PURPLE)),true);
        Afk = false;
    }

    public void setDisableOnMove(SavaData savaData, boolean boolValue){
        savaData.saveDataKey("disableOnMove", String.valueOf(boolValue));
        disableOnMove = boolValue;

    }


    public boolean checkIfPlayerMoved(){

        if (!disableOnMove) return false;

        KeyBinding forwardKey = MinecraftClient.getInstance().options.forwardKey;
        KeyBinding leftKey = MinecraftClient.getInstance().options.leftKey;
        KeyBinding rightKey = MinecraftClient.getInstance().options.rightKey;
        KeyBinding backKey = MinecraftClient.getInstance().options.backKey;

        if (forwardKey.isPressed() || leftKey.isPressed() || rightKey.isPressed() || backKey.isPressed()){
            MinecraftClient.getInstance().player.sendMessage(Text.literal("AFK Detector now off!").setStyle(Style.EMPTY.withColor(Formatting.LIGHT_PURPLE)),true);
            return true;
        }

        return false;

    }
}
