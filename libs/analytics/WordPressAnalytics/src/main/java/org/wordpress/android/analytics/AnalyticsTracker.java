package org.wordpress.android.analytics;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class AnalyticsTracker {
    private static boolean mHasUserOptedOut;

    public static final String READER_DETAIL_TYPE_KEY = "post_detail_type";
    public static final String READER_DETAIL_TYPE_NORMAL = "normal";
    public static final String READER_DETAIL_TYPE_BLOG_PREVIEW = "preview-blog";
    public static final String READER_DETAIL_TYPE_TAG_PREVIEW = "preview-tag";

    public enum Stat {
        APPLICATION_OPENED,
        APPLICATION_CLOSED,
        THEMES_ACCESSED_THEMES_BROWSER,
        THEMES_CHANGED_THEME,
        THEMES_PREVIEWED_SITE,
        READER_ACCESSED,
        READER_OPENED_ARTICLE,
        READER_LIKED_ARTICLE,
        READER_REBLOGGED_ARTICLE,
        READER_INFINITE_SCROLL,
        READER_FOLLOWED_READER_TAG,
        READER_UNFOLLOWED_READER_TAG,
        READER_FOLLOWED_SITE,
        READER_LOADED_TAG,
        READER_LOADED_FRESHLY_PRESSED,
        READER_COMMENTED_ON_ARTICLE,
        READER_BLOCKED_BLOG,
        READER_BLOG_PREVIEW,
        READER_TAG_PREVIEW,
        STATS_ACCESSED,
        STATS_VIEW_ALL_ACCESSED,
        STATS_SINGLE_POST_ACCESSED,
        STATS_OPENED_WEB_VERSION,
        STATS_TAPPED_BAR_CHART,
        STATS_SCROLLED_TO_BOTTOM,
        EDITOR_CREATED_POST,
        EDITOR_ADDED_PHOTO_VIA_LOCAL_LIBRARY,
        EDITOR_ADDED_PHOTO_VIA_WP_MEDIA_LIBRARY,
        EDITOR_UPDATED_POST,
        EDITOR_SCHEDULED_POST,
        EDITOR_CLOSED_POST,
        EDITOR_PUBLISHED_POST,
        EDITOR_SAVED_DRAFT,
        EDITOR_PUBLISHED_POST_WITH_PHOTO,
        EDITOR_PUBLISHED_POST_WITH_VIDEO,
        EDITOR_PUBLISHED_POST_WITH_CATEGORIES,
        EDITOR_PUBLISHED_POST_WITH_TAGS,
        EDITOR_TAPPED_BLOCKQUOTE,
        EDITOR_TAPPED_BOLD,
        EDITOR_TAPPED_IMAGE,
        EDITOR_TAPPED_ITALIC,
        EDITOR_TAPPED_LINK,
        EDITOR_TAPPED_MORE,
        EDITOR_TAPPED_STRIKETHROUGH,
        EDITOR_TAPPED_UNDERLINE,
        NOTIFICATIONS_ACCESSED,
        NOTIFICATIONS_OPENED_NOTIFICATION_DETAILS,
        NOTIFICATION_REPLIED_TO,
        NOTIFICATION_APPROVED,
        NOTIFICATION_UNAPPROVED,
        NOTIFICATION_LIKED,
        NOTIFICATION_UNLIKED,
        NOTIFICATION_TRASHED,
        NOTIFICATION_FLAGGED_AS_SPAM,
        OPENED_POSTS,
        OPENED_PAGES,
        OPENED_COMMENTS,
        OPENED_VIEW_SITE,
        OPENED_VIEW_ADMIN,
        OPENED_MEDIA_LIBRARY,
        OPENED_SETTINGS,
        CREATED_ACCOUNT,
        SHARED_ITEM,
        ADDED_SELF_HOSTED_SITE,
        SIGNED_IN,
        SIGNED_INTO_JETPACK,
        PERFORMED_JETPACK_SIGN_IN_FROM_STATS_SCREEN,
        STATS_SELECTED_INSTALL_JETPACK,
        APPLICATION_STARTED,
        PUSH_NOTIFICATION_RECEIVED,
        SUPPORT_OPENED_HELPSHIFT_SCREEN,
        SUPPORT_SENT_REPLY_TO_SUPPORT_MESSAGE,
        LOGIN_FAILED,
        LOGIN_FAILED_TO_GUESS_XMLRPC
    }

    public interface Tracker {
        void track(Stat stat);
        void track(Stat stat, Map<String, ?> properties);
        void endSession();
        void refreshMetadata(boolean isUserConnected,boolean isWordPressComUser, boolean isJetpackUser,
                             int sessionCount, int numBlogs, int versionCode, String username, String email);
        void clearAllData();
        void registerPushNotificationToken(String regId);
    }

    private static final List<Tracker> TRACKERS = new ArrayList<Tracker>();

    private AnalyticsTracker() {
    }

    public static void init(Context context) {
        loadPrefHasUserOptedOut(context, false);
    }

    public static void loadPrefHasUserOptedOut(Context context, boolean manageSession) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        boolean hasUserOptedOut = !prefs.getBoolean("wp_pref_send_usage_stats", true);
        if (hasUserOptedOut != mHasUserOptedOut && manageSession) {
            mHasUserOptedOut = hasUserOptedOut;
            if (mHasUserOptedOut) {
                endSession(true);
                clearAllData();
            }
        }
    }

    public static void registerTracker(Tracker tracker) {
        if (tracker != null) {
            TRACKERS.add(tracker);
        }
    }

    public static void track(Stat stat) {
        if (mHasUserOptedOut) {
            return;
        }
        for (Tracker tracker : TRACKERS) {
            tracker.track(stat);
        }
    }

    public static void track(Stat stat, Map<String, ?> properties) {
        if (mHasUserOptedOut) {
            return;
        }
        for (Tracker tracker : TRACKERS) {
            tracker.track(stat, properties);
        }
    }

    public static void endSession(boolean force) {
        if (mHasUserOptedOut && !force) {
            return;
        }
        for (Tracker tracker : TRACKERS) {
            tracker.endSession();
        }
    }

    public static void registerPushNotificationToken(String regId) {
        if (mHasUserOptedOut) {
            return;
        }
        for (Tracker tracker : TRACKERS) {
            tracker.registerPushNotificationToken(regId);
        }
    }

    public static void clearAllData() {
        for (Tracker tracker : TRACKERS) {
            tracker.clearAllData();
        }
    }

    public static void refreshMetadata(boolean isUserConnected, boolean isWordPressComUser, boolean isJetpackUser,
                                       int sessionCount, int numBlogs, int versionCode, String username, String email) {
        for (Tracker tracker : TRACKERS) {
            tracker.refreshMetadata(isUserConnected, isWordPressComUser, isJetpackUser, sessionCount, numBlogs,
                    versionCode, username, email);
        }
    }
}

