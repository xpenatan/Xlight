package xlight.editor.impl;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class XEmuFileHandle extends FileHandle {

    private FileHandle fileHandle;
    protected XEmuFileHandle(FileHandle fileHandle) {
        this.fileHandle = fileHandle;
    }

    @Override
    public String path() {
        return fileHandle.path();
    }

    @Override
    public String name() {
        return fileHandle.name();
    }

    @Override
    public String extension() {
        return fileHandle.extension();
    }

    @Override
    public String nameWithoutExtension() {
        return fileHandle.nameWithoutExtension();
    }

    @Override
    public String pathWithoutExtension() {
        return fileHandle.pathWithoutExtension();
    }

    @Override
    public Files.FileType type() {
        return fileHandle.type();
    }

    @Override
    public File file() {
        FileHandle absolutePath = isAbsolute();
        if(absolutePath != null) {
            return absolutePath.file();
        }
        return fileHandle.file();
    }

    @Override
    public InputStream read() {
        FileHandle absolutePath = isAbsolute();
        if(absolutePath != null) {
            return absolutePath.read();
        }
        return fileHandle.read();
    }

    @Override
    public BufferedInputStream read(int bufferSize) {
        FileHandle absolutePath = isAbsolute();
        if(absolutePath != null) {
            return absolutePath.read(bufferSize);
        }
        return fileHandle.read(bufferSize);
    }

    @Override
    public Reader reader() {
        FileHandle absolutePath = isAbsolute();
        if(absolutePath != null) {
            return absolutePath.reader();
        }
        return fileHandle.reader();
    }

    @Override
    public Reader reader(String charset) {
        FileHandle absolutePath = isAbsolute();
        if(absolutePath != null) {
            return absolutePath.reader(charset);
        }
        return fileHandle.reader();
    }

    @Override
    public BufferedReader reader(int bufferSize) {
        FileHandle absolutePath = isAbsolute();
        if(absolutePath != null) {
            return absolutePath.reader(bufferSize);
        }
        return fileHandle.reader(bufferSize);
    }

    @Override
    public BufferedReader reader(int bufferSize, String charset) {
        FileHandle absolutePath = isAbsolute();
        if(absolutePath != null) {
            return absolutePath.reader(bufferSize, charset);
        }
        return fileHandle.reader(bufferSize, charset);
    }

    @Override
    public String readString() {
        FileHandle absolutePath = isAbsolute();
        if(absolutePath != null) {
            return absolutePath.readString();
        }
        return fileHandle.readString();
    }

    @Override
    public String readString(String charset) {
        FileHandle absolutePath = isAbsolute();
        if(absolutePath != null) {
            return absolutePath.readString(charset);
        }
        return fileHandle.readString(charset);
    }

    @Override
    public byte[] readBytes() {
        FileHandle absolutePath = isAbsolute();
        if(absolutePath != null) {
            return absolutePath.readBytes();
        }
        return fileHandle.readBytes();
    }

    @Override
    public int readBytes(byte[] bytes, int offset, int size) {
        FileHandle absolutePath = isAbsolute();
        if(absolutePath != null) {
            return absolutePath.readBytes(bytes, offset, size);
        }
        return fileHandle.readBytes(bytes, offset, size);
    }

    @Override
    public ByteBuffer map() {
        FileHandle absolutePath = isAbsolute();
        if(absolutePath != null) {
            return absolutePath.map();
        }
        return fileHandle.map();
    }

    @Override
    public ByteBuffer map(FileChannel.MapMode mode) {
        FileHandle absolutePath = isAbsolute();
        if(absolutePath != null) {
            return absolutePath.map(mode);
        }
        return fileHandle.map(mode);
    }

    @Override
    public OutputStream write(boolean append) {
        FileHandle absolutePath = isAbsolute();
        if(absolutePath != null) {
            return absolutePath.write(append);
        }
        return fileHandle.write(append);
    }

    @Override
    public OutputStream write(boolean append, int bufferSize) {
        FileHandle absolutePath = isAbsolute();
        if(absolutePath != null) {
            return absolutePath.write(append, bufferSize);
        }
        return fileHandle.write(append, bufferSize);
    }

    @Override
    public void write(InputStream input, boolean append) {
        FileHandle absolutePath = isAbsolute();
        if(absolutePath != null) {
            absolutePath.write(input, append);
            return;
        }
        fileHandle.write(input, append);
    }

    @Override
    public Writer writer(boolean append) {
        FileHandle absolutePath = isAbsolute();
        if(absolutePath != null) {
            return absolutePath.writer(append);
        }
        return fileHandle.writer(append);
    }

    @Override
    public Writer writer(boolean append, String charset) {
        FileHandle absolutePath = isAbsolute();
        if(absolutePath != null) {
            return absolutePath.writer(append, charset);
        }
        return fileHandle.writer(append, charset);
    }

    @Override
    public void writeString(String string, boolean append) {
        FileHandle absolutePath = isAbsolute();
        if(absolutePath != null) {
            absolutePath.writeString(string, append);
            return;
        }
        fileHandle.writeString(string, append);
    }

    @Override
    public void writeString(String string, boolean append, String charset) {
        FileHandle absolutePath = isAbsolute();
        if(absolutePath != null) {
            absolutePath.writeString(string, append, charset);
            return;
        }
        fileHandle.writeString(string, append, charset);
    }

    @Override
    public void writeBytes(byte[] bytes, boolean append) {
        FileHandle absolutePath = isAbsolute();
        if(absolutePath != null) {
            absolutePath.writeBytes(bytes, append);
            return;
        }
        fileHandle.writeBytes(bytes, append);
    }

    @Override
    public void writeBytes(byte[] bytes, int offset, int length, boolean append) {
        FileHandle absolutePath = isAbsolute();
        if(absolutePath != null) {
            absolutePath.writeBytes(bytes, offset, length, append);
            return;
        }
        fileHandle.writeBytes(bytes, offset, length, append);
    }

    @Override
    public FileHandle[] list() {
        FileHandle absolutePath = isAbsolute();
        if(absolutePath != null) {
            return absolutePath.list();
        }
        return fileHandle.list();
    }

    @Override
    public FileHandle[] list(FileFilter filter) {
        FileHandle absolutePath = isAbsolute();
        if(absolutePath != null) {
            return absolutePath.list(filter);
        }
        return fileHandle.list(filter);
    }

    @Override
    public FileHandle[] list(FilenameFilter filter) {
        FileHandle absolutePath = isAbsolute();
        if(absolutePath != null) {
            return absolutePath.list(filter);
        }
        return fileHandle.list(filter);
    }

    @Override
    public FileHandle[] list(String suffix) {
        FileHandle absolutePath = isAbsolute();
        if(absolutePath != null) {
            return absolutePath.list(suffix);
        }
        return fileHandle.list(suffix);
    }

    @Override
    public boolean isDirectory() {
        FileHandle absolutePath = isAbsolute();
        if(absolutePath != null) {
            return absolutePath.isDirectory();
        }
        return fileHandle.isDirectory();
    }

    @Override
    public FileHandle child(String name) {
        FileHandle absolutePath = isAbsolute();
        if(absolutePath != null) {
            return absolutePath.child(name);
        }
        return fileHandle.child(name);
    }

    @Override
    public FileHandle sibling(String name) {
        FileHandle absolutePath = isAbsolute();
        if(absolutePath != null) {
            return absolutePath.sibling(name);
        }
        return fileHandle.sibling(name);
    }

    @Override
    public FileHandle parent() {
        FileHandle absolutePath = isAbsolute();
        if(absolutePath != null) {
            return absolutePath.parent();
        }
        return fileHandle.parent();
    }

    @Override
    public void mkdirs() {
        FileHandle absolutePath = isAbsolute();
        if(absolutePath != null) {
            absolutePath.mkdirs();
            return;
        }
        fileHandle.mkdirs();
    }

    @Override
    public boolean exists() {
        FileHandle absolutePath = isAbsolute();
        if(absolutePath != null) {
            return absolutePath.exists();
        }
        return fileHandle.exists();
    }

    @Override
    public boolean delete() {
        FileHandle absolutePath = isAbsolute();
        if(absolutePath != null) {
            return absolutePath.delete();
        }
        return fileHandle.delete();
    }

    @Override
    public boolean deleteDirectory() {
        FileHandle absolutePath = isAbsolute();
        if(absolutePath != null) {
            return absolutePath.deleteDirectory();
        }
        return fileHandle.deleteDirectory();
    }

    @Override
    public void emptyDirectory() {
        FileHandle absolutePath = isAbsolute();
        if(absolutePath != null) {
            absolutePath.emptyDirectory();
            return;
        }
        fileHandle.emptyDirectory();
    }

    @Override
    public void emptyDirectory(boolean preserveTree) {
        FileHandle absolutePath = isAbsolute();
        if(absolutePath != null) {
            absolutePath.emptyDirectory(preserveTree);
            return;
        }
        fileHandle.emptyDirectory(preserveTree);
    }

    @Override
    public void copyTo(FileHandle dest) {
        FileHandle absolutePath = isAbsolute();
        if(absolutePath != null) {
            absolutePath.copyTo(dest);
            return;
        }
        fileHandle.copyTo(dest);
    }

    @Override
    public void moveTo(FileHandle dest) {
        FileHandle absolutePath = isAbsolute();
        if(absolutePath != null) {
            absolutePath.moveTo(dest);
            return;
        }
        fileHandle.moveTo(dest);
    }

    @Override
    public long length() {
        FileHandle absolutePath = isAbsolute();
        if(absolutePath != null) {
            return absolutePath.length();
        }
        return fileHandle.length();
    }

    @Override
    public long lastModified() {
        FileHandle absolutePath = isAbsolute();
        if(absolutePath != null) {
            return absolutePath.lastModified();
        }
        return fileHandle.lastModified();
    }

    @Override
    public boolean equals(Object obj) {
        return fileHandle.equals(obj);
    }

    @Override
    public int hashCode() {
        return fileHandle.hashCode();
    }

    @Override
    public String toString() {
        return fileHandle.toString();
    }

    private FileHandle isAbsolute() {
        if(fileHandle.type() == Files.FileType.Local && Gdx.app.getType() == Application.ApplicationType.Desktop) {
            String path = fileHandle.path();
            FileHandle absolute = Gdx.files.absolute(path);
            boolean isAbsolutePath = false;
            FileHandle cur = absolute.parent();
            while(cur != null) {
                String curPath = cur.path();
                if(curPath.isEmpty() || curPath.equals("/")) {
                    isAbsolutePath = false;
                    break;
                }
                if(cur.exists()) {
                    isAbsolutePath = true;
                    break;
                }
                else {
                    cur = cur.parent();
                }
            }
            if(isAbsolutePath) {
                return absolute;
            }
        }
        return null;
    }
}