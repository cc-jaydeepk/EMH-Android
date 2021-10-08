package com.everymomentholy.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.everymomentholy.R
import com.everymomentholy.api.response.DataVo
import com.everymomentholy.ui.fragments.NotificationListFragment


class NotificationListAdapter(
    var context: Context, var notificationList: List<DataVo>,
    var notificationClickListner: NotificationListFragment
) : RecyclerView.Adapter<NotificationListAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var notificationTitle: TextView = view.findViewById(R.id.txtNotificationTitle)
        var notificationTime: TextView = view.findViewById(R.id.txtNotificationTime)
        var layoutLinear: LinearLayout = view.findViewById(R.id.layoutLinear)
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

        holder.layoutLinear.setOnClickListener() {
//            val intent = Intent(context, NotificationDetailActivity::class.java)
//            intent.putExtra("date", notification.createdAt)
//            intent.putExtra("message", notification.message)
//            context.startActivity(intent)

            notificationClickListner.onNotificationListClick(position, notification)

        }

        /* holder.layoutLinear.setOnClickListener(object : View.OnClickListener {
             override fun onClick(v: View?) {
                 val bundle = Bundle()
                 bundle.putString("date", notification.createdAt)
                 bundle.putString("message", notification.message)

                 //val notificationDetail = NotificationDetailFragment()
                 //notificationDetail.setArguments(bundle)

                 val activity = v!!.context as AppCompatActivity
                 val notificationDetail = NotificationDetailFragment()
                 activity.supportFragmentManager.beginTransaction()
                     .replace(R.id.notificationLinear, notificationDetail).addToBackStack(null)
                     .commit()

                 notificationDetail.setArguments(bundle)
             }

         })*/
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }
}