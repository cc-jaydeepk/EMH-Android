package com.everymomentholy.ui.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.response.*
import com.everymomentholy.ui.activity.MainActivity
import com.everymomentholy.ui.adapter.FavoriteAdapter
import com.everymomentholy.ui.adapter.FavoriteBookAdapter
import com.everymomentholy.ui.adapter.MyLiturgyAdapter
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
import com.folioreader.emh.EMHUtils
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoritesFragment : Fragment() {

    private lateinit var favRecyclerView: RecyclerView
    private lateinit var favBookRecyclerView: RecyclerView
    private lateinit var favoriteAdapter: FavoriteAdapter

    // private lateinit var favBookAdapter: FavoriteBookAdapter
    lateinit var progressbar: CardView

    var favLiturgiesList: ArrayList<GetFavoritesDataVo> = ArrayList<GetFavoritesDataVo>()

    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)

        (activity as MainActivity).toolbar.visibility = View.VISIBLE
        (activity as MainActivity).iv_toolbar_backImage.visibility = View.GONE
        (activity as MainActivity).iv_toolbar_search.visibility = View.VISIBLE
        (activity as MainActivity).iv_toolbar_drawer.visibility = View.VISIBLE

        (activity as MainActivity).iv_toolbar_notification.visibility = View.GONE

        favRecyclerView = view.findViewById(R.id.favRecyclerView)
        favBookRecyclerView = view.findViewById(R.id.favBookRecyclerView)
        favRecyclerView.layoutManager = LinearLayoutManager(activity)
        favBookRecyclerView.layoutManager = LinearLayoutManager(activity)

        progressbar = view.findViewById(R.id.progressCardView)



        //favLiturgiesList = ArrayList<GetFavoritesDataVo>()

        if (Utils.isNetworkAvailable(requireContext())) {
//            getFavoriteLiturgiesBookList()
            progressbar.visibility = View.VISIBLE
            getFavoriteLiturgiesList()
        } else {
            setUpOfflineView()
            /*Toast.makeText(
                requireContext(),
                resources.getString(R.string.check_internet),
                Toast.LENGTH_LONG
            ).show()*/
        }
        return view
    }

    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    @SuppressLint("HardwareIds")
    private fun getFavoriteLiturgiesBookList() {
        val request = APIService.buildService(APIInterface::class.java)
        val call = request.getFavoriteBookList(
            Utils.readIntFromSharedPref(requireContext(), Constants.PrefUserID, -1),
            "bearer " + Utils.readStringFromSharedPref(
                requireContext(),
                Constants.SHARED_PREF_TOKEN,
                ""
            )
        )

        try {
            call.enqueue(object : Callback<GetFavoriteBookList> {
                override fun onResponse(
                    call: Call<GetFavoriteBookList>,
                    response: Response<GetFavoriteBookList>
                ) {
                    if (response.body()?.statusCode == 1) {
                        if (context != null) {
                            //setAdapter(response.body()!!.response.data)
                            for (i in 0..response.body()!!.response.data.size - 1) {
                                var temp = GetFavoritesDataVo()
                                temp.bookId = response.body()!!.response.data[i].bookId
                                temp.chapterTitle = response.body()!!.response.data[i].bookTitle
                                temp.volumeTags = response.body()!!.response.data[i].volumeTags
                                temp.chapterPageImage =
                                    response.body()!!.response.data[i].bookCoverPageImage

                                temp.chapterId = 0
                                temp.isFavorite = response.body()!!.response.data[i].isFavorite

                                if (!isExist(temp.bookId)) {
                                    favLiturgiesList.add(temp)
                                }

                                if (i == response.body()!!.response.data.size - 1) {
//                                    getFavoriteLiturgiesList()
                                    setAdapter(favLiturgiesList)
                                }
                            }
                            // favLiturgiesList.addAll(response.body()!!.response.data)
                            //setBookAdapter(response.body()!!.response.data)
                            //setBookAdapter(favLiturgiesList)
                            favoriteAdapter.notifyDataSetChanged()

                        }
                    } else {
                        if (context != null) {
                            Toast.makeText(
                                requireContext(),
                                response.body()!!.message.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                            setAdapter(favLiturgiesList)
                        }
                    }
                }

                override fun onFailure(call: Call<GetFavoriteBookList>, t: Throwable) {

                    if (context != null) {
                        Toast.makeText(requireContext(), "${t.message}", Toast.LENGTH_SHORT)
                            .show()
                    }

                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    fun isExist(bookId: Int): Boolean {
        for (item in favLiturgiesList) {
            if (item.bookId == bookId) {
                return true
            }
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    @SuppressLint("HardwareIds")
    private fun getFavoriteLiturgiesList() {
        val request = APIService.buildService(APIInterface::class.java)
        val call = request.getFavoriteList(
            Utils.readIntFromSharedPref(requireContext(), Constants.PrefUserID, -1),
            "bearer " + Utils.readStringFromSharedPref(
                requireContext(),
                Constants.SHARED_PREF_TOKEN,
                ""
            )
        )

        try {
            call.enqueue(object : Callback<GetFavoritesResponseVo> {
                override fun onResponse(
                    call: Call<GetFavoritesResponseVo>,
                    response: Response<GetFavoritesResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {
                        progressbar.visibility = View.GONE
                        if (context != null) {
                            favLiturgiesList.clear()
                            favLiturgiesList.addAll(response.body()!!.response.data)
                            getFavoriteLiturgiesBookList()
//                            setAdapter(favLiturgiesList)
//                            setAdapter(response.body()!!.response.data)
                        }
                    } else {
                        if (context != null) {
                            progressbar.visibility = View.GONE
                            Toast.makeText(
                                requireContext(),
                                response.body()!!.message.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                            getFavoriteLiturgiesBookList()
                        }
                    }
                }

                override fun onFailure(call: Call<GetFavoritesResponseVo>, t: Throwable) {

                    if (context != null) {
                        Toast.makeText(requireContext(), "${t.message}", Toast.LENGTH_SHORT)
                            .show()
                    }

                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    fun setAdapter(favLiturgiesData: ArrayList<GetFavoritesDataVo>) {
        favoriteAdapter = FavoriteAdapter(requireContext(), favLiturgiesData)
        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(context)
        favRecyclerView.layoutManager = layoutManager
        favRecyclerView.adapter = favoriteAdapter
    }

    /* fun setBookAdapter(favBookData: ArrayList<GetFavoriteBookVo>) {
         favBookAdapter = FavoriteBookAdapter(requireContext(), favBookData)
         val layoutManager: RecyclerView.LayoutManager =
             LinearLayoutManager(context)
         favBookRecyclerView.layoutManager = layoutManager
         favBookRecyclerView.adapter = favBookAdapter
     }*/

    /*fun setBookAdapter(favBookData: ArrayList<GetFavoritesDataVo>) {
        favoriteAdapter = FavoriteAdapter(requireContext(), favLiturgiesData)
        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(context)
        favRecyclerView.layoutManager = layoutManager
        favRecyclerView.adapter = favoriteAdapter
    }*/

    override fun onResume() {
        super.onResume()
//        favLiturgiesList.clear()
//        getFavoriteLiturgiesList()
        if (EMHUtils.favoriteFlagChange) {
            try {
                if (favoriteAdapter.bookOpenPosition != -1) {
                    favoriteAdapter.updateFavoriteStatusFromBookRead()
                    favoriteAdapter.notifyDataSetChanged()

                    // favBookAdapter.updateFavoriteStatusFromBookRead()
                    // favBookAdapter.notifyDataSetChanged()
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setUpOfflineView() {
        val favoritesJsonString =
            Utils.readJsonFromFile(requireContext(), Constants.FAVORITES_FILE_NAME)

        if (!favoritesJsonString.isNullOrEmpty()) {

            val response: FavoriteResponseVo =
                Gson().fromJson(favoritesJsonString, FavoriteResponseVo::class.java)

            if (context != null) {
                setAdapter(response.data)
            }
        }
    }

}