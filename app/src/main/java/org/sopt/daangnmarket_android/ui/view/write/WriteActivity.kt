package org.sopt.daangnmarket_android.ui.view.write

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import org.sopt.daangnmarket_android.R
import org.sopt.daangnmarket_android.databinding.ActivityWriteBinding
import org.sopt.daangnmarket_android.ui.view.main.MainActivity

class WriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWriteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnFinish.setOnClickListener {
            if (binding.etTitle.text.isNullOrBlank() || binding.etPrice.text.isNullOrBlank() || binding.etContent.text.isNullOrBlank()) {
                Toast.makeText(this, "채워지지 않은 부분이 있습니다", Toast.LENGTH_SHORT).show()
            } else {
                startActivity(Intent(this@WriteActivity, MainActivity::class.java))
                //우선은 버튼 변화 없이 MainActivity로 연결해두었습니다.
            }
        }
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}