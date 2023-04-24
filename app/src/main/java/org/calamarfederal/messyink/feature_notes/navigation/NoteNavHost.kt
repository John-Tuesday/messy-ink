package org.calamarfederal.messyink.feature_notes.navigation

import org.calamarfederal.messyink.common.navigation.SubNavOwner

object NoteNavHost : SubNavOwner<NoteNavNode> {
    override val rootRoute: String = "note_root"
    override val defaultStart: NoteNavNode = ViewAllNode
}
