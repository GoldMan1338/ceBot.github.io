package io.github.ceBot.music;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.util.Permission;
import io.github.ceBot.BotUtils;
import reactor.core.publisher.Mono;

import java.util.Set;

public enum MusicPerms {
    DJ, ALONE, NONE;

    public static Mono<MusicPerms> getPermission(Member member) {
        return isDJ(member)
                .filter(b -> b)
                .map(b -> MusicPerms.DJ)
                .switchIfEmpty(BotUtils.isMemberAloneWithBot(member)
                        .map(b -> b ? MusicPerms.ALONE : MusicPerms.NONE)
                );
    }

    static Mono<Boolean> isDJ(Member member) {
        return Mono.zip(hasDJRole(member), hasManageChannels(member))
                .map(tuple -> tuple.getT1() || tuple.getT2());
    }

    private static Mono<Boolean> hasDJRole(Member member) {
        return member.getRoles()
                .any(role -> role.getName().equalsIgnoreCase("dj"));
    }

    private static Mono<Boolean> hasManageChannels(Member member) {
        return member.getBasePermissions().map(ps -> ps.contains(Permission.MANAGE_CHANNELS));
    }

    public static Mono<Boolean> requirePermissions(Member member, TextChannel channel,
                                                   Set<MusicPerms> permissions, boolean displayNoPermission) {
        if (permissions.contains(MusicPerms.NONE)) return Mono.just(true);
        if (permissions.contains(MusicPerms.ALONE))
            permissions.add(MusicPerms.DJ); //DJs can do all of Alone commands + more
        Mono<MusicPerms> permission = getPermission(member);
        Mono<Boolean> contains = permission.map(permissions::contains);
        if (!displayNoPermission) return contains;
        return contains
                .flatMap(hasPermission -> {
                    if (!hasPermission) {
                        if (permissions.contains(MusicPerms.ALONE)) {
                            return BotUtils.sendMessage(":x: Insufficient permission. You can do this command if you are alone with the bot or are a DJ.", channel);
                        } else if (permissions.contains(MusicPerms.DJ)) {
                            return BotUtils.sendMessage(":x: Insufficient permission. You can do this command if you are a DJ.", channel);
                        }
                    }
                    return Mono.empty();
                })
                .then(contains);
    }

    public static Mono<Boolean> requirePermissions(Member member, TextChannel channel, Set<MusicPerms> permissions) {
        return requirePermissions(member, channel, permissions, true);
    }
}