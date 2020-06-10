package me.nullx.tablistfix;

import net.labymod.api.LabyModAddon;
import net.labymod.main.LabyMod;
import net.labymod.settings.elements.SettingsElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.lang.reflect.Field;
import java.util.List;

public class TabListFix extends LabyModAddon {

    private Field registeredName;

    public TabListFix() throws NoSuchFieldException {
        registeredName = ScorePlayerTeam.class.getDeclaredField(LabyMod.isForge() ? "name" : "b");
        registeredName.setAccessible(true);
    }

    @Override
    public void onEnable() {
        getApi().registerForgeListener(this);
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {
        if (Minecraft.getMinecraft().gameSettings.keyBindPlayerList.isKeyDown()) {
            Minecraft.getMinecraft().player.connection.getPlayerInfoMap().forEach(this::fixTablistEntry);
        }
    }

    private void fixTablistEntry(NetworkPlayerInfo networkPlayerInfo) {
        fixTeamName(networkPlayerInfo);
    }

    private void fixTeamName(NetworkPlayerInfo networkPlayerInfo) {
        ScorePlayerTeam team = networkPlayerInfo.getPlayerTeam();
        if (team != null && (team.getName().length() == 16 || team.getName().length() == 5)) {
            String playerName = networkPlayerInfo.getGameProfile().getName();
            try {
                registeredName.set(team, String.format("%s%s", team.getName().substring(0, 5), playerName.substring(0, 2).toUpperCase()));
            } catch (Exception e) { //IllegalAccess & IndexOutOfBounds
                e.printStackTrace();
            }
        }
    }

    @Override
    public void loadConfig() {
    }

    @Override
    protected void fillSettings(List<SettingsElement> list) {
    }
}
