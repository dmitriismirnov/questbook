package com.smirnov.dmitrii.questbook.ui.fragment.story;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.smirnov.dmitrii.questbook.app.books.Books;
import com.smirnov.dmitrii.questbook.ui.fragment.story.helpers.items.StoryActionItem;
import com.smirnov.dmitrii.questbook.ui.fragment.story.helpers.items.StoryItem;
import com.smirnov.dmitrii.questbook.ui.model.story.StoryModel;

import java.util.List;

import ru.mvp.IView;

/**
 * @author Dmitry Smirnov
 * @version 14.11.2017.
 */

public interface StoryView extends IView {

    void resetStory();

    void addStoryItem(@NonNull StoryItem storyItem);

    void setUserAction(@NonNull StoryActionItem actionItem);

    void showUserAction();

    void hideUserAction();

    void removeLastItem();

    void showToastMessage(@NonNull String message);

    void scrollDown();

    void scrollDownDelayed();

    @NonNull
    Books getCurrentBook();

    @NonNull
    StoryModel getCurrentChapter();

    @NonNull
    String getCurrentChapterName();

    @NonNull
    List<String> getUserItems();

    void addUserItem(@NonNull String item);

    boolean isUserHasItem(@NonNull String item);

    void removeItem(@NonNull String item);

    void setCurrentChapter(@NonNull StoryModel storyModel, @NonNull String chapterName);
}
