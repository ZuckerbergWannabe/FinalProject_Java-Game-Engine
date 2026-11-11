package edu.game.almanzor;

import edu.game.almanzor.adapter.LegacyAudioAdapter;
import edu.game.almanzor.adapter.LegacyAudioEngine;
import edu.game.almanzor.adapter.SoundPlayer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AdapterTest {
    @Test void convertsModernSoundCallToLegacySignature() {
        SoundPlayer player = new LegacyAudioAdapter(new LegacyAudioEngine());
        assertEquals("Legacy audio: assets/sounds/jump.wav (volume 80%)", player.play("jump"));
        assertThrows(IllegalArgumentException.class, () -> player.play("../secret"));
    }
}
