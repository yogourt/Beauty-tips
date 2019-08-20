package com.blogspot.android_czy_java.beautytips.view.listView.view


import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

import com.blogspot.android_czy_java.beautytips.R
import com.blogspot.android_czy_java.beautytips.view.listView.utils.recyclerViewUtils.RecyclerViewHelper
import com.blogspot.android_czy_java.beautytips.view.listView.utils.recyclerViewUtils.SpacesItemDecoration
import com.blogspot.android_czy_java.beautytips.viewmodel.recipe.RecipeViewModel
import com.blogspot.android_czy_java.beautytips.viewmodel.detail.DetailActivityViewModel

import butterknife.BindView
import butterknife.ButterKnife
import com.blogspot.android_czy_java.beautytips.database.recipe.RecipeModel
import com.blogspot.android_czy_java.beautytips.view.AppFragment
import com.blogspot.android_czy_java.beautytips.view.IntentDataKeys
import com.blogspot.android_czy_java.beautytips.view.detail.DetailActivity

import com.blogspot.android_czy_java.beautytips.view.listView.view.RecipeListAdapter.KEY_ITEM
import com.blogspot.android_czy_java.beautytips.view.listView.view.callback.RecipeListAdapterCallback
import com.blogspot.android_czy_java.beautytips.viewmodel.GenericUiModel
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject


class MainActivityFragment : AppFragment(), RecipeListAdapterCallback {


    @BindView(R.id.recycler_view)
    lateinit var list: RecyclerView

    @BindView(R.id.loading_indicator)
    lateinit var loadingIndicator: ProgressBar

    lateinit var layoutManager: StaggeredGridLayoutManager

    lateinit var adapter: RecipeListAdapter

    @Inject
    lateinit var activityViewModel: DetailActivityViewModel

    @Inject
    lateinit var recipeViewModel: RecipeViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main_activity, container, false)

        ButterKnife.bind(this, view)

        return view

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recipeViewModel.recipeLiveData.observe(this, Observer { this.render(it) })
        recipeViewModel.init()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    override fun onResume() {
        super.onResume()
        //open tip from notification
        if (activity != null && activity!!.intent != null) {
            val tipId = activity!!.intent.getStringExtra(KEY_ITEM)
            if (!TextUtils.isEmpty(tipId) && tipId != null) {
                adapter.openDetailScreen(context, tipId.toLong(), null)
                activity!!.intent = null
            }
        }
    }

    private fun prepareRecyclerView(recyclerViewList: List<RecipeModel>) {

        if (loadingIndicator.visibility == View.VISIBLE) {
            loadingIndicator.visibility = View.INVISIBLE
        }

        adapter = RecipeListAdapter(this, recyclerViewList)


        list.adapter = adapter

        val columnNum: Int

        val isTablet = resources.getBoolean(R.bool.is_tablet)
        val orientation = resources.configuration.orientation

        if (!isTablet && orientation == Configuration.ORIENTATION_LANDSCAPE) {
            columnNum = 2
        } else
            columnNum = 1

        //add layout manager
        layoutManager = RecyclerViewHelper.createLayoutManager(columnNum)
        list.layoutManager = layoutManager

        //item decoration is added to make spaces between items in recycler view
        if (list.itemDecorationCount == 0)
            list.addItemDecoration(SpacesItemDecoration(
                    resources.getDimension(R.dimen.list_padding).toInt()))


    }

    private fun render(uiModel: GenericUiModel<List<RecipeModel>>) {
        when (uiModel) {
            is GenericUiModel.LoadingSuccess -> {
                prepareRecyclerView(uiModel.data)
            }
            is GenericUiModel.StatusLoading -> {
                loadingIndicator.visibility = View.VISIBLE
            }
            is GenericUiModel.LoadingError -> {
                showInfoAboutError(uiModel.message)
            }
        }

    }

    override fun onRecipeClick(recipeId: Long) {

        if (isTablet) {
            activityViewModel.chosenItemId = recipeId
        } else {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(IntentDataKeys.KEY_RECIPE_ID, recipeId)
            startActivity(intent)
        }
    }


    private fun showInfoAboutError(message: String) {
        Snackbar.make(
                list,
                getString(R.string.database_error_msg, message),
                Snackbar.LENGTH_INDEFINITE
        ).setAction(
                R.string.retry
        ) { recipeViewModel.retry() }
    }


}