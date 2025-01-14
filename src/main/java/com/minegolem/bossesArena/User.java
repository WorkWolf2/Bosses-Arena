package com.minegolem.bossesArena;
import lombok.Getter;

import java.util.UUID;

public record User(UUID uuid, String username, int kill, int death) {
}
