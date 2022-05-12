package com.mayreh.lowtable;

import java.awt.Font;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;

/**
 * Immutable wrapper of {@link Font}
 */
@Value
@Accessors(fluent = true)
@Builder(toBuilder = true)
public class TableFont {
    @NonNull
    String fontName;

    int style;

    int size;

    public Font jfont() {
        return new Font(fontName, style, size);
    }

    public TableFont withFontName(String newFontName) {
        return new TableFont(newFontName, style, size);
    }

    public TableFont withStyle(int newStyle) {
        return new TableFont(fontName, newStyle, size);
    }

    public TableFont withSize(int newSize) {
        return new TableFont(fontName, style, newSize);
    }
}
