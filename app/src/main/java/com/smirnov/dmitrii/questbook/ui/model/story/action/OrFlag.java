package com.smirnov.dmitrii.questbook.ui.model.story.action;

import java.util.List;

/**
 * @author Дмитрий
 * @version 26.11.2017.
 */

public class OrFlag extends FlagModel {

    public OrFlag(List<String> items) {
        super(items);
    }

    @Override
    boolean canPassTheFlag(List<String> userItems) {
        for (String flag : getFlags()) {
            for (String userItem : userItems) {
                if (flag.equalsIgnoreCase(userItem)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    void passTheFlag(List<String> userItems) {
        //do nothing
    }
}
