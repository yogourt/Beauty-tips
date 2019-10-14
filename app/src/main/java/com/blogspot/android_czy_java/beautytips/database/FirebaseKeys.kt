package com.blogspot.android_czy_java.beautytips.database

interface FirebaseKeys {

    companion object {
        const val KEY_RECIPE_TITLE = "title"
        const val KEY_RECIPE_IMAGE = "image"
        const val KEY_RECIPE_AUTHOR_ID = "authorId"
        const val KEY_RECIPE_CATEGORY = "category"
        const val KEY_RECIPE_SUBCATEGORY = "subcategory"
        const val KEY_RECIPE_FAV_NUM = "favNum"
        const val KEY_RECIPE_TAGS = "tags"

        const val KEY_RECIPE_DESCRIPTION = "description"
        const val KEY_RECIPE_INGREDIENTS = "ingredients"
        const val KEY_RECIPE_SOURCE = "source"

        const val KEY_COMMENT_MESSAGE = "c"
        const val KEY_COMMENT_AUTHOR = "a"
        const val KEY_COMMENT_AUTHOR_ID = "b"
        const val KEY_COMMENT_RESPONSE_TO = "response_to"
    }
}