package org.sopt.daangnmarket_android.ui.view.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import org.sopt.daangnmarket_android.R
import org.sopt.daangnmarket_android.databinding.ActivityMainBinding

// 다희
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAdapter()
        initBottomNavi()

    }

    private fun initAdapter(){
        val fragmentList = listOf(HomeFragment(), TestFragment1(), TestFragment2(),TestFragment3(),TestFragment4())
        viewPagerAdapter = ViewPagerAdapter(this)
        viewPagerAdapter.fragments.addAll(fragmentList)

        binding.vpMain.adapter = viewPagerAdapter
    }

    private fun initBottomNavi(){
        binding.bnvMain.itemIconTintList = null
        binding.vpMain.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                binding.bnvMain.menu.getItem(position).isChecked = true
            }
        })

        binding.bnvMain.setOnItemSelectedListener {
            when(it.itemId){
                R.id.menu_home -> {
                    binding.vpMain.currentItem = HOME_FRAGMENT
                    true
                }
                R.id.menu_viliage -> {
                    binding.vpMain.currentItem = FIRST_FRAGMENT
                    true
                }
                R.id.menu_gps -> {
                    binding.vpMain.currentItem = SECOND_FRAGMENT
                    true
                }
                R.id.menu_talk -> {
                    binding.vpMain.currentItem = THIRD_FRAGMENT
                    true
                }
                else -> {
                    binding.vpMain.currentItem = FOURTH_FRAGMENT
                    true
                }
            }
        }
    }

    companion object {
        const val HOME_FRAGMENT = 0
        const val FIRST_FRAGMENT = 1
        const val SECOND_FRAGMENT = 2
        const val THIRD_FRAGMENT = 3
        const val FOURTH_FRAGMENT = 4
    }
}
