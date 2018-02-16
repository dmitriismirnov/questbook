package com.smirnov.dmitrii.questbook.ui.fragment.story;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.smirnov.dmitrii.questbook.R;
import com.smirnov.dmitrii.questbook.app.books.Books;
import com.smirnov.dmitrii.questbook.app.cache.DataProvider;
import com.smirnov.dmitrii.questbook.ui.fragment.BaseFragmentView;
import com.smirnov.dmitrii.questbook.ui.fragment.story.helpers.StoryAdapter;
import com.smirnov.dmitrii.questbook.ui.fragment.story.helpers.TextDisplayingListener;
import com.smirnov.dmitrii.questbook.ui.fragment.story.helpers.UserInteractionListener;
import com.smirnov.dmitrii.questbook.ui.fragment.story.helpers.items.StoryActionItem;
import com.smirnov.dmitrii.questbook.ui.fragment.story.helpers.items.StoryItem;
import com.smirnov.dmitrii.questbook.ui.model.story.StoryModel;
import com.smirnov.dmitrii.questbook.ui.model.story.StoryProgress;
import com.smirnov.dmitrii.questbook.ui.model.story.action.ActionModel;
import com.smirnov.dmitrii.questbook.ui.widget.StoryUserActionView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author Dmitry Smirnov
 * @version 14.11.2017.
 */

public class StoryFragment extends BaseFragmentView<StoryView, StoryPresenter>
        implements StoryView,
        TextDisplayingListener,
        UserInteractionListener {

    @SuppressWarnings("unused")
    private static final String TAG = StoryFragment.class.getSimpleName();

    private static final String EXTRA_BOOK_TYPE = "book-type";
    private static final String EXTRA_CONTINUE = "is-continue";
    private static final int SCROLL_DOWN_DEFAULT_DELAY = 100;

    @BindView(R.id.recycler_view)
    RecyclerView mStoryList;
    @BindView(R.id.user_action_view)
    StoryUserActionView mActionView;

    private StoryAdapter mAdapter;
    private StoryModel mCurrentStoryModel;
    private Books mCurrentBook;
    private String mCurrentChapter;
    public List<String> mCurrentFlags;

    @NonNull
    public static Fragment newInstance(@NonNull Bundle extras) {
        Fragment fragment = new StoryFragment();
        fragment.setArguments(extras);
        return fragment;
    }

    @Override
    public StoryPresenter createPresenter() {
        return new StoryPresenter();
    }

    @Override
    @LayoutRes
    protected int getLayoutId() {
        return R.layout.fragment_story;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new StoryAdapter(getContext());
        mStoryList.setAdapter(mAdapter);

        boolean isContinued = getArguments().getBoolean(EXTRA_CONTINUE, false);

        if (isContinued) {
            loadProgress();
        } else {
            mCurrentBook = (Books) getArguments().getSerializable(EXTRA_BOOK_TYPE);
            mCurrentChapter = mCurrentBook.getFirstChapter();
            mCurrentFlags = new ArrayList<>();
            saveProgress();
        }

        getPresenter().init();
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.setTextDisplayFinishListener(this);
        mActionView.setUserInteractionListener(this);
//        mAdapter.setUserInteractionListener(this);
    }

    @Override
    public void onPause() {
        mAdapter.setTextDisplayFinishListener(null);
        mActionView.setUserInteractionListener(null);
//        mAdapter.setUserInteractionListener(null);
        saveProgress();
        super.onPause();
    }

    @Override
    public void resetStory() {
        mAdapter.removeAllItems();
        resetProgress();
    }

    private void resetProgress() {
        mCurrentFlags.clear();
        mCurrentChapter = mCurrentBook.getFirstChapter();
        saveProgress();
    }

    @Override
    public void addStoryItem(@NonNull StoryItem storyItem) {
        mAdapter.addItem(storyItem);
        scrollDownDelayed();
    }

    @Override
    public void setUserAction(@NonNull StoryActionItem actionItem) {
        mActionView.clear();
        mActionView.setUpActionItem(actionItem);
        mActionView.show();

    }

    @Override
    public void showUserAction() {
        mActionView.show();
    }

    @Override
    public void hideUserAction() {
        mActionView.hide();
    }

    @Override
    public void removeLastItem() {
        mAdapter.removeItem(mAdapter.getItemCount() - 1);
    }

    @Override
    public void showToastMessage(@NonNull String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTextDisplayingFinished() {
        getPresenter().processTextShown();
        scrollDown();
        scrollDownDelayed();
    }

    @Override
    public void onViewSizeChanged() {
        scrollDown();
    }

    @Override
    public void onChooseAction(@NonNull ActionModel action) {
        getPresenter().processActionChosen(action);
    }

    @Override
    public void scrollDown() {
//        mStoryList.smoothScrollToPosition(mAdapter.getItemCount() - 1);
        scrollDownDelayed();
    }

    @Override
    public void scrollDownDelayed() {
        new Handler().postDelayed(() -> mStoryList.smoothScrollBy(0, getTargetScrollPosition(mStoryList)), SCROLL_DOWN_DEFAULT_DELAY);
    }

    @NonNull
    @Override
    public Books getCurrentBook() {
        return mCurrentBook;
    }

    @NonNull
    @Override
    public StoryModel getCurrentChapter() {
        return mCurrentStoryModel;
    }

    @NonNull
    @Override
    public String getCurrentChapterName() {
        return mCurrentChapter;
    }

    @NonNull
    @Override
    public List<String> getUserItems() {
        return mCurrentFlags;
    }

    @Override
    public void addUserItem(@NonNull String item) {
        mCurrentFlags.add(item);
        saveProgress();
    }

    @Override
    public boolean isUserHasItem(@NonNull String item) {
        return mCurrentFlags.contains(item);
    }

    @Override
    public void removeItem(@NonNull String item) {
        mCurrentFlags.remove(item);
        saveProgress();
    }

    @Override
    public void setCurrentChapter(@NonNull StoryModel storyModel, @NonNull String chapterName) {
        mCurrentStoryModel = storyModel;
        mCurrentChapter = chapterName;
    }

    public static int getTargetScrollPosition(@NonNull RecyclerView view) {
        return view.computeVerticalScrollRange() - view.computeVerticalScrollOffset();
    }

    private void saveProgress() {
        StoryProgress progress = new StoryProgress(mCurrentBook, mCurrentChapter, mCurrentFlags);
        DataProvider.provide().storeStoryProgress(progress);
    }

    private void loadProgress() {
        StoryProgress progress = DataProvider.provide().getStoryProgress();
        if (progress != null) {
            mCurrentBook = progress.getBook();
            mCurrentChapter = progress.getChapter();
            mCurrentFlags = progress.getFlags();
        }
    }
}
