package org.jetbrains.kotlinconf

import android.app.*
import kotlinx.coroutines.*
import kotlinx.coroutines.android.*
import org.jetbrains.anko.*
import org.jetbrains.kotlinconf.model.*
import java.util.*

class KotlinConfApplication : Application(), AnkoLogger {
    lateinit var viewModel: KotlinConfViewModel

    override fun onCreate() {
        super.onCreate()

        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            println(throwable)
            throwable.printStackTrace()
            throwable?.cause?.printStackTrace()
        }

        val userId = getUserId()

        viewModel = KotlinConfViewModel(
            this, userId
        ) { action ->
            when (action) {
                KotlinConfViewModel.Error.FAILED_TO_DELETE_RATING -> toast(R.string.msg_failed_to_delete_vote)
                KotlinConfViewModel.Error.FAILED_TO_POST_RATING -> toast(R.string.msg_failed_to_post_vote)
                KotlinConfViewModel.Error.FAILED_TO_GET_DATA -> toast(R.string.msg_failed_to_get_data)
                KotlinConfViewModel.Error.EARLY_TO_VOTE -> toast(R.string.msg_early_vote)
                KotlinConfViewModel.Error.LATE_TO_VOTE -> toast(R.string.msg_late_vote)
                KotlinConfViewModel.Error.FAILED_TO_VERIFY_CODE -> toast(R.string.code_verification_failed)
                KotlinConfViewModel.Error.CODE_INCORRECT -> toast(R.string.code_incorrect)
            }
        }

        launch(UI) {
            viewModel.update()
        }
    }

    private fun getUserId(): String {
        defaultSharedPreferences.getString(USER_ID_KEY, null)?.let { return it }

        val userId = "android-" + UUID.randomUUID().toString()
        defaultSharedPreferences
            .edit()
            .putString(USER_ID_KEY, userId)
            .apply()

        return userId
    }

    companion object {
        const val USER_ID_KEY = "UserId"
    }
}