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
        setContentView(R.layout.activity_write)
        //버튼을 눌러도 아무일이 일어나지 않고 있습니다......... 혹시 제가 바보같은 짓을 했나요?

        binding.btnFinish.setOnClickListener {
            if (binding.etTitle.text.isNullOrBlank() || binding.etPrice.text.isNullOrBlank() || binding.etContent.text.isNullOrBlank()) {
                Toast.makeText(this, "채워지지 않은 부분이 있습니다", Toast.LENGTH_SHORT).show()
                //이렇게 쓴게 맞나요?
            } else {
                //완료버튼 주황색으로 변경해야하는데... ㅠㅠ .. 함수를 뭘 써야할지 모르겠음...
                finish()
            }
        }
        binding.btnBack.setOnClickListener() {
                startActivity(Intent(this@WriteActivity, MainActivity::class.java))
            //이게 맞나요
        }
    }
}