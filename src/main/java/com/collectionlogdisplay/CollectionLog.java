package com.collectionlogdisplay;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Map;

@AllArgsConstructor
public class CollectionLog
{
    @Getter
    private final String username;

    @Getter
    private final int totalObtained;

    @Getter
    private final int totalItems;

    @Getter
    private final int uniqueObtained;

    @Getter
    private final int uniqueItems;

    @Getter
    private final ArrayList<CollectionLogItem> collLogItems ;


}