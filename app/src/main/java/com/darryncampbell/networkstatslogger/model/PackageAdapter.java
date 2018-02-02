package com.darryncampbell.networkstatslogger.model;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.darryncampbell.networkstatslogger.R;

import java.util.List;

/**
 * Created by darry on 31/01/2018.
 */

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.PackageViewHolder> {

    List<Package> mPackageList;

    public PackageAdapter(List<Package> packageList) {
        mPackageList = packageList;
    }

    @Override
    public PackageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.package_card, parent, false);
        return new PackageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PackageViewHolder holder, int position) {
        Package packageItem = mPackageList.get(position);
        holder.name.setText(packageItem.getName());
        holder.packageName.setText(packageItem.getPackageName());
        String packageUidText = "uid: " + packageItem.getPackageUid();
        if (packageItem.getDuplicateUids())
            packageUidText += " (Shared with others)";
        holder.packageUid.setText(packageUidText);
        holder.receivedBytesTotal.setText("" + (char) 0x03A3 + " " + bytesToUI(packageItem.getReceivedBytesTotal()));
        holder.receivedBytesWifi.setText(bytesToUI(packageItem.getReceivedBytesWifi()) + "");
        holder.receivedBytesMobile.setText(bytesToUI(packageItem.getReceivedBytesMobile()) + "");
        holder.transmittedBytesTotal.setText("" + (char) 0x03A3 + " " + bytesToUI(packageItem.getTransmittedBytesTotal()));
        holder.transmittedBytesWifi.setText(bytesToUI(packageItem.getTransmittedBytesWifi()) + "");
        holder.transmittedBytesMobile.setText(bytesToUI(packageItem.getTransmittedBytesMobile()) + "");
        holder.receivedPacketsTotal.setText("" + (char) 0x03A3 + " " + packetsToUI(packageItem.getReceivedPacketsTotal()));
        holder.receivedPacketsWifi.setText(packetsToUI(packageItem.getReceivedPacketsWifi()) + "");
        holder.receivedPacketsMobile.setText(packetsToUI(packageItem.getReceivedPacketsMobile()) + "");
        holder.transmittedPacketsTotal.setText("" + (char) 0x03A3 + " " + packetsToUI(packageItem.getTransmittedPacketsTotal()));
        holder.transmittedPacketsWifi.setText(packetsToUI(packageItem.getTransmittedPacketsWifi()) + "");
        holder.transmittedPacketsMobile.setText(packetsToUI(packageItem.getTransmittedPacketsMobile()) + "");
        try {
            holder.icon.setImageDrawable(holder.context.getPackageManager().getApplicationIcon(packageItem.getPackageName()));
            holder.received_data_wifi_icon.setImageDrawable(holder.context.getDrawable(R.drawable.ic_signal_wifi_3_bar_black_24dp));
            holder.transmitted_data_wifi_icon.setImageDrawable(holder.context.getDrawable(R.drawable.ic_signal_wifi_3_bar_black_24dp));
            holder.received_packets_wifi_icon.setImageDrawable(holder.context.getDrawable(R.drawable.ic_signal_wifi_3_bar_black_24dp));
            holder.transmitted_packets_wifi_icon.setImageDrawable(holder.context.getDrawable(R.drawable.ic_signal_wifi_3_bar_black_24dp));
            holder.received_data_mobile_icon.setImageDrawable(holder.context.getDrawable(R.drawable.ic_sim_card_black_24dp));
            holder.transmitted_data_mobile_icon.setImageDrawable(holder.context.getDrawable(R.drawable.ic_sim_card_black_24dp));
            holder.received_packets_mobile_icon.setImageDrawable(holder.context.getDrawable(R.drawable.ic_sim_card_black_24dp));
            holder.transmitted_packets_mobile_icon.setImageDrawable(holder.context.getDrawable(R.drawable.ic_sim_card_black_24dp));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String bytesToUI(long bytes)
    {
        if (bytes > 1024*1024*1024)
        {
            return (bytes / 1024 / 1024 / 1024) + " GB";
        }
        else if (bytes > 1024*1024)
        {
            return (bytes / 1024 / 1024) + " MB";
        }
        else if (bytes > 1024)
        {
            return (bytes / 1024) + " KB";
        }
        else
            return bytes + " b";
    }

    private String packetsToUI(long packets)
    {
        long thousand= 1000L;
        long million = 1000000L;
        long billion = 1000000000L;
        long trillion= 1000000000000L;
        long number = Math.round(packets);
        if ((number >= thousand) && (number < million)) {
            float fraction = calculateFraction(number, thousand);
            return Float.toString(fraction) + "K";
        } else if ((number >= million) && (number < billion)) {
            float fraction = calculateFraction(number, million);
            return Float.toString(fraction) + "M";
        } else if ((number >= billion) && (number < trillion)) {
            float fraction = calculateFraction(number, billion);
            return Float.toString(fraction) + "B";
        }
        return Long.toString(number);
    }

    public float calculateFraction(long number, long divisor) {
        long truncate = (number * 10L + (divisor / 2L)) / divisor;
        float fraction = (float) truncate / 10F;
        return fraction;
    }

    public int getItemCount() {
        return mPackageList.size();
    }

    public class PackageViewHolder extends RecyclerView.ViewHolder {
        Context context;
        TextView name;
        TextView packageUid;
        TextView packageName;
        AppCompatImageView icon;
        AppCompatImageView received_data_wifi_icon;
        AppCompatImageView transmitted_data_wifi_icon;
        AppCompatImageView received_packets_wifi_icon;
        AppCompatImageView transmitted_packets_wifi_icon;
        AppCompatImageView received_data_mobile_icon;
        AppCompatImageView transmitted_data_mobile_icon;
        AppCompatImageView received_packets_mobile_icon;
        AppCompatImageView transmitted_packets_mobile_icon;
        TextView receivedBytesTotal;
        TextView receivedBytesWifi;
        TextView receivedBytesMobile;
        TextView transmittedBytesTotal;
        TextView transmittedBytesWifi;
        TextView transmittedBytesMobile;
        TextView receivedPacketsTotal;
        TextView receivedPacketsWifi;
        TextView receivedPacketsMobile;
        TextView transmittedPacketsTotal;
        TextView transmittedPacketsWifi;
        TextView transmittedPacketsMobile;

        public PackageViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            name = (TextView) itemView.findViewById(R.id.name);
            packageUid = (TextView) itemView.findViewById(R.id.package_uid);
            packageName = (TextView) itemView.findViewById(R.id.package_name);
            icon = (AppCompatImageView) itemView.findViewById(R.id.icon);
            received_data_wifi_icon = (AppCompatImageView) itemView.findViewById(R.id.received_data_wifi_icon);
            transmitted_data_wifi_icon = (AppCompatImageView) itemView.findViewById(R.id.transmitted_data_wifi_icon);
            received_packets_wifi_icon = (AppCompatImageView) itemView.findViewById(R.id.received_packets_wifi_icon);
            transmitted_packets_wifi_icon = (AppCompatImageView) itemView.findViewById(R.id.transmitted_packets_wifi_icon);
            received_data_mobile_icon = (AppCompatImageView) itemView.findViewById(R.id.received_data_mobile_icon);
            transmitted_data_mobile_icon = (AppCompatImageView) itemView.findViewById(R.id.transmitted_data_mobile_icon);
            received_packets_mobile_icon = (AppCompatImageView) itemView.findViewById(R.id.received_packets_mobile_icon);
            transmitted_packets_mobile_icon = (AppCompatImageView) itemView.findViewById(R.id.transmitted_packets_mobile_icon);
            receivedBytesTotal = (TextView) itemView.findViewById(R.id.lblReceivedTotal);
            receivedBytesWifi = (TextView) itemView.findViewById(R.id.lblReceivedWifi);
            receivedBytesMobile = (TextView) itemView.findViewById(R.id.lblReceivedMobile);
            transmittedBytesTotal = (TextView) itemView.findViewById(R.id.lblTransmittedTotal);
            transmittedBytesWifi = (TextView) itemView.findViewById(R.id.lblTransmittedWifi);
            transmittedBytesMobile = (TextView) itemView.findViewById(R.id.lblTransmittedMobile);
            receivedPacketsTotal = (TextView) itemView.findViewById(R.id.lblReceivedPacketsTotal);
            receivedPacketsWifi = (TextView) itemView.findViewById(R.id.lblReceivedPacketsWifi);
            receivedPacketsMobile = (TextView) itemView.findViewById(R.id.lblReceivedPacketsMobile);
            transmittedPacketsTotal = (TextView) itemView.findViewById(R.id.lblTransmittedPacketsTotal);
            transmittedPacketsWifi = (TextView) itemView.findViewById(R.id.lblTransmittedPacketsWifi);
            transmittedPacketsMobile = (TextView) itemView.findViewById(R.id.lblTransmittedPacketsMobile);
        }
    }


}
