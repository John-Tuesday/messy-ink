package org.calamarfederal.messyink.feature_notes.data

import org.calamarfederal.messyink.data.entity.Note
import org.calamarfederal.messyink.data.entity.NoteItem as DTOItem
import org.calamarfederal.messyink.feature_notes.domain.CheckData
import org.calamarfederal.messyink.feature_notes.domain.NoteData
import org.calamarfederal.messyink.feature_notes.domain.NoteItemData
import org.calamarfederal.messyink.data.entity.NoteItem.CheckState as DTOCheck

internal fun DTOCheck.toData(): CheckData = when (this) {
    DTOCheck.None -> CheckData.None
    DTOCheck.Checked -> CheckData.Checked
    DTOCheck.Unchecked -> CheckData.Unchecked
    DTOCheck.Default -> CheckData.Default
    DTOCheck.Partial -> CheckData.Partial
}

internal fun CheckData.toDto(): DTOCheck = when (this) {
    CheckData.None -> DTOCheck.None
    CheckData.Checked -> DTOCheck.Checked
    CheckData.Unchecked -> DTOCheck.Unchecked
    CheckData.Default -> DTOCheck.Default
    CheckData.Partial -> DTOCheck.Partial
}

internal fun Note.toData() = NoteData(title = title, id = id)
internal fun NoteData.toDto() = Note(title = title, id = id)

internal fun DTOItem.toNoteItem() =
    NoteItemData(
        subtitle = subtitle,
        name = name,
        field = field,
        description = description,
        checked = checked.toData(),
        id = id,
        noteId = noteId
    )

internal fun NoteItemData.toDto() = DTOItem(
    subtitle = subtitle,
    name = name,
    field = field,
    description = description,
    checked = checked.toDto(),
    noteId = noteId,
    id = id,
)
