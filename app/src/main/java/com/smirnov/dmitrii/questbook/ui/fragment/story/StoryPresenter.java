package com.smirnov.dmitrii.questbook.ui.fragment.story;

import com.smirnov.dmitrii.questbook.ui.fragment.story.helpers.Generator;
import com.smirnov.dmitrii.questbook.ui.model.story.action.ActionModel;

import java.util.Random;

import ru.mvp.RxPresenter;

/**
 * @author Dmitry Smirnov
 * @version 14.11.2017.
 */

public class StoryPresenter extends RxPresenter<StoryView> {

    private static final String TAG = StoryPresenter.class.getSimpleName();
    private Random mRandom = new Random();

    public void init() {
        getView().resetStory();

        if (mRandom.nextBoolean()) {
            getView().addStoryItem(Generator.getRandomImageItem());
        }
        getView().addStoryItem(Generator.getRandomChapterItem());

    }

    public void processTextShown() {
        getView().addStoryItem(Generator.getRandomActionItem());
    }

    public void processActionChosen(ActionModel model) {
        getView().showToastMessage(model.getName());
        getView().removeLastItem();
        if (mRandom.nextBoolean()) {
            getView().addStoryItem(Generator.getRandomImageItem());
        }
        getView().addStoryItem(Generator.getRandomChapterItem());
    }

}
