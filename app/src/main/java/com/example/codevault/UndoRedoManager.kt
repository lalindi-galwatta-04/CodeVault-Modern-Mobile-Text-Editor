package com.example.codevault

class UndoRedoManager {

    private val undoStack =
        mutableListOf<String>()

    private val redoStack =
        mutableListOf<String>()


    fun recordChange(
        previousText: String
    ) {

        if (
            undoStack.isEmpty() ||
            undoStack.last() != previousText
        ) {

            undoStack.add(
                previousText
            )
        }


        redoStack.clear()
    }

    fun canUndo(): Boolean {

        return undoStack.isNotEmpty()
    }

    fun canRedo(): Boolean {

        return redoStack.isNotEmpty()
    }

    fun undo(
        currentText: String
    ): String? {

        if (!canUndo()) {

            return null
        }


        redoStack.add(
            currentText
        )

        return undoStack.removeAt(
            undoStack.lastIndex
        )
    }

    fun redo(
        currentText: String
    ): String? {

        if (!canRedo()) {

            return null
        }


        undoStack.add(
            currentText
        )

        return redoStack.removeAt(
            redoStack.lastIndex
        )
    }


    fun clear() {

        undoStack.clear()
        redoStack.clear()
    }
}