*Please be aware that this application / sample is provided as-is for demonstration purposes without any guarantee of support*
=========================================================

# Network Stats Logger
This application is designed to determine the amount of data being sent and received by the applications on your Android device in a controlled fashion.

Android exposes the [Network Stats API](https://developer.android.com/reference/android/app/usage/NetworkStats.html) which is only supported on Marshmallow and higher so as such this application requires a minimum API level of 23.  Android also exposes a [Traffic Stats API](https://developer.android.com/reference/android/net/TrafficStats.html) which works on pre-M devices but since that API aggregates all data per-app since boot it does not offer enough control for what this logging app is trying to do. 

**Aren't there thousands of these type of apps?**  There are many applications designed around checking you do not exceed your data allowance or checking data use in the last 7 or 30 days.  There are far fewer apps out there which allow you to specify a specific time to start logging and then export those results for later analysis.  Other apps will obsfucate how they tally up data usage, are difficult to verify and also group many applications into 'System' whereas I would like to have greater granularity than that. 

## Credits
The look and feel of this app as well as the bootstrapping is taken from https://github.com/RobertZagorski/NetworkStats.  I needed to make a couple of fixes to how data is returned (see https://github.com/RobertZagorski/NetworkStats/issues/2) & add a lot of functionality but full credit to the author of that app for the look & feel and core functionality.  https://github.com/RobertZagorski/NetworkStats does not have any license specified therefore I have not specified a license type for this app.   

## Screenshots
![Options](https://raw.githubusercontent.com/darryncampbell/Network-Stats-Logger/master/screenshots/1.jpg)
![Retrieving stats](https://raw.githubusercontent.com/darryncampbell/Network-Stats-Logger/master/screenshots/2.jpg)
![Stats](https://raw.githubusercontent.com/darryncampbell/Network-Stats-Logger/master/screenshots/3.jpg)

## Requesting permissions
This application requires several runtime permissions as well as the ability to monitor usage stats.  All permissions are requested when the app is first launched and should be accepted by the user.  If you do not accept the permissions at launch then you can request them later through the menu.  **This is just a test app so if you don't accept permissions you may experience unexpected behaviour**

## Running a test
The intended way to run a test is as follows:
1. Select _Start Test_ from the menu, this records the start time of the test internally
2. At any point you can update the network statistics for all applications on the device with the _Update Stats (UI only)_ option.
3. You can record an intermediate set of results during the test which will update the statistics and write a csv file to the external storage documents directory, _Update Stats and Log_.
4. The following metrics are reported both on the UI and in the CSV for the test
  - Data sent and received over Wifi and mobile
  - Packets sent and received over Wifi and mobile
  - Total data and packets sent and received per app

5. Because of the way the Android API works, it is recommended to leave your test running for at least 3 hours for reliable results but the longer the test can run the better.
6. Once you want the final results for your test select _Stop Test_.  This will also write a csv file to the external storage documents directory which you can copy off via adb.
7. You can only have one test running at a time.

## Package uids
Each package has an associated kernal user-id (or uid) assigned to it, this uid is passed to the Network Stats API to differentiate between packages however it is not unique on the device.  What this means is that **you may see multiple applications reporting the same Rx/Tx statistics because under the covers they share the same uid**.  

I have tried to make this obvious by indicating on the UI where an application has a shared uid and uids are also written to the csv file.

Most other applications like this will group these kind of packages into "System" but I wanted to maintain the granularity of reporting statistics for as many applications or services as possible.

## Wait screen
It takes some time for the system to iterate over every package and determine statistics for that package.  Please wait a few seconds when selecting _Update Stats_ from the menu to allow it to update.
