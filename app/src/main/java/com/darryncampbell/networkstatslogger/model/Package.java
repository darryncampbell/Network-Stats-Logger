package com.darryncampbell.networkstatslogger.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by darry on 31/01/2018.
 */

public class Package implements Parcelable, Comparable {
    private String name;
    private String version;
    private String packageName;
    private int packageUid;
    private boolean duplicateUids; //  Whether multiple packages share this uid
    private long receivedBytesWifi;
    private long receivedBytesMobile;
    private long receivedBytesTotal;
    private long transmittedBytesWifi;
    private long transmittedBytesMobile;
    private long transmittedBytesTotal;
    private long receivedPacketsWifi;
    private long receivedPacketsMobile;
    private long receivedPacketsTotal;
    private long transmittedPacketsWifi;
    private long transmittedPacketsMobile;
    private long transmittedPacketsTotal;

    protected Package(Parcel in) {
        name = in.readString();
        version = in.readString();
        packageName = in.readString();
        packageUid = in.readInt();
        duplicateUids = in.readByte() != 0;
        receivedBytesWifi = in.readLong();
        receivedBytesMobile = in.readLong();
        receivedBytesTotal = in.readLong();
        transmittedBytesWifi = in.readLong();
        transmittedBytesMobile = in.readLong();
        transmittedBytesTotal = in.readLong();
        receivedPacketsWifi = in.readLong();
        receivedPacketsMobile = in.readLong();
        receivedPacketsTotal = in.readLong();
        transmittedPacketsWifi = in.readLong();
        transmittedPacketsMobile = in.readLong();
        transmittedPacketsTotal = in.readLong();
    }

    public Package() {}

    public static final Creator<Package> CREATOR = new Creator<Package>() {
        @Override
        public Package createFromParcel(Parcel in) {
            return new Package(in);
        }

        @Override
        public Package[] newArray(int size) {
            return new Package[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }

    public String getPackageName() {
        return packageName;
    }
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getPackageUid() {return packageUid;}
    public void setPackageUid(int packageUid) { this.packageUid = packageUid;}

    public boolean getDuplicateUids() {return duplicateUids;}
    public void setDuplicateUids(boolean dup) { this.duplicateUids = dup;}

    public long getReceivedBytesWifi() {return receivedBytesWifi;}
    public void setReceivedBytesWifi(long bytes) {receivedBytesWifi = bytes;}

    public long getReceivedBytesMobile() {return receivedBytesMobile;}
    public void setReceivedBytesMobile(long bytes) {receivedBytesMobile = bytes;}

    public long getReceivedBytesTotal() {return receivedBytesTotal;}
    public void setReceivedBytesTotal(long bytes) {receivedBytesTotal = bytes;}

    public long getTransmittedBytesWifi() {return transmittedBytesWifi;}
    public void setTransmittedBytesWifi(long bytes) {transmittedBytesWifi = bytes;}

    public long getTransmittedBytesMobile() {return transmittedBytesMobile;}
    public void setTransmittedBytesMobile(long bytes) {transmittedBytesMobile = bytes;}

    public long getTransmittedBytesTotal() {return transmittedBytesTotal;}
    public void setTransmittedBytesTotal(long bytes) {transmittedBytesTotal = bytes;}

    public long getReceivedPacketsWifi() {return receivedPacketsWifi;}
    public void setReceivedPacketsWifi(long bytes) {receivedPacketsWifi = bytes;}

    public long getReceivedPacketsMobile() {return receivedPacketsMobile;}
    public void setReceivedPacketsMobile(long bytes) {receivedPacketsMobile = bytes;}

    public long getReceivedPacketsTotal() {return receivedPacketsTotal;}
    public void setReceivedPacketsTotal(long bytes) {receivedPacketsTotal = bytes;}

    public long getTransmittedPacketsWifi() {return transmittedPacketsWifi;}
    public void setTransmittedPacketsWifi(long bytes) {transmittedPacketsWifi = bytes;}

    public long getTransmittedPacketsMobile() {return transmittedPacketsMobile;}
    public void setTransmittedPacketsMobile(long bytes) {transmittedPacketsMobile = bytes;}

    public long getTransmittedPacketsTotal() {return transmittedPacketsTotal;}
    public void setTransmittedPacketsTotal(long bytes) {transmittedPacketsTotal = bytes;}

    @Override
    public int compareTo(@NonNull Object comparePackage) {
        long compareMetric = ((Package)comparePackage).getReceivedPacketsTotal() + ((Package)comparePackage).getTransmittedPacketsTotal();
        return (int) (compareMetric - (this.getReceivedPacketsTotal() + this.getTransmittedPacketsTotal()));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(version);
        parcel.writeString(packageName);
        parcel.writeInt(packageUid);
        parcel.writeByte((byte) (duplicateUids ? 1 : 0));
        parcel.writeLong(receivedBytesWifi);
        parcel.writeLong(receivedBytesMobile);
        parcel.writeLong(receivedBytesTotal);
        parcel.writeLong(transmittedBytesWifi);
        parcel.writeLong(transmittedBytesMobile);
        parcel.writeLong(transmittedBytesTotal);
        parcel.writeLong(receivedPacketsWifi);
        parcel.writeLong(receivedPacketsMobile);
        parcel.writeLong(receivedPacketsTotal);
        parcel.writeLong(transmittedPacketsWifi);
        parcel.writeLong(transmittedPacketsMobile);
        parcel.writeLong(transmittedPacketsTotal);
    }
}
