package edu.game.almanzor.adapter;

import java.util.Objects;

public final class LegacyAudioAdapter implements SoundPlayer {
    private final LegacyAudioEngine legacy;

    public LegacyAudioAdapter(LegacyAudioEngine legacy) { this.legacy = Objects.requireNonNull(legacy); }
    @Override public String play(String soundId) {
        if (soundId == null || !soundId.matches("[a-zA-Z0-9_-]+")) {
            throw new IllegalArgumentException("Invalid sound id");
        }
        return legacy.playFile("assets/sounds/" + soundId + ".wav", 80);
    }
}
