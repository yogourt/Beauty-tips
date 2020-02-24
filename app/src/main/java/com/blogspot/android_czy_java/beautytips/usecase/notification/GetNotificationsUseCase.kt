package com.blogspot.android_czy_java.beautytips.usecase.notification

import com.blogspot.android_czy_java.beautytips.database.notification.NotificationModel
import com.blogspot.android_czy_java.beautytips.database.user.UserModel
import com.blogspot.android_czy_java.beautytips.exception.account.UserNotFoundException
import com.blogspot.android_czy_java.beautytips.repository.forViewModels.notification.NotificationRepository
import com.blogspot.android_czy_java.beautytips.service.notification.NotificationKeys
import com.blogspot.android_czy_java.beautytips.usecase.account.GetCurrentUserUseCase
import com.blogspot.android_czy_java.beautytips.usecase.account.GetUserFromFirebaseUseCase
import io.reactivex.Observable
import io.reactivex.Single

class GetNotificationsUseCase(private val notificationRepository: NotificationRepository,
                              private val currentUserUseCase: GetCurrentUserUseCase,
                              private val getUserFromFirebaseUseCase: GetUserFromFirebaseUseCase) {

    fun execute(): Observable<List<NotificationModel>> {
        return Observable.create { emitter ->

            currentUserUseCase.currentUserId()?.let { userId ->
                val notifications =
                        notificationRepository.getNotificationsForUser(userId)

                emitter.onNext(notifications)
                val singleToZip = createUserRequests(notifications)

                if (singleToZip == null) {
                    emitter.onComplete()
                }

                Single.zip(singleToZip) { results ->
                    for (result in results) {
                        if (result is UserModel) {
                            notifications.map {
                                if (it.authorId == result.id) {
                                    createMessageToDisplay(it, result)
                                }
                            }
                        }
                    }
                    notifications
                }.subscribe { newList ->
                    emitter.onNext(newList)
                    emitter.onComplete()
                }

            } ?: emitter.onError(UserNotFoundException())

        }
    }


    private fun createUserRequests(notifications: List<NotificationModel>): List<Single<UserModel>>? {
        val singleToZip = mutableListOf<Single<UserModel>>()

        val alreadyAddedIds = mutableListOf<String>()

        for (notification in notifications) {
            notification.authorId?.let {
                if (!alreadyAddedIds.contains(it))
                    singleToZip.add(getUserFromFirebaseUseCase.execute(it))
                alreadyAddedIds.add(it)
            }
        }
        return if (singleToZip.isNotEmpty()) {
            singleToZip
        } else {
            null
        }
    }


    private fun createMessageToDisplay(it: NotificationModel, result: UserModel) {
        if (it.type == NotificationKeys.NOTIFICATION_TYPE_COMMENT_ON_USER_RECIPE) {
            it.message = "${result.nickname} commented your recipe: ${it.message}"
        } else if (it.type == NotificationKeys.NOTIFICATION_TYPE_SUBCOMMENT_ON_USER_COMMENT) {
            it.message = "${result.nickname} commented your comment: ${it.message}"
        }
    }
}