package com.blogspot.android_czy_java.beautytips.database.detail

import android.database.Observable
import androidx.room.Dao
import androidx.room.Query
import com.blogspot.android_czy_java.beautytips.database.comment.CommentModel

@Dao
interface DetailDao {

    @Query("SELECT title FROM Recipes WHERE recipeId = :recipeId")
    fun getTitle(recipeId: Long): String

    @Query("SELECT imageUrl FROM Recipes WHERE recipeId = :recipeId")
    fun getImageUrl(recipeId: Long): String

    @Query("SELECT description FROM Recipes WHERE recipeId = :recipeId")
    fun getDescription(recipeId: Long): String

    @Query("SELECT ingredients FROM Recipes WHERE recipeId = :recipeId")
    fun getIngredients(recipeId: Long): String

    @Query("SELECT optionalIngredients FROM Recipes WHERE recipeId = :recipeId")
    fun getOptionalIngredients(recipeId: Long): String

    @Query("SELECT source FROM Recipes WHERE recipeId = :recipeId")
    fun getSource(recipeId: Long): String

    @Query("SELECT * FROM RecipeComments WHERE recipeId = :recipeId")
    fun getComments(recipeId: Long): List<CommentModel>

    @Query("SELECT COUNT(id) FROM RecipeComments WHERE recipeId = :recipeId")
    fun getCommentNumberForRecipe(recipeId: Long): Int

}