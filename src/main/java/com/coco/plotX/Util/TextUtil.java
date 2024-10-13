package com.coco.plotX.Util;

import com.coco.plotX.PlotX;
import net.md_5.bungee.api.ChatColor;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {

    private static final Pattern GRADIENT_PATTERN = Pattern.compile("<(#[A-Fa-f0-9]{6})>(.*?)</(#[A-Fa-f0-9]{6})>");
    private static final Pattern LEGACY_GRADIENT_PATTERN = Pattern.compile("<(&[A-Za-z0-9])>(.*?)</(&[A-Za-z0-9])>");
    private static final Pattern RGB_PATTERN = Pattern.compile("<(#......)>");

    public static List<String> colorize(List<String> list) {
        final List<String> coloredList = new ArrayList<>();
        for (String line : list) {
            coloredList.add(colorize(line));
        }
        return coloredList;
    }

    public static String colorize(String text) {
        if (text == null)
            return "Not found";

        if (VersionUtil.isHexSupport()) {
            text = processGradientColors(text);
            text = processLegacyGradientColors(text, LEGACY_GRADIENT_PATTERN);
            text = processRGBColors(text);
        }

        return ChatColor.translateAlternateColorCodes('&', text);
    }

    private static String processGradientColors(String text) {
        Matcher matcher = TextUtil.GRADIENT_PATTERN.matcher(text);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            Color startColor = Color.decode(matcher.group(1));
            String between = matcher.group(2);
            Color endColor = Color.decode(matcher.group(3));
            BeforeType[] types = BeforeType.detect(between);
            between = BeforeType.replaceColors(between);
            String gradient = rgbGradient(between, startColor, endColor, types);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(gradient));
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    private static String processLegacyGradientColors(String text, Pattern pattern) {
        Matcher matcher = pattern.matcher(text);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            char first = matcher.group(1).charAt(1);
            String between = matcher.group(2);
            char second = matcher.group(3).charAt(1);
            ChatColor firstColor = ChatColor.getByChar(first);
            ChatColor secondColor = ChatColor.getByChar(second);
            BeforeType[] types = BeforeType.detect(between);
            between = BeforeType.replaceColors(between);
            if (firstColor == null) {
                firstColor = ChatColor.WHITE;
            }
            if (secondColor == null) {
                secondColor = ChatColor.WHITE;
            }
            String gradient = rgbGradient(between, firstColor.getColor(), secondColor.getColor(), types);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(gradient));
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    private static String processRGBColors(String text) {
        Matcher matcher = TextUtil.RGB_PATTERN.matcher(text);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            ChatColor color = ChatColor.of(Color.decode(matcher.group(1)));
            matcher.appendReplacement(sb, Matcher.quoteReplacement(color.toString()));
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    private static String rgbGradient(String str, Color from, Color to, BeforeType[] types) {
        final double[] red = linear(from.getRed(), to.getRed(), str.length());
        final double[] green = linear(from.getGreen(), to.getGreen(), str.length());
        final double[] blue = linear(from.getBlue(), to.getBlue(), str.length());
        StringBuilder before = new StringBuilder();
        for (BeforeType type : types) {
            before.append(ChatColor.getByChar(type.getCode()));
        }
        final StringBuilder builder = new StringBuilder();
        if (str.length() == 1) {
            return ChatColor.of(to) + before.toString() + str;
        }
        PlotX.runFor(str.length(), index -> {
            builder.append(ChatColor.of(new Color((int) Math.round(red[index]), (int) Math.round(green[index]), (int) Math.round(blue[index])))).append(before).append(str.charAt(index));
        });
        return builder.toString();
    }

    private static double[] linear(double from, double to, int max) {
        final double[] res = new double[max];
        PlotX.runFor(max, index -> {
            res[index] = from + index * ((to - from) / (max - 1));
        });
        return res;
    }

    public enum BeforeType {
        MIXED('k'),
        BOLD('l'),
        CROSSED('m'),
        UNDERLINED('n'),
        CURSIVE('o');

        private final char code;

        BeforeType(char code) {
            this.code = code;
        }

        public char getCode() {
            return code;
        }

        public static BeforeType[] detect(String text) {
            List<BeforeType> values = new ArrayList<>();
            if (text.contains("&k")) {
                values.add(MIXED);
            }
            if (text.contains("&l")) {
                values.add(BOLD);
            }
            if (text.contains("&m")) {
                values.add(CROSSED);
            }
            if (text.contains("&n")) {
                values.add(UNDERLINED);
            }
            if (text.contains("&o")) {
                values.add(CURSIVE);
            }
            return values.toArray(new BeforeType[0]);
        }

        public static String replaceColors(String text) {
            return text.replaceAll("&[kmno]", "");
        }
    }
}
