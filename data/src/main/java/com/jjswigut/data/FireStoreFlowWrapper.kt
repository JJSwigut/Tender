package com.jjswigut.data


abstract class FireStoreFlowWrapper {

//    suspend inline fun <reified T>getUserGroups(db: FirebaseFirestore, collection: String): Flow<List<T>> =
//        callbackFlow {
//            val snapshot = db.collection(FirestoreRepository.groups)
//
//            val subscription = snapshot.addSnapshotListener { groups, _ ->
//                val userGroups = arrayListOf<T>()
//                for (doc in groups!!.documents) {
//                    doc.toObject<T>()?.let { userGroups.add(it) }
//                }
//                offer(userGroups)
//            }
//            awaitClose { subscription.remove() }
//
//        }
}


