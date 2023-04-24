package org.calamarfederal.messyink.feature_notes.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.calamarfederal.messyink.feature_notes.domain.*
import org.calamarfederal.messyink.feature_notes.presentation.state.UiNoteBrief
import org.calamarfederal.messyink.feature_notes.presentation.state.UiNoteItem
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.CoroutineContext


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NotesViewEditModel @Inject constructor(
    @Named("timeoutDelay") private val timeoutDelay: Long,
    @Named("notesUseCaseContext") private val useCaseContext: CoroutineContext,
    private val savedStateHandle: SavedStateHandle,
    private val getAllNotesFlow: GetAllNotesFlow,
    private val getNote: GetNote,
    private val getItems: GetItems,
    private val getItemsFlow: GetItemsFlow,
    private val createNote: CreateNote,
    private val createNoteItem: CreateNoteItem,
    private val reportItemChange: ReportItemChange,
    private val reportNoteChange: ReportNoteChange,
    private val deleteNote: DeleteNote,
    private val deleteNotes: DeleteNotes,
    private val deleteItem: DeleteItem,
    private val deleteItems: DeleteItems,
) : ViewModel() {
    companion object {
        private const val NOTE_ID: String = "noteId"
    }

    private val useCaseScope: CoroutineScope = viewModelScope + useCaseContext
    private fun <T> Flow<T>.stateInUseCases(initial: T): StateFlow<T> =
        stateIn(useCaseScope, SharingStarted.WhileSubscribed(timeoutDelay), initialValue = initial)

    private fun <T> Flow<T>.stateInViewModel(initial: T): StateFlow<T> =
        stateIn(useCaseScope, SharingStarted.WhileSubscribed(timeoutDelay), initialValue = initial)

    val allNotes = getAllNotesFlow().stateInUseCases(listOf())

    private val _selectedNoteBriefs = MutableStateFlow(setOf<UiNoteBrief>())
    val selectedNoteBriefs = _selectedNoteBriefs.asStateFlow()

    private val _selectedNoteItems = MutableStateFlow(setOf<UiNoteItem>())
    val selectedNoteItems = _selectedNoteItems.asStateFlow()

    val isSelectMode = combine(
        _selectedNoteBriefs,
        selectedNoteItems
    ) { note, items -> note.isNotEmpty() || items.isNotEmpty() }.stateInViewModel(false)

    private val _pickedNoteBrief = MutableStateFlow<UiNoteBrief?>(null)
    val pickedNoteBrief = _pickedNoteBrief.asStateFlow()

    private val _pickedNoteItems = MutableStateFlow<List<UiNoteItem>>(listOf())
    val pickedNoteItems = _pickedNoteItems.asStateFlow()

    init {
        _pickedNoteBrief.filterNotNull().onEach { note ->
            reportNoteChange(note)
        }.launchIn(useCaseScope)

        _pickedNoteBrief.distinctUntilChanged { old, new -> old?.id == new?.id }.mapLatest { note ->
            val items = note?.let { getItems(it.id) } ?: listOf()
            _pickedNoteItems.update { items }
        }.launchIn(useCaseScope)

        _pickedNoteBrief.distinctUntilChanged { old, new -> old?.id == new?.id }.onEach { note ->
            savedStateHandle[NOTE_ID] = note?.id
        }.launchIn(viewModelScope)
    }

    private fun onDeleteNote(note: UiNoteBrief) {
        _pickedNoteBrief.compareAndSet(note, null)
        _selectedNoteBriefs.update {
            if (it.contains(note)) it.toMutableSet().apply { remove(note) } else it
        }
        useCaseScope.launch { deleteNote(note) }
    }

    private fun deleteNotesFromSelection() {
        val notes = _selectedNoteBriefs.getAndUpdate { setOf() }
        _pickedNoteBrief.update { if (notes.contains(it)) null else it }
        useCaseScope.launch { deleteNotes(notes) }
    }

    private fun onDeleteItem(item: UiNoteItem) {
        _pickedNoteItems.update {
            if (it.contains(item)) it.toMutableList().apply { remove(item) } else it
        }
        _selectedNoteItems.update {
            if (it.contains(item)) it.toMutableSet().apply { remove(item) } else it
        }
        useCaseScope.launch { deleteItem(item) }
    }

    private fun deleteItemsFromSelection() {
        val items = _selectedNoteItems.getAndUpdate { setOf() }
        _pickedNoteItems.update { it.toMutableList().apply { removeAll(items) } }
        useCaseScope.launch { deleteItems(items) }
    }

    /**
     * # Response to User
     */

    fun loadNoteFromId(
        noteId: Long,
        onNotFound: () -> Unit = { throw (Throwable("Could note find target note with id: $noteId")) },
    ) {
        useCaseScope.launch {
            val note = getNote(noteId)
            _pickedNoteBrief.update { note }
            if (note == null) onNotFound()
        }
    }

    fun loadOrCreateNoteFromId(noteId: Long?) {
        useCaseScope.launch {
            _pickedNoteBrief.value = noteId?.let { getNote(it) } ?: createNote()
        }
    }

    fun createAndLoadNote() {
        useCaseScope.launch {
            val note = createNote()
            createNoteItem(note.id)
            _pickedNoteBrief.update { note.copy(title = "untitled") }
        }
    }

    fun createAddNoteItem() {
        useCaseScope.launch {
            val noteId = _pickedNoteBrief.value?.id
                ?: throw (Throwable("Cannot create an item without a parent"))
            val item = createNoteItem(noteId)
            _pickedNoteItems.update { it.toMutableList().apply { add(item) } }
        }
    }

    fun selectNote(note: UiNoteBrief) {
        _selectedNoteBriefs.update { it.toMutableSet().apply { add(note) } }
    }

    fun selectNotes(note: List<UiNoteBrief>) {
        _selectedNoteBriefs.update { it.toMutableSet().apply { addAll(note) } }
    }

    fun selectItem(item: UiNoteItem) {
        _selectedNoteItems.update { it.toMutableSet().apply { add(item) } }
    }

    fun selectItems(items: List<UiNoteItem>) {
        _selectedNoteItems.update { it.toMutableSet().apply { addAll(items) } }
    }

    fun deselectNote(note: UiNoteBrief) {
        _selectedNoteBriefs.update { it.toMutableSet().apply { remove(note) } }
    }

    fun deselectNotes(note: List<UiNoteBrief>) {
        _selectedNoteBriefs.update { it.toMutableSet().apply { removeAll(note.toSet()) } }
    }

    fun deselectItem(item: UiNoteItem) {
        _selectedNoteItems.update { it.toMutableSet().apply { remove(item) } }
    }

    fun deselectItems(items: List<UiNoteItem>) {
        _selectedNoteItems.update { it.toMutableSet().apply { removeAll(items.toSet()) } }
    }

    fun deselectAll() {
        _selectedNoteItems.update { setOf() }
        _selectedNoteBriefs.update { setOf() }
    }

    fun updateTargetedNote(note: UiNoteBrief) {
        _pickedNoteBrief.value = note
    }

    fun updateTargetedNoteItem(index: Int, item: UiNoteItem) {
        _pickedNoteItems.update { it.toMutableList().apply { set(index, item) } }
        useCaseScope.launch { reportItemChange(item) }
    }

    fun deleteTargetNote(note: UiNoteBrief) {
        onDeleteNote(note)
    }

    fun deleteTargetNoteItem(index: Int, item: UiNoteItem) {
        onDeleteItem(item)
    }

    fun deleteSelectedItems() {
        deleteItemsFromSelection()
    }

    fun deleteSelectedNotes() {
        deleteNotesFromSelection()
    }

}
