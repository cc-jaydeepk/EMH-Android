package com.everymomentholy.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.everymomentholy.R
import com.everymomentholy.api.response.NotificationDataVo
import com.everymomentholy.ui.activity.NotificationListActivity


class NotificationListAdapter(
    var context: Context, var notificationList: List<NotificationDataVo>,
    var notificationClickListner: NotificationListActivity
) : RecyclerView.Adapter<NotificationListAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var notificationTitle: TextView = view.findViewById(R.id.txtNotificationTitle)
        var notificationTime: TextView = view.findViewById(R.id.txtNotificationTime)
        var rlNotificationRawMain: RelativeLayout = view.findViewById(R.id.rl_notification_raw_main)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.notification_raw, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = notificationList[position]
        holder.notificationTitle.text = notification.title
        holder.notificationTime.text = notification.createdAt

        holder.rlNotificationRawMain.setOnClickListener() {
            notificationClickListner.onNotificationListClick(position, notification)
        }

        if (notification.mode == "Unread") {
            holder.rlNotificationRawMain.setBackgroundColor(context.resources.getColor(R.color.app_gray))
        } else {
            holder.rlNotificationRawMain.setBackgroundColor(context.resources.getColor(R.color.white))
        }
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }
}