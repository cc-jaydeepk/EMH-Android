package com.everymomentholy.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.everymomentholy.R
import com.everymomentholy.api.response.SubScriptionPlanSubResponseVo
import com.everymomentholy.interfaces.SubscriptionPlanListCLick

class SubscriptionListPlanAdapter(
    var context: Context,
    var subscriptionPlanList: ArrayList<SubScriptionPlanSubResponseVo>,
    var planTypeClickListner: SubscriptionPlanListCLick
) : RecyclerView.Adapter<SubscriptionListPlanAdapter.MyViewHolder>() {

    var isSelected: Boolean = false
    private var selectedItemPosition: Int = 0

    lateinit var selectedPlanType: String

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textPlaneName = view.findViewById<TextView>(R.id.txtMonthly)
        var textMonthlyPrice = view.findViewById<TextView>(R.id.txtMontlyPrice)
        var textCurrency = view.findViewById<TextView>(R.id.txtCurrency)
        var txtUnlimitedAccess = view.findViewById<TextView>(R.id.txtUnlimitedAccess)
        var textMonthlyDesc = view.findViewById<TextView>(R.id.txtMonthlyDes)
        var linearMonthly = view.findViewById<LinearLayout>(R.id.linearMonthly);
        var txtSaving = view.findViewById<TextView>(R.id.txtSaving);

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.subscriptionplan_list_raw, parent, false)
        return SubscriptionListPlanAdapter.MyViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        var planList = subscriptionPlanList[position]


        holder.textPlaneName.text = planList.plan_type
        holder.textMonthlyPrice.text = planList.unit_amount
        // holder.textCurrency.text = planList.currency
        holder.textMonthlyDesc.text = planList.description
        holder.txtUnlimitedAccess.text = planList.plan_message

        if (planList.plan_savings == "") {
            //  holder.txtSaving.text = "(Savings of $" + "" + planList.plan_savings + "/month)"
            holder.txtSaving.visibility = View.GONE
        } else {
            holder.txtSaving.text = "(Save $" + "" + planList.plan_savings + "/year)"
        }

        if(planList.plan_type == "Annually"){
            selectedPlanType = "Yearly"
        }else{
            selectedPlanType = "Monthly"
        }

        holder.linearMonthly.setOnClickListener {
            selectedItemPosition = position

           // planTypeClickListner.onSelectPlan(position, planList.plan_type, true)
          //  planTypeClickListner.onSelectPlan(position, planList.plan_type, true)

            planTypeClickListner.onSelectPlan(position, selectedPlanType, true)
            notifyDataSetChanged()

            // liturgyListClickListner.onMyLiturgiesListClick(position, myLiturgies.bookId, true)
        }

        if (selectedItemPosition == position) {
            holder.linearMonthly.setBackgroundColor(context.resources.getColor(R.color.loginbg))
            holder.textPlaneName.setTextColor(context.getResources().getColor(R.color.white));
            holder.textMonthlyPrice.setTextColor(context.getResources().getColor(R.color.white));
            holder.textMonthlyDesc.setTextColor(context.getResources().getColor(R.color.white));
            holder.textCurrency.setTextColor(context.getResources().getColor(R.color.white));
            holder.txtUnlimitedAccess.setTextColor(context.getResources().getColor(R.color.white));
            holder.txtSaving.setTextColor(context.getResources().getColor(R.color.white));
            planTypeClickListner.onSelectPlan(position, selectedPlanType, true)
            //planTypeClickListner.onSelectPlan(position, planList.plan_type, true)

        } else {
            holder.textPlaneName.setTextColor(context.getResources().getColor(R.color.loginbg));
            holder.textMonthlyPrice.setTextColor(context.getResources().getColor(R.color.loginbg));
            holder.textMonthlyDesc.setTextColor(context.getResources().getColor(R.color.loginbg));
            holder.textCurrency.setTextColor(context.getResources().getColor(R.color.loginbg));
            holder.txtUnlimitedAccess.setTextColor(
                context.getResources().getColor(R.color.loginbg)
            )
            holder.txtSaving.setTextColor(context.getResources().getColor(R.color.loginbg));
            holder.linearMonthly.setBackgroundResource(R.drawable.subcripiton_bg);
        }
    }

    override fun getItemCount(): Int {
        return subscriptionPlanList.size
    }

    open fun setItemSelected(planType:String)
    {
        for(i in 0..subscriptionPlanList.size-1)
        {
            if (planType.equals(subscriptionPlanList[i].plan_type))
            {
                selectedItemPosition = i
                notifyDataSetChanged()
                break
            }
        }

    }
}