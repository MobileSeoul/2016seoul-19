package com.hour24.toysrental.common.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.hour24.toysrental.common.view.floatingbutton.FloatingActionsMenu;

/**
 * Created by 장세진 on 2016-08-12.
 */
public class RecyclerViewToolbarHideShow {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private FloatingActionsMenu fabGroup;
    private Boolean isToolbarShow = true;
    public int gDy = 1;

    private static final float TOOLBAR_ELEVATION = 14f;

    public RecyclerViewToolbarHideShow(Toolbar toolbar, RecyclerView recyclerView) {
        this.toolbar = toolbar;
        this.recyclerView = recyclerView;
    }

    public RecyclerViewToolbarHideShow(Toolbar toolbar, RecyclerView recyclerView, FloatingActionsMenu fabGroup) {
        this.toolbar = toolbar;
        this.recyclerView = recyclerView;
        this.fabGroup = fabGroup;
    }

    public void onProc() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            // Keeps track of the overall vertical offset in the list
            int verticalOffset;

            // Determines the scroll UP/DOWN direction
            boolean scrollingUp;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (scrollingUp) {
                        if (verticalOffset > toolbar.getHeight()) {
                            toolbarAnimateHide();
                        } else {
                            toolbarAnimateShow(verticalOffset);
                        }
                    } else {
                        if (toolbar.getTranslationY() < toolbar.getHeight() * -0.6 && verticalOffset > toolbar.getHeight()) {
                            toolbarAnimateHide();
                        } else {
                            toolbarAnimateShow(verticalOffset);
                        }
                    }
                }
            }

            @Override
            public final void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                verticalOffset += dy;
                scrollingUp = dy > 0;
                int toolbarYOffset = (int) (dy - toolbar.getTranslationY());
                toolbar.animate().cancel();
                if (scrollingUp) {

                    if (fabGroup != null) {
                        fabGroup.setVisibility(View.VISIBLE);
                    }

                    if (toolbarYOffset < toolbar.getHeight()) {
                        if (verticalOffset > toolbar.getHeight()) {
                            toolbarSetElevation(TOOLBAR_ELEVATION);
                        }
                        toolbar.setTranslationY(-toolbarYOffset);
                    } else {
                        toolbarSetElevation(0);
                        toolbar.setTranslationY(-toolbar.getHeight());
                    }
                } else {

                    if (fabGroup != null) {
                        fabGroup.setVisibility(View.GONE);

                        if (fabGroup.isExpanded()) {
                            fabGroup.collapse();
                        }
                    }

                    if (toolbarYOffset < 0) {
                        if (verticalOffset <= 0) {
                            toolbarSetElevation(0);
                        }
                        toolbar.setTranslationY(0);
                    } else {
                        if (verticalOffset > toolbar.getHeight()) {
                            toolbarSetElevation(TOOLBAR_ELEVATION);
                        }
                        toolbar.setTranslationY(-toolbarYOffset);
                    }
                }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void toolbarSetElevation(float elevation) {
        // setElevation() only works on Lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(elevation);
        }
    }

    private void toolbarAnimateShow(final int verticalOffset) {

        isToolbarShow = false;

        toolbar.animate()
                .translationY(0)
                .setInterpolator(new LinearInterpolator())
                .setDuration(180)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        toolbarSetElevation(verticalOffset == 0 ? 0 : TOOLBAR_ELEVATION);
                    }
                });
    }

    private void toolbarAnimateHide() {

        isToolbarShow = true;

        if (fabGroup != null) {
            fabGroup.setVisibility(View.VISIBLE);
        }

        toolbar.animate()
                .translationY(-toolbar.getHeight())
                .setInterpolator(new LinearInterpolator())
                .setDuration(180)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        toolbarSetElevation(0);
                    }
                });

    }
}
