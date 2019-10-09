package com.blogspot.android_czy_java.beautytips.di.view.comment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.blogspot.android_czy_java.beautytips.di.core.ViewModelKey
import com.blogspot.android_czy_java.beautytips.di.usecase.comment.CommentUseCaseModule
import com.blogspot.android_czy_java.beautytips.di.view.detail.DetailActivityModule
import com.blogspot.android_czy_java.beautytips.usecase.comment.LoadCommentsUseCase
import com.blogspot.android_czy_java.beautytips.usecase.detail.LoadHeaderFragmentDataUseCase
import com.blogspot.android_czy_java.beautytips.view.comment.CommentFragment
import com.blogspot.android_czy_java.beautytips.view.detail.DetailActivity
import com.blogspot.android_czy_java.beautytips.viewmodel.comments.CommentsViewModel
import com.blogspot.android_czy_java.beautytips.viewmodel.detail.DetailActivityViewModel
import com.blogspot.android_czy_java.beautytips.viewmodel.detail.HeaderViewModel
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module(includes = [
    CommentFragmentModule.ProvideViewModel::class,
    CommentUseCaseModule::class
])
abstract class CommentFragmentModule {

    @ContributesAndroidInjector(modules = [
        InjectViewModel::class,
        DetailActivityModule::class
    ])
    abstract fun bind(): CommentFragment

    @Module
    class ProvideViewModel {

        @Provides
        @IntoMap
        @ViewModelKey(CommentsViewModel::class)
        fun provideCommentViewModel(loadCommentsUseCase: LoadCommentsUseCase):
                ViewModel = CommentsViewModel(loadCommentsUseCase)

    }

    @Module
    class InjectViewModel {

        @Provides
        fun provideDetailActivityViewModel(
                factory: ViewModelProvider.Factory,
                target: CommentFragment
        ): DetailActivityViewModel =
                ViewModelProviders.of(target.requireActivity(), factory).get(DetailActivityViewModel::class.java)

        @Provides
        fun provideCommentsViewModel(
                factory: ViewModelProvider.Factory,
                target: CommentFragment
        ): CommentsViewModel =
                ViewModelProviders.of(target, factory).get(CommentsViewModel::class.java)

    }
}