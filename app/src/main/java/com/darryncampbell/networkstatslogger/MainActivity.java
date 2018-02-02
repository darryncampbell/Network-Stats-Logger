package com.darryncampbell.networkstatslogger;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.darryncampbell.networkstatslogger.model.Package;
import com.darryncampbell.networkstatslogger.utils.Constants;
import com.darryncampbell.networkstatslogger.model.PackageAdapter;
import com.darryncampbell.networkstatslogger.utils.StorageHelper;
import com.darryncampbell.networkstatslogger.utils.TimingHelper;

import java.util.ArrayList;
import java.util.List;

import static com.darryncampbell.networkstatslogger.utils.TimingHelper.getTestDurationInMins;

//  Credit: Heavily influenced by https://github.com/RobertZagorski/NetworkStats,
//          particularly requesting permissions and the bootstrapping

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Package> packageList;
    private RecyclerView.Adapter packageAdapter;
    private NetworkStatsUpdateServiceReceiver networkStatsUpdateServiceReceiver;
    private ProgressDialog statsUpdateDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        statsUpdateDialog = new ProgressDialog(this);
        packageList = getPackagesData();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        packageAdapter = new PackageAdapter(packageList);
        recyclerView.setAdapter(packageAdapter);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    protected void onResume() {
        super.onResume();

        if (!hasPermissions()) {
            requestPermissions();
        }

        if (networkStatsUpdateServiceReceiver == null)
            networkStatsUpdateServiceReceiver = new NetworkStatsUpdateServiceReceiver();
        //  Listen for messages from the service
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION.PACKAGE_LIST_UPDATED);
        registerReceiver(networkStatsUpdateServiceReceiver, intentFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_request_permissions) {
            requestPermissions();
            return true;
        }
        else if (id == R.id.menu_start_test) {
            if (TimingHelper.getIsTestRunning(this))
            {
                Toast.makeText(this, "Test is already running.  Please stop current test first", Toast.LENGTH_LONG).show();
            }
            else
            {
                TimingHelper.setStartTime(this);
                TimingHelper.setIsTestRunning(this, true);
                Toast.makeText(this, "Started test at " + TimingHelper.getStartTimeForUI(this), Toast.LENGTH_LONG).show();
            }
            return true;
        }
        else if (id == R.id.menu_update_stats) {
            if (!TimingHelper.getIsTestRunning(this))
            {
                Toast.makeText(this, "There is no test running", Toast.LENGTH_LONG).show();
            }
            else
            {
                long testDurationInMins = getTestDurationInMins(System.currentTimeMillis(), this);
                if (testDurationInMins <= Constants.MISC.MINIMUM_RECOMMENDED_TEST_TIME)
                    Toast.makeText(this, getString(R.string.minimum_duration_error), Toast.LENGTH_LONG).show();
                UpdateNetworkStats("Updating network stats so far during test (UI only)\nTest duration: " + testDurationInMins + "mins",
                        false);
            }
            return true;
        }
        else if (id == R.id.menu_update_stats_and_log) {
            if (!TimingHelper.getIsTestRunning(this))
            {
                Toast.makeText(this, "There is no test running", Toast.LENGTH_LONG).show();
            }
            else
            {
                long testDurationInMins = getTestDurationInMins(System.currentTimeMillis(), this);
                if (testDurationInMins <= Constants.MISC.MINIMUM_RECOMMENDED_TEST_TIME)
                    Toast.makeText(this, getString(R.string.minimum_duration_error), Toast.LENGTH_LONG).show();
                UpdateNetworkStats("Updating network stats so far during test (will log result)\nTest duration: " + testDurationInMins + "mins",
                        true);
            }
            return true;
        }
        else if (id == R.id.menu_stop_test) {
            if (!TimingHelper.getIsTestRunning(this))
            {
                Toast.makeText(this, "There is no test running", Toast.LENGTH_LONG).show();
            }
            else
            {
                TimingHelper.setIsTestRunning(this, false);
                long testDurationInMins = getTestDurationInMins(System.currentTimeMillis(), this);
                if (testDurationInMins < Constants.MISC.MINIMUM_RECOMMENDED_TEST_TIME)
                    Toast.makeText(this, getString(R.string.minimum_duration_error), Toast.LENGTH_LONG).show();
                UpdateNetworkStats("Gathering network stats at the end of the test\nTest duration: " + testDurationInMins + "mins",
                        true);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    //////////////////////////////////////////////////////////////////////////////////////////

    private void UpdateNetworkStats(String messageToUser, Boolean saveStatsToFile)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            statsUpdateDialog.setMessage(messageToUser);
            statsUpdateDialog.setTitle("Retrieving network stats");
            statsUpdateDialog.setIndeterminate(false);
            statsUpdateDialog.setCancelable(true);
            statsUpdateDialog.show();

            Intent updateStatsIntent = new Intent(MainActivity.this, NetworkStatsUpdateService.class);
            updateStatsIntent.putParcelableArrayListExtra(Constants.PARAMS.PACKAGE_LIST, packageList);
            updateStatsIntent.putExtra(Constants.PARAMS.SAVE_STATS_TO_FILE, saveStatsToFile);
            updateStatsIntent.setAction(Constants.ACTION.ACTION_UPDATE_STATS);
            startService(updateStatsIntent);
        }
    }


    private class NetworkStatsUpdateServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.ACTION.PACKAGE_LIST_UPDATED))
            {
                ArrayList<Package> packageListLocal = intent.getParcelableArrayListExtra(Constants.PARAMS.PACKAGE_LIST);
                packageList.clear();
                packageList.addAll(packageListLocal);
                packageAdapter.notifyDataSetChanged();
                statsUpdateDialog.dismiss();
                Boolean saveStatsToFile = intent.getBooleanExtra(Constants.PARAMS.SAVE_STATS_TO_FILE, false);
                if (saveStatsToFile)
                {
                    StorageHelper fileHelper = new StorageHelper();
                    String fileLocation = fileHelper.persistNetworkStatisticsToFile(getApplicationContext(), packageList);
                    if (!fileLocation.equals(""))
                        Toast.makeText(getApplicationContext(), "Statistics written to " + fileLocation , Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    private ArrayList<Package> getPackagesData() {
        PackageManager packageManager = getPackageManager();
        List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);
        packageList = new ArrayList<>(packageInfoList.size());
        for (PackageInfo packageInfo : packageInfoList) {
            Package packageItem = new Package();
            packageItem.setVersion(packageInfo.versionName);
            packageItem.setPackageName(packageInfo.packageName);
            packageItem.setPackageUid(getPackageUid(getApplicationContext(), packageInfo.packageName));
            packageList.add(packageItem);
            ApplicationInfo ai = null;
            try {
                ai = packageManager.getApplicationInfo(packageInfo.packageName, PackageManager.GET_META_DATA);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (ai == null) {
                continue;
            }
            CharSequence appName = packageManager.getApplicationLabel(ai);
            if (appName != null) {
                packageItem.setName(appName.toString());
            }
        }
        //  Go through the package list and set whether the entries have duplicate uids
        for (int i = 0; i < packageList.size(); i++)
        {
            if (packageList.get(i).getDuplicateUids())
                continue;
            int packageUid = packageList.get(i).getPackageUid();
            for (int j = 0; j < packageList.size(); j++)
            {
                if (i == j)
                    continue;
                else
                {
                    if (packageUid == packageList.get(j).getPackageUid()) {
                        packageList.get(i).setDuplicateUids(true);
                        packageList.get(j).setDuplicateUids(true);
                    }
                }
            }
        }
        return packageList;
    }

    private static int getPackageUid(Context context, String packageName) {
        /*
        uid
        The kernel user-ID that has been assigned to this application;
        currently this is not a unique ID (multiple applications can have the same uid).
         */
        PackageManager packageManager = context.getPackageManager();
        int uid = -1;
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA);
            uid = packageInfo.applicationInfo.uid;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return uid;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void requestPermissions() {
        if (!hasPermissionToReadNetworkHistory()) {
            return;
        }
        if (!hasPermissionToReadPhoneStats()) {
            requestPhoneStateStats();
        }
    }

    private boolean hasPermissions() {
        return hasPermissionToReadNetworkHistory() && hasPermissionToReadPhoneStats();
    }

    private boolean hasPermissionToReadPhoneStats() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            return false;
        } else {
            return true;
        }
    }

    private void requestPhoneStateStats() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                Constants.PERMISSION.PERMISSION_REQUEST);
    }

    private boolean hasPermissionToReadNetworkHistory() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        final AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        if (mode == AppOpsManager.MODE_ALLOWED) {
            return true;
        }
        Toast.makeText(this, "Usage Stats permission is required", Toast.LENGTH_LONG).show();
        appOps.startWatchingMode(AppOpsManager.OPSTR_GET_USAGE_STATS,
                getApplicationContext().getPackageName(),
                new AppOpsManager.OnOpChangedListener() {
                    @Override
                    @TargetApi(Build.VERSION_CODES.M)
                    public void onOpChanged(String op, String packageName) {
                        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                                android.os.Process.myUid(), getPackageName());
                        if (mode != AppOpsManager.MODE_ALLOWED) {
                            return;
                        }
                        appOps.stopWatchingMode(this);
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        if (getIntent().getExtras() != null) {
                            intent.putExtras(getIntent().getExtras());
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(intent);
                    }
                });
        requestReadNetworkHistoryAccess();
        return false;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void requestReadNetworkHistoryAccess() {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivity(intent);
    }
}
