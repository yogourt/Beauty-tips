package com.blogspot.android_czy_java.beautytips.view.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.blogspot.android_czy_java.beautytips.view.common.NestedRecipeListFragment
import com.blogspot.android_czy_java.beautytips.viewmodel.account.UserListViewModel
import com.blogspot.android_czy_java.beautytips.viewmodel.recipe.MainListData
import javax.inject.Inject

class UserListFragment: NestedRecipeListFragment() {

    @Inject
    lateinit var viewModel: UserListViewModel

    override fun retryDataLoading() {
        viewModel.retry()
    }

    override fun prepareViewModel() {
        viewModel.recipeListLiveData.observe(this, Observer { this.render(it) })
        viewModel.init()
    }

}