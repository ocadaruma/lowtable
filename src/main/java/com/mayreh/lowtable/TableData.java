package com.mayreh.lowtable;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mayreh.lowtable.TableStyle.TableCellStyle;

import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;

@Value
@Accessors(fluent = true)
public class TableData {
    @Value
    @Accessors(fluent = true)
    public static class Cell {
        String value;

        @NonNull
        TableCellStyle style;

        public static Cell of(String value) {
            return new Cell(value, TableCellStyle.NONE);
        }

        public static Cell of(String value, TableCellStyle style) {
            return new Cell(value, style);
        }
    }

    @Value
    @Accessors(fluent = true)
    public static class Row {
        List<Cell> columns;
    }

    List<Row> rows;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<Row> rows = new ArrayList<>();

        public Builder header(Cell... columns) {
            rows.add(0, new Row(Arrays.asList(columns)));
            return this;
        }

        public Builder header(String... columns) {
            rows.add(0, new Row(Arrays.stream(columns).map(Cell::of).collect(toList())));
            return this;
        }

        public Builder addRow(Cell... columns) {
            addRow(new Row(Arrays.asList(columns)));
            return this;
        }

        public Builder addRow(String... columns) {
            addRow(new Row(Arrays.stream(columns).map(Cell::of).collect(toList())));
            return this;
        }

        public Builder addRow(Row row) {
            rows.add(row);
            return this;
        }

        public TableData build() {
            if (!rows.isEmpty()) {
                Row head = rows.get(0);
                for (Row row : rows) {
                    if (head.columns.size() != row.columns.size()) {
                        throw new IllegalArgumentException("must have same column count with header");
                    }
                }
            }
            return new TableData(rows);
        }
    }
}
