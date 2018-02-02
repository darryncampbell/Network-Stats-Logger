package com.darryncampbell.networkstatslogger;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Intent;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.os.Build;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.darryncampbell.networkstatslogger.model.Package;
import com.darryncampbell.networkstatslogger.utils.Constants;
import com.darryncampbell.networkstatslogger.utils.TimingHelper;

import java.util.ArrayList;
import java.util.Collections;

public class NetworkStatsUpdateService extends IntentService {

    public NetworkStatsUpdateService() {
        super("NetworkStatsUpdateService");
    }

    @SuppressLint("MissingPermission")
    private String getSubscriberId(Context context, int networkType) {
        if (ConnectivityManager.TYPE_MOBILE == networkType) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                return tm.getSubscriberId();
            }
        }
        return "";
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (Constants.ACTION.ACTION_UPDATE_STATS.equals(action)) {
                ArrayList<Package> packageList = intent.getParcelableArrayListExtra(Constants.PARAMS.PACKAGE_LIST);
                Boolean saveStatsToFile = intent.getBooleanExtra(Constants.PARAMS.SAVE_STATS_TO_FILE, false);

                long ONE_MINUTE_IN_MS = 1000*60;
                long ONE_DAY_IN_MS = 1000* 60*60*24;
                long SEVEN_DAYS_IN_MS = 7 * ONE_DAY_IN_MS;
                long startTime = TimingHelper.getStartTime(this);
                long temp = System.currentTimeMillis();
                //long startTime = System.currentTimeMillis() - SEVEN_DAYS_IN_MS;
                //long startTime = System.currentTimeMillis() - ONE_DAY_IN_MS;
                //long startTime = System.currentTimeMillis() - ONE_MINUTE_IN_MS;

                for (int i = 0; i < packageList.size(); i++)
                {
                    long rxBytesWifi = 0;
                    long txBytesWifi = 0;
                    long rxBytesMobile = 0;
                    long txBytesMobile = 0;
                    long rxPacketsWifi = 0;
                    long txPacketsWifi = 0;
                    long rxPacketsMobile = 0;
                    long txPacketsMobile = 0;
                    long rxBytesTotal = 0;
                    long txBytesTotal = 0;
                    long rxPacketsTotal = 0;
                    long txPacketsTotal = 0;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        NetworkStats networkStatsWifi = null;
                        NetworkStats networkStatsMobile = null;
                        NetworkStatsManager networkStatsManager = (NetworkStatsManager) getSystemService(Context.NETWORK_STATS_SERVICE);

                        try {
                            networkStatsWifi = networkStatsManager.queryDetailsForUid(
                                    ConnectivityManager.TYPE_WIFI,
                                    "",
                                    startTime,
                                    System.currentTimeMillis() + SEVEN_DAYS_IN_MS,  //  Otherwise we don't catch all the recent data
                                    packageList.get(i).getPackageUid());
                            NetworkStats.Bucket bucket = new NetworkStats.Bucket();
                            while (networkStatsWifi.hasNextBucket()) {
                                networkStatsWifi.getNextBucket(bucket);
                                rxBytesWifi += bucket.getRxBytes();
                                txBytesWifi += bucket.getTxBytes();
                                rxPacketsWifi += bucket.getRxPackets();
                                txPacketsWifi += bucket.getTxPackets();
                                //Log.d(Constants.LOG.TAG, "Cumulative bytes: " + rxBytesWifi);
                            }
                        } catch (RemoteException e) {
                            Log.e(Constants.LOG.TAG, "Remote Exception: " + e.getMessage());
                        }
                        try {
                            networkStatsMobile = networkStatsManager.queryDetailsForUid(
                                    ConnectivityManager.TYPE_MOBILE,
                                    getSubscriberId(this, ConnectivityManager.TYPE_MOBILE),
                                    startTime,
                                    System.currentTimeMillis() + SEVEN_DAYS_IN_MS,
                                    packageList.get(i).getPackageUid());
                            NetworkStats.Bucket bucket = new NetworkStats.Bucket();
                            while (networkStatsMobile.hasNextBucket()) {
                                networkStatsMobile.getNextBucket(bucket);
                                rxBytesMobile += bucket.getRxBytes();
                                txBytesMobile += bucket.getTxBytes();
                                rxPacketsMobile += bucket.getRxPackets();
                                txPacketsMobile += bucket.getTxPackets();
                            }
                            rxBytesTotal = rxBytesWifi + rxBytesMobile;
                            txBytesTotal = txBytesWifi + txBytesMobile;
                            rxPacketsTotal = rxPacketsWifi + rxPacketsMobile;
                            txPacketsTotal = txPacketsWifi + txPacketsMobile;

                        } catch (RemoteException e) {
                            Log.e(Constants.LOG.TAG, "Remote Exception: " + e.getMessage());
                        }
                    }
                    else
                    {
                        //  Note: These only return data for our own UID on M and higher
                        //  Note: These only reset to zero after every reboot so the start / stop test logic doesn't
                        //        make sense for these
                        //  Note: After playing around with these briefly I don't think they give us enough info and
                        //        add confusion.  Changing the minimum API level to Marshmallow
                        rxBytesTotal = TrafficStats.getUidRxBytes(packageList.get(i).getPackageUid());
                        txBytesTotal = TrafficStats.getUidTxBytes(packageList.get(i).getPackageUid());
                        rxPacketsTotal = TrafficStats.getUidRxPackets(packageList.get(i).getPackageUid());
                        txPacketsTotal = TrafficStats.getUidTxPackets(packageList.get(i).getPackageUid());
                    }

                    packageList.get(i).setReceivedBytesWifi(rxBytesWifi);
                    packageList.get(i).setReceivedBytesMobile(rxBytesMobile);
                    packageList.get(i).setReceivedBytesTotal(rxBytesTotal);
                    packageList.get(i).setTransmittedBytesWifi(txBytesWifi);
                    packageList.get(i).setTransmittedBytesMobile(txBytesMobile);
                    packageList.get(i).setTransmittedBytesTotal(txBytesTotal);
                    packageList.get(i).setReceivedPacketsWifi(rxPacketsWifi);
                    packageList.get(i).setReceivedPacketsMobile(rxPacketsMobile);
                    packageList.get(i).setReceivedPacketsTotal(rxPacketsTotal);
                    packageList.get(i).setTransmittedPacketsWifi(txPacketsWifi);
                    packageList.get(i).setTransmittedPacketsMobile(txPacketsMobile);
                    packageList.get(i).setTransmittedPacketsTotal(txPacketsTotal);
                }

                //  Put the highest using packages at the top
                Collections.sort(packageList);

                //  Return the updated package list to the main activity
                Intent packageListUpdatedIntent = new Intent(Constants.ACTION.PACKAGE_LIST_UPDATED);
                packageListUpdatedIntent.putParcelableArrayListExtra(Constants.PARAMS.PACKAGE_LIST, packageList);
                packageListUpdatedIntent.putExtra(Constants.PARAMS.SAVE_STATS_TO_FILE, saveStatsToFile);
                sendBroadcast(packageListUpdatedIntent);
            }
        }
    }

}
