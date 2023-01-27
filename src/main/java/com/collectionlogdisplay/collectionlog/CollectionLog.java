package com.collectionlogdisplay.collectionlog;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;

@AllArgsConstructor
public class CollectionLog
{
    @Getter
    private final ArrayList<CollectionLogItem> collLogItems ;


}