package io.github.ceBot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import discord4j.core.DiscordClient;
import discord4j.core.object.util.Snowflake;

import java.util.HashSet;
import java.util.Set;

public class ServerMusic {
    public final AudioPlayer player;
    public final Tracks scheduler;
    public final Set<Snowflake> usersSkipping = new HashSet<>();

    public ServerMusic(AudioPlayerManager manager, Snowflake guildId, DiscordClient client) {
        player = manager.createPlayer();
        scheduler = new Tracks(player, guildId, client);
        player.addListener(scheduler);
    }
}
