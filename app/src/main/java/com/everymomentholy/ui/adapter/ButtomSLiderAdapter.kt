package com.everymomentholy.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.everymomentholy.R

class ButtomSLiderAdapter: RecyclerView.Adapter<ButtomSLiderAdapter.MyViewHolder>() {
    // lateinit var imgShare: ImageView
    //var context: Context? = null

    var freeLiturgiesList: ArrayList<Fragment> = ArrayList()

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var imgShare = view.findViewById<ImageView>(R.id.imgShare)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.buttomslider_raw, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val context = holder.itemView.context
        holder.imgShare.setOnClickListener {
            /* val builder = AlertDialog.Builder(context)
             val inflater = layoutInflater
             val dialogLayout = inflater.inflate(R.layout.register_dialog, null)

             builder.setView(dialogLayout)
             builder.show()*/

            val builder = AlertDialog.Builder(context)
            val view: View = LayoutInflater.from(context).inflate(R.layout.share_dialog, null)
            builder.setView(view)
            builder.show()
        }

        /* val builder = AlertDialog.Builder(v.getContext())
         val view: View = LayoutInflater.from(context).inflate(R.layout.share_dialog, null)
         builder.setView(view)

         builder.setNegativeButton(
             "back"
         ) { dialog, which -> dialog.dismiss() }
         val alertDialog = builder.create()
         alertDialog.show()*/
    }

    override fun getItemCount(): Int {
        return 3
        //return freeLiturgiesList.size
    }
}