package com.mayreh.lowtable;

import org.junit.Test;

public class TableDataTest {
    @Test(expected = IllegalArgumentException.class)
    public void testColumnCountValidation() {
        TableData.builder()
                 .addRow("foo")
                 .addRow("foo", "bar")
                 .build();
    }
}
