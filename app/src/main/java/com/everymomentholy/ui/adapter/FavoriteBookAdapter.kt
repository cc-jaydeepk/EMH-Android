package com.everymomentholy.ui.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.PrivateSharingRequestVo
import com.everymomentholy.api.request.SetFavouriteRequestVo
import com.everymomentholy.api.response.*
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
import com.folioreader.emh.EMHUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class FavoriteBookAdapter(
    var context: Context,
    var favBookLiturgiesList: ArrayList<GetFavoriteBookVo>
) : RecyclerView.Adapter<FavoriteBookAdapter.MyViewHolder>() {

    public var bookOpenPosition = -1
    lateinit var progressDialog: ProgressDialog

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imgFavCover = view.findViewById<ImageView>(R.id.imgFavCover)
        var btnFavReadNow = view.findViewById<Button>(R.id.btn_fav_read_now)
        var imgFavorite = view.findViewById<ImageView>(R.id.img_favorite)
        var imgFavShareImg = view.findViewById<ImageView>(R.id.imgFavShareImg)
        var txtFavLiturgyName = view.findViewById<TextView>(R.id.txtFavLiturgyName)
        var txtFavFree = view.findViewById<TextView>(R.id.txt_fav_free)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.raw_book_favorites, parent, false)
        return FavoriteBookAdapter.MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.txtFavLiturgyName.text = favBookLiturgiesList[position].bookTitle
        Glide.with(context)
            .load(favBookLiturgiesList[position].bookCoverPageImage)
            .into(holder.imgFavCover)

        if (favBookLiturgiesList[position].isFavorite == "True" || favBookLiturgiesList[position].isFavorite == "true") {
            holder.imgFavorite.setImageDrawable(context.resources.getDrawable(R.drawable.ic_favourite_fill))
        } else {
            holder.imgFavorite.setImageDrawable(context.resources.getDrawable(R.drawable.ic_favorite))
        }

        holder.imgFavorite.setOnClickListener() {
            if (Utils.isNetworkAvailable(context)) {
                setLiturgiesFavourite(holder.imgFavorite, favBookLiturgiesList[position], position)
            } else {
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.internet_required),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        holder.imgFavShareImg.setOnClickListener() {
            if (Utils.isNetworkAvailable(context)) {
              //  privateShareLiturgy(favBookLiturgiesList[position])
            } else {
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.internet_required),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun setLiturgiesFavourite(
        ivfav: ImageView,
        liturgiesDataVo: GetFavoriteBookVo,
        position: Int
    ) {

        var setFavouriteRequestVo = SetFavouriteRequestVo()

        setFavouriteRequestVo.userId = Utils.readIntData(context, Constants.PrefUserID, -1)
        setFavouriteRequestVo.isFavorite = liturgiesDataVo.isFavorite != "True"
        setFavouriteRequestVo.bookId = liturgiesDataVo.bookId
        setFavouriteRequestVo.type = "book"

        val request = APIService.buildService(APIInterface::class.java)
        val call =
            request.setFavorite(
                setFavouriteRequestVo,
                "bearer " + Utils.readStringFromSharedPref(context, Constants.SHARED_PREF_TOKEN, "")
            )

        try {
            call.enqueue(object : Callback<BaseResponseVo> {
                @RequiresApi(Build.VERSION_CODES.CUPCAKE)
                override fun onResponse(
                    call: Call<BaseResponseVo>,
                    response: Response<BaseResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {
                        if (liturgiesDataVo.isFavorite == "True") {
                            ivfav.setImageDrawable(context.resources.getDrawable(R.drawable.ic_favorite))
                            Toast.makeText(
                                context,
                                "Removed from favorite",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            ivfav.setImageDrawable(context.resources.getDrawable(R.drawable.ic_favourite_fill))
                            Toast.makeText(
                                context,
                                "Added to favorite",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        updateList(setFavouriteRequestVo.isFavorite, position)
                    } else {
                        /*Toast.makeText(
                            context,
                            response.message().toString(),
                            Toast.LENGTH_LONG
                        ).show()*/

                        Toast.makeText(
                            context,
                            "Error",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<BaseResponseVo>, t: Throwable) {
                    Toast.makeText(context, "${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private fun updateList(favourite: Boolean, position: Int) {
        if (favourite) {
            favBookLiturgiesList[position].isFavorite = "True"
        } else {
            favBookLiturgiesList[position].isFavorite = "False"
            favBookLiturgiesList.remove(favBookLiturgiesList[position])
        }
        notifyDataSetChanged()
    }



    override fun getItemCount(): Int {
        return favBookLiturgiesList.size
    }

    fun updateFavoriteStatusFromBookRead() {
        if (EMHUtils.favoriteFlagChange) {
            if (bookOpenPosition != -1) {
                if (EMHUtils.favoriteStatusChange) {
                    favBookLiturgiesList[bookOpenPosition].isFavorite = "True"
                } else {
                    favBookLiturgiesList[bookOpenPosition].isFavorite = "False"
                    favBookLiturgiesList.removeAt(bookOpenPosition)
                }

            }
            notifyDataSetChanged()
        }
    }
}