package org.calamarfederal.messyink.feature_counter.presentation.create_counter

/**
 * Test tags, not used in release build, helps quickly find key nodes in testing
 */
object CreateCounterTestTags {
    /**
     * Button which closes the dialog and navigates up
     */
    const val CloseButton = "close-button"

    /**
     * Button which finalizes and saves the Counter then navigates away
     *
     * should only be enabled when the submission is valid
     */
    const val SubmitButton = "submit-button"

    /**
     * Text field for the Counter title
     */
    const val TitleTextField = "title-field"
}
