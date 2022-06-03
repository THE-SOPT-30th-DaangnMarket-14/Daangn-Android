package org.sopt.daangnmarket_android.ui.view.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import org.sopt.daangnmarket_android.databinding.FragmentHomeBinding
import org.sopt.daangnmarket_android.ui.view.data.ItemData
import org.sopt.daangnmarket_android.ui.view.data.ResponseItem
import org.sopt.daangnmarket_android.ui.view.data.ServiceCreator
import org.sopt.daangnmarket_android.ui.view.main.adapter.ItemAdapter
import org.sopt.daangnmarket_android.ui.view.write.WriteActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    private lateinit var itemAdapter: ItemAdapter
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding ?: error("binding not initialized")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        initTransactionEvent()
        itemNetwork()
    }

    private fun initAdapter() {
        itemAdapter = ItemAdapter()
        binding.rvItem.adapter = itemAdapter
    }

    private fun initTransactionEvent(){
        binding.ibFab.setOnClickListener {
            val intent = Intent(context,WriteActivity::class.java)
            startActivity(intent)
        }
    }

    private fun itemNetwork(){
        val call : Call<ResponseItem> = ServiceCreator.itemService.getItems()

        call.enqueue(object : Callback<ResponseItem> {
            override fun onResponse(
                call : Call<ResponseItem>,
                response: Response<ResponseItem>
            ){
                if (response.isSuccessful){
                    val data = response.body()?.data
                    if(data != null){
                        for (i in data){
                            itemAdapter.itemList.add(
                                ItemData(
                                    i.title,
                                    "청파동",
                                    i.price,
                                    i.image,
                                    i.likeCount,
                                    i.chatCount,
                                    i.timeBefore
                                )
                            )
                        }
                        itemAdapter.notifyDataSetChanged()
                    }

                }else{
                    Toast.makeText(context, "판매글을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseItem>, t: Throwable) {
                Log.e("NetworkTest","error:$t")
                Toast.makeText(context, "연결 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}