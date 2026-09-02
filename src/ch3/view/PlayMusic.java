package ch3.view;

import java.io.*;
import java.net.*;
import javax.sound.sampled.*;
import javazoom.jl.player.Player;

/**
 * 音频播放类（课设要求③）
 * 支持两种播放方式：
 *   1. 本地音频文件（.wav/.au/.aiff）—— 用Java Sound API播放
 *   2. 有道词典在线发音（创新功能）—— 用JLayer解码mp3，程序内直接播放
 */
public class PlayMusic {
    private Clip localClip;
    private Player onlinePlayer;
    private Thread playThread;

    public boolean load(String fileName) {
        try {
            File musicFile = new File("audio/" + fileName);
            if (!musicFile.exists()) musicFile = new File(fileName);
            if (!musicFile.exists()) {
                System.out.println("音频文件不存在: " + fileName);
                return false;
            }
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(musicFile);
            localClip = AudioSystem.getClip();
            localClip.open(audioIn);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean playOnline(String word, int type) {
        try {
            stopOnline();
            String urlStr = "https://dict.youdao.com/dictvoice?audio="
                    + URLEncoder.encode(word, "UTF-8") + "&type=" + type;
            URL url = new URL(urlStr);
            InputStream is = url.openStream();
            onlinePlayer = new Player(is);
            playThread = new Thread(() -> {
                try { onlinePlayer.play(); }
                catch (Exception e) { System.out.println("播放异常: " + e.getMessage()); }
            });
            playThread.start();
            return true;
        } catch (Exception e) {
            System.out.println("在线发音失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void stopOnline() {
        if (onlinePlayer != null) {
            try { onlinePlayer.close(); } catch (Exception e) {}
            onlinePlayer = null;
        }
        if (playThread != null && playThread.isAlive()) {
            playThread.interrupt();
            playThread = null;
        }
    }

    public static void openInBrowser(String word) {
        try {
            String url = "https://dict.youdao.com/w/" + URLEncoder.encode(word, "UTF-8") + "/";
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void play() {
        if (localClip != null) localClip.start();
    }

    public void loop() {
        if (localClip != null) localClip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop() {
        if (localClip != null && localClip.isRunning()) localClip.stop();
        stopOnline();
    }
}
