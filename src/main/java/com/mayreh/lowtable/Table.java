package com.mayreh.lowtable;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.FileImageOutputStream;
import javax.swing.JComponent;
import javax.swing.JFrame;

import com.mayreh.lowtable.TableData.Cell;
import com.mayreh.lowtable.TableData.Row;
import com.mayreh.lowtable.TableStyle.Defaults;
import com.mayreh.lowtable.TableStyle.TableCellStyler;

import lombok.RequiredArgsConstructor;

/**
 * Simple table component
 */
@RequiredArgsConstructor
public class Table extends JComponent {
    private final TableStyle tableStyle;
    private final TableData tableData;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int rowCount = tableData.rows().size();
        if (rowCount < 1) {
            return;
        }

        int columnCount = tableData.rows().get(0).columns().size();
        if (columnCount < 1) {
            return;
        }

        int tableWidth = getWidth();
        int tableHeight = getHeight();
        int columnWidth = tableWidth / columnCount;
        int rowHeight = tableHeight / rowCount;

        for (int rowIdx = 0; rowIdx < rowCount; rowIdx++) {
            Row row = tableData.rows().get(rowIdx);
            for (int colIdx = 0; colIdx < columnCount; colIdx++) {
                Cell cell = row.columns().get(colIdx);
                TableCellStyler styler = new TableCellStyler();
                cell.styleConfigurer().accept(styler);

                final Color background;
                if (styler.background != null) {
                    background = styler.background;
                } else {
                    background = tableStyle.background();
                }
                g.setColor(background);
                g.fillRect(colIdx * columnWidth, rowIdx * rowHeight, columnWidth, rowHeight);

                final Color foreground;
                if (styler.foreground != null) {
                    foreground = styler.foreground;
                } else {
                    foreground = tableStyle.foreground();
                }

                Font font = cellFont(styler);
                FontMetrics fontMetrics = g.getFontMetrics(font);
                g.setFont(font);
                g.setColor(foreground);
                g.drawString(
                        cell.value(),
                        colIdx * columnWidth + tableStyle.margin(),
                        fontMetrics.getAscent() + (rowHeight * rowIdx) + tableStyle.margin());

                g.setColor(tableStyle.borderColor());
                if (colIdx > 0) {
                    g.fillRect(
                            colIdx * columnWidth,
                            rowIdx * rowHeight,
                            tableStyle.borderThickness(),
                            rowHeight);
                }
                if (rowIdx > 0) {
                    g.fillRect(
                            colIdx * columnWidth,
                            rowIdx * rowHeight,
                            columnWidth,
                            tableStyle.borderThickness());
                }
            }
        }
    }

    public void setAutoSize() {
        int maxTextWidth = 0;
        int maxTextHeight = 0;

        int rowCount = tableData.rows().size();
        if (rowCount < 1) {
            return;
        }

        int columnCount = tableData.rows().get(0).columns().size();
        if (columnCount < 1) {
            return;
        }

        for (Row row : tableData.rows()) {
            for (Cell cell : row.columns()) {
                TableCellStyler styler = new TableCellStyler();
                cell.styleConfigurer().accept(styler);
                Font font = cellFont(styler);
                FontMetrics fontMetrics = getFontMetrics(font);

                maxTextWidth = Math.max(maxTextWidth, fontMetrics.stringWidth(cell.value()));
                maxTextHeight = Math.max(maxTextHeight, fontMetrics.getHeight());
            }
        }

        int requiredWidth = (maxTextWidth + (tableStyle.margin() * 2)) * columnCount +
                            (columnCount - 1) * tableStyle.borderThickness();
        int requiredHeight = (maxTextHeight + (tableStyle.margin() * 2)) * rowCount +
                             (rowCount - 1) * tableStyle.borderThickness();

        setSize(new Dimension(requiredWidth, requiredHeight));
    }

    private Font cellFont(TableCellStyler styler) {
        TableFont font = tableStyle.font();
        if (styler.fontName != null) {
            font = font.withFontName(styler.fontName);
        }
        if (styler.fontStyle != null) {
            font = font.withStyle(styler.fontStyle);
        }
        if (styler.fontSize != null) {
            font = font.withStyle(styler.fontSize);
        }
        return font.jfont();
    }

    /**
     * Show example table and save it in specified path
     */
    public static void main(String[] args) throws Exception {
        File file = new File(args[0]);
        TableData data = TableData
                .builder()
                .header("target", "metricA", "metricB")
                .addRow(Cell.of("foo"),
                        Cell.of("99.9999", styler -> styler.background(Defaults.SUCCESS_LIGHT)),
                        Cell.of("0.1", styler -> styler.background(Defaults.DANGER_LIGHT)))
                .addRow(Cell.of("bar"),
                        Cell.of("1300000", styler -> styler.background(Defaults.SUCCESS_LIGHT)),
                        Cell.of("99", styler -> styler.background(Defaults.SUCCESS_LIGHT)))
                .build();

        Table table = new Table(TableStyle.DEFAULT, data);
        table.setAutoSize();
        table.saveAsPNG(file);

        JFrame frame = new JFrame();
        frame.setSize(table.getSize());
        frame.setContentPane(table);
        frame.setVisible(true);
    }

    /**
     * Save this table as the PNG file to the specified file
     */
    public void saveAsPNG(File file) throws IOException {
        int dpi = 144;
        Dimension size = getSize();

        double scaleFactor = dpi / 72.0;
        BufferedImage img = new BufferedImage(
                (int)(size.width * scaleFactor),
                (int)(size.height * scaleFactor),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        AffineTransform transform = g2d.getTransform();
        transform.setToScale(scaleFactor, scaleFactor);
        g2d.setTransform(transform);
        Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName("png");
        if (it.hasNext()) {
            ImageWriter writer = it.next();
            // instantiate an ImageWriteParam object with default compression options
            ImageWriteParam iwp = writer.getDefaultWriteParam();

            ImageTypeSpecifier typeSpecifier =
                    ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_ARGB);
            IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, iwp);
            if (metadata.isReadOnly() || !metadata.isStandardMetadataFormatSupported()) {
                throw new IllegalArgumentException(
                        "It is not possible to set the DPI on a bitmap with "
                        + "png"
                        + " format!! Try another format.");
            }

            setDPI(metadata, dpi);
            paint(g2d);

            try (FileImageOutputStream output = new FileImageOutputStream(file)) {
                writer.setOutput(output);
                IIOImage image = new IIOImage(img, null, metadata);
                writer.write(null, image, iwp);
                writer.dispose();
            }
        }
        g2d.dispose();
    }

    // Code taken from https://github.com/knowm/XChart/blob/xchart-3.8.1/xchart/src/main/java/org/knowm/xchart/BitmapEncoder.java
    private static void setDPI(IIOMetadata metadata, int DPI) throws IOException {

        // for PNG, it's dots per millimeter
        double dotsPerMilli = 1.0 * DPI / 10 / 2.54;

        IIOMetadataNode horiz = new IIOMetadataNode("HorizontalPixelSize");
        horiz.setAttribute("value", Double.toString(dotsPerMilli));

        IIOMetadataNode vert = new IIOMetadataNode("VerticalPixelSize");
        vert.setAttribute("value", Double.toString(dotsPerMilli));

        IIOMetadataNode dim = new IIOMetadataNode("Dimension");
        dim.appendChild(horiz);
        dim.appendChild(vert);

        IIOMetadataNode root = new IIOMetadataNode("javax_imageio_1.0");
        root.appendChild(dim);

        metadata.mergeTree("javax_imageio_1.0", root);
    }
}
