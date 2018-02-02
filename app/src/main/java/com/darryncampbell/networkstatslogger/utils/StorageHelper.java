package com.darryncampbell.networkstatslogger.utils;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.darryncampbell.networkstatslogger.model.Package;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by darry on 01/02/2018.
 */

public class StorageHelper {

    public static final String TAG = "NetworkStatsLog";

    public String persistNetworkStatisticsToFile(Context context, ArrayList<Package> packageList)
    {
        File path =  Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS);
        SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String currentDateTimeStringFileName = df.format(new Date());
        SimpleDateFormat humanFormat = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss");
        String currentDateTimeStringHumanReadable = humanFormat.format(new Date());
        String endDateTimeHumanReadable = humanFormat.format(TimingHelper.getStartTime(context));
        File file = new File(path, "network_stats_" + currentDateTimeStringFileName + ".csv");
        Boolean isWriteable = isExternalStorageWritable();
        if (isWriteable)
        {
            try
            {
                path.mkdirs();
                file.createNewFile();
                if (file.exists())
                {
                    Log.i(TAG, "Log file location: " + file.getAbsolutePath());
                    String newLine = "\n";
                    OutputStream os = new FileOutputStream(file);
                    String deviceInformation = "Device Model: " + Build.MODEL + newLine +
                            "Build Number: " + Build.ID + newLine + "Serial Number: " + Build.SERIAL
                            + newLine + "Start Date / Time: " + endDateTimeHumanReadable + newLine
                            + "Finish Date / Time: " + currentDateTimeStringHumanReadable + newLine;
                    os.write(deviceInformation.getBytes());
                    os.write(CSVHeaderRow().getBytes());
                    os.write(newLine.getBytes());
                    for (int i = 0; i < packageList.size(); i++)
                    {
                        os.write(CSVRow(packageList.get(i)).getBytes());
                        os.write(newLine.getBytes());
                    }
                    os.close();
                    // Tell the media scanner about the new file so that it is
                    // immediately available to the user.
                    MediaScannerConnection.scanFile(context,
                            new String[] { file.toString() }, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("ExternalStorage", "Scanned " + path + ":");
                                    Log.i("ExternalStorage", "-> uri=" + uri);
                                }
                            });
                    return file.getAbsolutePath();
                }
                else
                {
                    Log.e(TAG, "Error creating log file");
                    Toast.makeText(context, "Error creating log file", Toast.LENGTH_LONG).show();
                    return "";
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e(TAG, "Error writing " + file, e);
                Toast.makeText(context, "Error writing log file", Toast.LENGTH_LONG).show();
                return "";
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Error writing " + file, e);
                Toast.makeText(context, "Error writing log file", Toast.LENGTH_LONG).show();
                return "";
            }
        }
        return "";
    }

    public static String CSVHeaderRow()
    {
        return "Application Name," +
                "Package Name," +
                "Package Uid," +
                "Rx Bytes (Wifi)," +
                "Rx Bytes (Mobile)," +
                "Rx Bytes (Total)," +
                "Tx Bytes (Wifi)," +
                "Tx Bytes (Mobile)," +
                "Tx Bytes (Total)," +
                "Rx Packets (Wifi)," +
                "Rx Packets (Mobile)," +
                "Rx Packets (Total)," +
                "Tx Packets (Wifi)," +
                "Tx Packets (Mobile)," +
                "Tx Packets (Total),";
    }

    public String CSVRow(Package packageStats)
    {
        return packageStats.getName() + "," + packageStats.getPackageName() + "," +
                packageStats.getPackageUid() + "," +
                packageStats.getReceivedBytesWifi() + "," + packageStats.getReceivedBytesMobile() + "," +
                packageStats.getReceivedBytesTotal() + "," + packageStats.getTransmittedBytesWifi() + "," +
                packageStats.getTransmittedBytesMobile() + "," + packageStats.getTransmittedBytesTotal() + "," +
                packageStats.getReceivedPacketsWifi() + "," + packageStats.getReceivedPacketsMobile() + "," +
                packageStats.getReceivedPacketsTotal() + "," + packageStats.getTransmittedPacketsWifi() + "," +
                packageStats.getTransmittedPacketsMobile() + "," + packageStats.getTransmittedPacketsTotal() + ",";
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

}
