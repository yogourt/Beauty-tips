package com.blogspot.android_czy_java.beautytips.viewmodel.recipe

import com.blogspot.android_czy_java.beautytips.appUtils.categories.CategoryInterface
import com.blogspot.android_czy_java.beautytips.database.recipe.RecipeModel

class OneListData(val data: List<RecipeModel>, val listTitle: String, val category: CategoryInterface? = null)