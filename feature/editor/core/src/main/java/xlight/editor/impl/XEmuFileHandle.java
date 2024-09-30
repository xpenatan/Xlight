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
        if(fileHandle.type() == Files.FileType.Local) {
            String path = fileHandle.file().getPath();
            FileHandle absolute = Gdx.files.absolute(path);
            return absolute.file();
        }
        return fileHandle.file();
    }

    @Override
    public InputStream read() {
        return fileHandle.read();
    }

    @Override
    public BufferedInputStream read(int bufferSize) {
        return fileHandle.read(bufferSize);
    }

    @Override
    public Reader reader() {
        return fileHandle.reader();
    }

    @Override
    public Reader reader(String charset) {
        return fileHandle.reader();
    }

    @Override
    public BufferedReader reader(int bufferSize) {
        return fileHandle.reader(bufferSize);
    }

    @Override
    public BufferedReader reader(int bufferSize, String charset) {
        return fileHandle.reader(bufferSize, charset);
    }

    @Override
    public String readString() {
        return fileHandle.readString();
    }

    @Override
    public String readString(String charset) {
        return fileHandle.readString(charset);
    }

    @Override
    public byte[] readBytes() {
        return fileHandle.readBytes();
    }

    @Override
    public int readBytes(byte[] bytes, int offset, int size) {
        return fileHandle.readBytes(bytes, offset, size);
    }

    @Override
    public ByteBuffer map() {
        return fileHandle.map();
    }

    @Override
    public ByteBuffer map(FileChannel.MapMode mode) {
        return fileHandle.map(mode);
    }

    @Override
    public OutputStream write(boolean append) {
        return fileHandle.write(append);
    }

    @Override
    public OutputStream write(boolean append, int bufferSize) {
        return fileHandle.write(append, bufferSize);
    }

    @Override
    public void write(InputStream input, boolean append) {
        fileHandle.write(input, append);
    }

    @Override
    public Writer writer(boolean append) {
        return fileHandle.writer(append);
    }

    @Override
    public Writer writer(boolean append, String charset) {
        return fileHandle.writer(append, charset);
    }

    @Override
    public void writeString(String string, boolean append) {
        try {
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
                    absolute.writeString(string, append);
                    return;
                }
            }
            fileHandle.writeString(string, append);
        }
         catch(Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void writeString(String string, boolean append, String charset) {
        fileHandle.writeString(string, append, charset);
    }

    @Override
    public void writeBytes(byte[] bytes, boolean append) {
        fileHandle.writeBytes(bytes, append);
    }

    @Override
    public void writeBytes(byte[] bytes, int offset, int length, boolean append) {
        fileHandle.writeBytes(bytes, offset, length, append);
    }

    @Override
    public FileHandle[] list() {
        return fileHandle.list();
    }

    @Override
    public FileHandle[] list(FileFilter filter) {
        return fileHandle.list(filter);
    }

    @Override
    public FileHandle[] list(FilenameFilter filter) {
        return fileHandle.list(filter);
    }

    @Override
    public FileHandle[] list(String suffix) {
        return fileHandle.list(suffix);
    }

    @Override
    public boolean isDirectory() {
        return fileHandle.isDirectory();
    }

    @Override
    public FileHandle child(String name) {
        return fileHandle.child(name);
    }

    @Override
    public FileHandle sibling(String name) {
        return fileHandle.sibling(name);
    }

    @Override
    public FileHandle parent() {
        return fileHandle.parent();
    }

    @Override
    public void mkdirs() {
        fileHandle.mkdirs();
    }

    @Override
    public boolean exists() {
        return fileHandle.exists();
    }

    @Override
    public boolean delete() {
        return fileHandle.delete();
    }

    @Override
    public boolean deleteDirectory() {
        return fileHandle.deleteDirectory();
    }

    @Override
    public void emptyDirectory() {
        fileHandle.emptyDirectory();
    }

    @Override
    public void emptyDirectory(boolean preserveTree) {
        fileHandle.emptyDirectory(preserveTree);
    }

    @Override
    public void copyTo(FileHandle dest) {
        fileHandle.copyTo(dest);
    }

    @Override
    public void moveTo(FileHandle dest) {
        fileHandle.moveTo(dest);
    }

    @Override
    public long length() {
        return fileHandle.length();
    }

    @Override
    public long lastModified() {
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
}