package org.sopt.daangnmarket_android.data.repository

import org.sopt.daangnmarket_android.data.service.DaangnService
import org.sopt.daangnmarket_android.domain.repository.DaangnRepository
import java.io.File
import javax.inject.Inject

class DaangnRepositoryImpl @Inject constructor(
    private val service: DaangnService
): DaangnRepository {

}