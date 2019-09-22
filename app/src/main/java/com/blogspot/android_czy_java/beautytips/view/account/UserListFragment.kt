package com.blogspot.android_czy_java.beautytips.view.account

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.blogspot.android_czy_java.beautytips.database.user.UserModel
import com.blogspot.android_czy_java.beautytips.view.common.RecipeListFragment
import com.blogspot.android_czy_java.beautytips.viewmodel.GenericUiModel
import com.blogspot.android_czy_java.beautytips.viewmodel.account.AccountViewModel
import com.blogspot.android_czy_java.beautytips.viewmodel.account.UserListViewModel
import kotlinx.android.synthetic.main.fragment_nested_list.view.*
import javax.inject.Inject

class UserListFragment : RecipeListFragment() {

    @Inject
    lateinit var viewModel: UserListViewModel

    @Inject
    lateinit var accountViewModel: AccountViewModel

    private var list: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        view?.let {
            list = it.recycler_view
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        accountViewModel.userLiveData.observe(this, Observer { handleUserChange(it) })
    }

    override fun onListClick(listId: Int) {
        viewModel.loadOneList(listId)
    }

    override fun retryDataLoading() {
        viewModel.retry()
    }

    override fun prepareViewModel(init: Boolean) {
        viewModel.recipeListLiveData.observe(this, Observer { this.render(it) })
        if (init) viewModel.init()
    }

    private fun handleUserChange(uiModel: GenericUiModel<UserModel>?) {

        list?.visibility = if (uiModel is GenericUiModel.LoadingSuccess) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
        when (uiModel) {
            is GenericUiModel.LoadingSuccess -> retryDataLoading()
        }
    }

}