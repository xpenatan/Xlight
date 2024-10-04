package xlight.engine.imgui.ui.filebrowser;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;
import xlight.engine.list.XLinkedList;
import xlight.engine.list.XLinkedListNode;

public class XFile {
    protected String name = "";
    protected boolean isDirectory = false;
    protected boolean isLocked;
    public boolean isSelected;
    public boolean isTreeOpen = false;
    public boolean isRename;
    public FileHandle fileHandle;
    private XFileManager fileManager;

    protected XFile parent;
    public XLinkedList<XFile> files = new XLinkedList<>();

    // if this is a folder and flag is true then it will fetch folder files.
    public boolean updateFiles = true;

    protected XFile(XFileManager fileManager, FileHandle fileHandle) {
        this(fileManager, fileHandle.name(), fileHandle.isDirectory(), false);
        this.fileHandle = fileHandle;
    }

    protected XFile(XFileManager fileManager, String name, boolean isDirectory, boolean isLocked) {
        this.fileManager = fileManager;
        this.isDirectory = isDirectory;
        this.name = name;
        this.isLocked = isLocked;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        if(fileHandle != null && fileManager.treeRootFolder == null) {
            // Root file and contains file handle return its path
            if(fileHandle.type() == Files.FileType.Absolute) {
                return fileHandle.file().getAbsolutePath().replace('\\', '/');
            }
            else  {
                String path = fileHandle.path();

                if(path.equals(".")) {
                    return "/";
                }
                else if(!path.startsWith("/")) {
                    return "/" + path;
                }
                return path;
            }
        }

        String path = "";
        XFile cur = this;
        while(cur != null) {
            String curName = cur.name;
            if(path.isEmpty()) {
                path = curName;
            }
            else {
                path = curName + "/" + path;
            }
            cur = cur.parent;
        }
        return path;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void addChild(XFile file) {
        file.parent = this;
        float order = file.isDirectory() ? 0 : 1;
        files.addOrder(order, file);
    }

    public void removeChild(XFile file) {
        files.remove(file);
        file.parent = null;
    }

    public XFile getChild(FileHandle fileHandle) {
        XLinkedListNode<XFile> cur = files.getHead();
        while(cur != null) {
            XFile file = cur.getValue();
            if(file.fileHandle != null) {
                if(file.fileHandle.equals(fileHandle)) {
                    return file;
                }
            }
            cur = cur.getNext();
        }
        return null;
    }

    public XFile getChild(String name) {
        XLinkedListNode<XFile> cur = files.getHead();
        while(cur != null) {
            XFile file = cur.getValue();
            if(file.getName().equals(name)) {
                return file;
            }
            cur = cur.getNext();
        }
        return null;
    }
}