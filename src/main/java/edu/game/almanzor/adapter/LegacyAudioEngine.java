package edu.game.almanzor.adapter;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

public final class LegacyAudioEngine {
    public String playFile(String filePath, int volumePercent) {
        String name = filePath.substring(filePath.lastIndexOf('/') + 1).replace(".wav", "");
        int hz = switch (name) {
            case "hit" -> 230;
            case "hurt" -> 140;
            case "pickup" -> 740;
            case "potion" -> 540;
            case "stairs" -> 430;
            case "victory" -> 900;
            default -> 390;
        };
        Thread effect = new Thread(() -> tone(hz, Math.max(0, Math.min(volumePercent, 100))), "game-audio");
        effect.setDaemon(true);
        effect.start();
        return "Legacy audio: " + filePath + " (volume " + volumePercent + "%)";
    }

    private static void tone(int hz, int volume) {
        try {
            float sampleRate = 16000f;
            AudioFormat format = new AudioFormat(sampleRate, 8, 1, true, false);
            int count = 1600;
            byte[] samples = new byte[count];
            for (int i = 0; i < count; i++) {
                double fade = 1.0 - (double) i / count;
                samples[i] = (byte) (Math.sin(2 * Math.PI * hz * i / sampleRate)
                        * fade * volume * 0.20);
            }
            try (SourceDataLine line = AudioSystem.getSourceDataLine(format)) {
                line.open(format, count);
                line.start();
                line.write(samples, 0, samples.length);
                line.drain();
                line.stop();
            }
        } catch (Exception ignored) {
            // Unsupported or missing audio devices must not interrupt gameplay.
        }
    }
}
