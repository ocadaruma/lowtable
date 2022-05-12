package com.mayreh.lowtable;

import java.awt.Color;
import java.awt.Font;

import lombok.Builder;
import lombok.NonNull;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.Accessors;

@Value
@Accessors(fluent = true)
@Builder(toBuilder = true)
public class TableStyle {
    int borderThickness;

    int margin;

    @NonNull
    TableFont font;

    @NonNull
    Color background;

    @NonNull
    Color foreground;

    @NonNull
    Color borderColor;

    @NonNull
    TextAlign textAlign;

    public static final TableStyle DEFAULT = new TableStyle(
            Defaults.BORDER_THICKNESS,
            Defaults.MARGIN,
            Defaults.FONT,
            Defaults.BACKGROUND,
            Defaults.FOREGROUND,
            Defaults.BORDER_COLOR,
            Defaults.TEXT_ALIGN);

    public enum TextAlign {
        Left,
        Center,
        Right,
    }

    /**
     * Contains style overrides.
     *
     * Non-null field means we override it from table's style.
     * Otherwise, table's style will be used.
     */
    @Value
    @Accessors(fluent = true)
    @Builder(toBuilder = true)
    public static class TableCellStyle {
        public static final TableCellStyle NONE = builder().build();

        String fontName;

        Integer fontStyle;

        Integer fontSize;

        Color background;

        Color foreground;

        TextAlign textAlign;
    }

    public interface Defaults {
        int BORDER_THICKNESS = 1;
        int MARGIN = 5;
        TableFont FONT = new TableFont(Font.SANS_SERIF, Font.PLAIN, 14);
        TableFont FONT_MONOSPACED = new TableFont(Font.MONOSPACED, Font.PLAIN, 14);
        TextAlign TEXT_ALIGN = TextAlign.Left;
        Color BACKGROUND = new Color(248, 248,248);
        Color FOREGROUND = new Color(50, 50, 50);
        Color BORDER_COLOR = new Color(194, 194, 194);
        Color SUCCESS_LIGHT = new Color(230, 249,232);
        Color DANGER_LIGHT = new Color(245, 205,219);
    }
}
