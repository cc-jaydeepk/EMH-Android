package com.everymomentholy.ui.fragments

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
import com.everymomentholy.api.response.DataVo
import com.everymomentholy.api.response.MyLiturgiesResponseVo
import com.everymomentholy.interfaces.BookListClickListner
import com.everymomentholy.ui.adapter.GetBooksAdapter
import com.everymomentholy.ui.adapter.MyLiturgyAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetBookFragment : Fragment(), BookListClickListner {

    private lateinit var getBookAdapter: GetBooksAdapter
    private lateinit var recycler_liturgy: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.liturgy_raw, container, false)
       // getBookList()
        return view
    }

    /*private fun getBookList() {
        val request = APIService.buildService(APIInterface::class.java)
        val call = request.getBooks()

        try {
            call.enqueue(object : Callback<MyLiturgiesResponseVo> {
                override fun onResponse(
                    call: Call<MyLiturgiesResponseVo>,
                    response: Response<MyLiturgiesResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {

                        getBookAdapter = GetBooksAdapter(
                            context!!,
                            response.body()!!.response.data,
                            this@GetBookFragment
                        )
                        val layoutManager: RecyclerView.LayoutManager =
                            LinearLayoutManager(context)
                        recycler_liturgy.layoutManager = layoutManager
                        // attach adapter to the recycler view
                        recycler_liturgy.adapter = getBookAdapter

                    } else {
                        Toast.makeText(
                            context,
                            response.body()!!.response.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<MyLiturgiesResponseVo>, t: Throwable) {
                    Toast.makeText(context, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }*/

    override fun getBookListClick(pos: Int, dataVo: DataVo) {
        TODO("Not yet implemented")
    }
}