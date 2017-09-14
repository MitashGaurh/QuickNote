package com.mitash.quicknote.model;

import java.util.Date;

/**
 * Created by Mitash Gaurh on 9/9/2017.
 */

public interface Note {

    int getId();

    String getTitle();

    String getNoteText();

    Date getCreatedDate();

    Date getUpdatedDate();
}
