package com.darryncampbell.networkstatslogger.utils;

/**
 * Created by darry on 01/02/2018.
 */

public class Constants {
    public interface PERMISSION {
        int PERMISSION_REQUEST = 1;
    }

    public interface ACTION {
        String ACTION_UPDATE_STATS = "com.darryncampbell.networkstatslogger.action.UPDATE_STATS";
        String PACKAGE_LIST_UPDATED = "com.darryncampbell.networkstatslogger.action.PACKAGE_LIST_UPDATED";
    }

    public interface PARAMS {
        String PACKAGE_LIST = "com.darryncampbell.networkstatslogger.extra.PACKAGE_LIST";
        String SAVE_STATS_TO_FILE = "com.darryncampbell.networkstatslogger.extra.SAVE_STATS_TO_FILE";
    }

    public interface MISC {
        long MINIMUM_RECOMMENDED_TEST_TIME = 180;
    }

    public interface LOG {
        String TAG = "NetworkStatsLog";
    }
}
