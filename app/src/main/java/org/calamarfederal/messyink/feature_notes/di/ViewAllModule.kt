package org.calamarfederal.messyink.feature_notes.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import org.calamarfederal.messyink.data.MessyInkDb
import org.calamarfederal.messyink.data.ViewAllDao
import org.calamarfederal.messyink.feature_notes.data.NoteDataRepoImpl
import org.calamarfederal.messyink.feature_notes.domain.*
import javax.inject.Named
import kotlin.coroutines.CoroutineContext

@Module
@InstallIn(ViewModelComponent::class)
object ViewAllModule {
    @Provides
    @ViewModelScoped
    fun provideViewAllDao(db: MessyInkDb): ViewAllDao {
        println("provide: MessyInkDao")
        return db.viewAllDoa()
    }

    @Provides
    @ViewModelScoped
    fun provideViewAllRepo(dao: ViewAllDao): NoteDataRepo {
        println("provide: view notes repository")
        return NoteDataRepoImpl(dao = dao)
    }

    @Provides
    @ViewModelScoped
    fun provideGetAllNotesFlow(repo: NoteDataRepo): GetAllNotesFlow = GetAllNotesFlowImpl(repo)

    @Provides
    @ViewModelScoped
    fun provideGetItemsFlow(repo: NoteDataRepo): GetItemsFlow = GetItemsFlowImpl(repo)

    @Provides
    @ViewModelScoped
    fun provideGetNote(repo: NoteDataRepo): GetNote = GetNoteImpl(repo)

    @Provides
    @ViewModelScoped
    fun provideGetItems(repo: NoteDataRepo): GetItems = GetItemsImpl(repo)

    @Provides
    @ViewModelScoped
    fun provideCreateNote(repo: NoteDataRepo): CreateNote = CreateNoteImpl(repo)

    @Provides
    @ViewModelScoped
    fun provideCreateNoteItem(repo: NoteDataRepo): CreateNoteItem = CreateNoteItemImpl(repo)

    @Provides
    @ViewModelScoped
    fun provideReportItemChange(repo: NoteDataRepo): ReportItemChange = ReportItemChangeImpl(repo)

    @Provides
    @ViewModelScoped
    fun provideReportNoteChange(repo: NoteDataRepo): ReportNoteChange = ReportNoteChangeImpl(repo)

    @Provides
    @ViewModelScoped
    fun provideDeleteNote(repo: NoteDataRepo): DeleteNote = DeleteNoteImpl(repo)

    @Provides
    @ViewModelScoped
    fun provideDeleteNotes(repo: NoteDataRepo): DeleteNotes = DeleteNotesImpl(repo)

    @Provides
    @ViewModelScoped
    fun provideDeleteItem(repo: NoteDataRepo): DeleteItem = DeleteItemImpl(repo)

    @Provides
    @ViewModelScoped
    fun provideDeleteItems(repo: NoteDataRepo): DeleteItems = DeleteItemsImpl(repo)

    @Provides
    @Named("timeoutDelay")
    @ViewModelScoped
    fun provideTimeoutDelay(): Long = 5000L

    @Provides
    @Named("notesUseCaseContext")
    @ViewModelScoped
    fun provideUseCaseContext(): CoroutineContext = Dispatchers.IO
}
