package org.sopt.daangnmarket_android.ui.view.write

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import dagger.hilt.android.AndroidEntryPoint
import org.sopt.daangnmarket_android.R
import org.sopt.daangnmarket_android.databinding.ActivityWriteBinding
import org.sopt.daangnmarket_android.ui.viewmodel.WriteViewModel

@AndroidEntryPoint
class WriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWriteBinding
    private val viewModel by viewModels<WriteViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initFragmentContainerView()
    }

    private fun initFragmentContainerView() {
        supportFragmentManager.commit {
            add<WriteFragment>(R.id.fcv_write, WRITE_FRAGMENT)
        }
    }

    companion object {
        const val WRITE_FRAGMENT = "WriteFragment"
    }
}