package com.blogspot.android_czy_java.beautytips.usecase.account.userlist

import com.blogspot.android_czy_java.beautytips.repository.forViewModels.recipe.RecipeRepositoryInterface
import com.blogspot.android_czy_java.beautytips.usecase.common.LoadNestedListDataUseCase
import com.blogspot.android_czy_java.beautytips.usecase.common.LoadRecipesUseCase
import com.blogspot.android_czy_java.beautytips.usecase.common.UserListRecipeRequest

class LoadRecipesFromUserListUseCase(loadRecipesUseCase: LoadRecipesUseCase<UserListRecipeRequest>,
                                     recipeRepositoryInterface: RecipeRepositoryInterface<UserListRecipeRequest>) :
        LoadNestedListDataUseCase<UserListRecipeRequest>(loadRecipesUseCase, recipeRepositoryInterface) {

}