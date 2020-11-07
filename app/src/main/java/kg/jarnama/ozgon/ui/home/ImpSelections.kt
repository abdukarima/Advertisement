package kg.jarnama.ozgon.ui.home

import com.google.firebase.firestore.DocumentSnapshot

interface ImpSelections {
    fun selectCategory(category: DocumentSnapshot)
}