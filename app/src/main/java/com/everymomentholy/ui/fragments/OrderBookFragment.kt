package com.everymomentholy.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.response.BookStoreResponseVo
import com.everymomentholy.api.response.BookStoreVo
import com.everymomentholy.ui.adapter.OrderBookAdapter
import com.everymomentholy.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderBookFragment : Fragment() {

    private lateinit var rvOrderBook: RecyclerView
    private var orderBookAdapter: RecyclerView.Adapter<OrderBookAdapter.MyViewHolder>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ordeerbook, container, false)
        rvOrderBook = view.findViewById(R.id.rcv_order_book)
        /* rvOrderBook.layoutManager = LinearLayoutManager(activity)
         rvOrderBook.adapter = OrderBookAdapter()
         adapter = OrderBookAdapter()*/

        if (Utils.isNetworkAvailable(requireContext())) {
            orderBooks()
        } else {
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.check_internet),
                Toast.LENGTH_LONG
            ).show()
        }
        return view
    }

    private fun orderBooks() {
        val request = APIService.buildService(APIInterface::class.java)
        val call = request.getBookStore()

        try {
            call.enqueue(object : Callback<BookStoreResponseVo> {
                override fun onResponse(
                    call: Call<BookStoreResponseVo>,
                    response: Response<BookStoreResponseVo>
                ) {
                    if (response.body()!!.statusCode == 1) {
                        //setAdapter(this@OrderHistoryActivity, response.body()!!)
                        /*if (context != null) {
                            adapter = GetLiturgiesAdapter(
                                context!!,
                                response.body()!!.response.data
                            )
                            viewPager.setPadding(100, 0, 100, 0)
                            viewPager.adapter = adapter;
                        }*/
                        setAdapter(requireContext(), response.body()!!.response)

                    } else {
                        Toast.makeText(
                            requireContext(),
                            response.body()!!.statusCode.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<BookStoreResponseVo>, t: Throwable) {
                    Toast.makeText(requireContext(), "${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    fun setAdapter(context: Context, response: BookStoreVo) {
        orderBookAdapter = OrderBookAdapter(
            context,
            // response.body()!!.response.data,
            response.data
        )
        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(context)
        rvOrderBook.layoutManager = layoutManager
        // attach adapter to the recycler view
        rvOrderBook.adapter = orderBookAdapter
    }
}