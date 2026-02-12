package com.luxury.cheats.features.home.service

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.luxury.cheats.features.home.logic.InAppNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Servicio para obtener notificaciones in-app desde Firebase.
 */
import javax.inject.Inject

/**
 * Servicio para obtener notificaciones in-app desde Firebase.
 */
class InAppNotificationService @Inject constructor() {

    private val db = FirebaseDatabase.getInstance().getReference("notifications")

    /**
     * Obtiene la lista de notificaciones activas.
     */
    suspend fun getActiveNotifications(): List<InAppNotification> = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine { cont ->
            db.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val notifications = mutableListOf<InAppNotification>()
                    snapshot.children.forEach { child ->
                        val active = child.child("active").getValue(Boolean::class.java) ?: false
                        if (active) {
                            val notification = InAppNotification(
                                id = child.key ?: "",
                                active = active,
                                title = child.child("title").getValue(String::class.java) ?: "",
                                description = child.child("description").getValue(String::class.java) ?: "",
                                frequency = child.child("frequency").getValue(String::class.java) ?: "always",
                                image = child.child("image").getValue(String::class.java) ?: "",
                                type = child.child("type").getValue(String::class.java) ?: "in-app",
                                timestamp = child.child("timestamp").getValue(String::class.java) ?: "",
                                sent = child.child("sent").getValue(Boolean::class.java) ?: false
                            )
                            notifications.add(notification)
                        }
                    }
                    cont.resume(notifications)
                }

                override fun onCancelled(error: DatabaseError) {
                    cont.resumeWithException(error.toException())
                }
            })
        }
    }
}
