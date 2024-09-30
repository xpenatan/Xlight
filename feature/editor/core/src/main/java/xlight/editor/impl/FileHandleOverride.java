package xlight.editor.impl;

import com.badlogic.gdx.files.FileHandle;
import com.github.xpenatan.gdx.multiview.EmuFileHandleOverride;

public class FileHandleOverride implements EmuFileHandleOverride {
    @Override
    public FileHandle getFileHandle(FileHandle fileHandle) {
        return new XEmuFileHandle(fileHandle);
    }
}