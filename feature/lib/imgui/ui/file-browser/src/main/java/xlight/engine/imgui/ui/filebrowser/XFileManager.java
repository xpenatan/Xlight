package xlight.engine.imgui.ui.filebrowser;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import xlight.engine.list.XLinkedList;
import xlight.engine.list.XLinkedListNode;

public class XFileManager {

    private static final String IMAGE_PATH = "images/";

    public XFile treeRootFolder = null;
    public XFile currentFolder = null;
    public final ObjectSet<XFile> selectedFiles;
    public final ObjectSet<XFile> dragFilesOperations;
    private final ObjectSet<XFile> copyFilesOperations;
    private final ObjectSet<XFile> cutFilesOperations;
    public final ObjectMap<String, XFile> files;

    public int thumbnailSize;
    public int thumbnailPadding;

    public int debugThumbnail = 0;

    public XFileManager() {
        selectedFiles = new ObjectSet<>();
        dragFilesOperations = new ObjectSet<>();
        copyFilesOperations = new ObjectSet<>();
        cutFilesOperations = new ObjectSet<>();
        files = new ObjectMap<>();
        thumbnailSize = 90;
        thumbnailPadding = 8;
    }

    public void updatedFolder(XFile folder) {
        if(folder == null) {
            currentFolder = null;
        }
        else if(folder.isDirectory()) {
            selectedFiles.clear();
            currentFolder = folder;
            addFolderItems(currentFolder);
            updateTreeOpenFlag();
        }
    }

    public void updatedRoot(XFile folder) {
        treeRootFolder = folder;

        updateAllFiles(treeRootFolder);
    }

    public String getCurrentFolderPath() {
        if(currentFolder != null) {
            return currentFolder.getPath();
        }
        return "";
    }

    public boolean selectDirectory(String path) {
        if(path == null) {
            return false;
        }
        path = path.trim();
        if(path.isEmpty()) {
            return false;
        }
        if(treeRootFolder == null) {
            FileHandle fileHandle;
            if(Gdx.app.getType() == Application.ApplicationType.WebGL) {
                fileHandle = Gdx.files.local(path);
            }
            else {
                fileHandle = Gdx.files.absolute(path);
            }

            if(fileHandle.exists()) {
                updatedFolder(createFile(fileHandle));
                return true;
            }
        }
        else {
            path = path.replace("\\", "/");
            path = path.replace("//", "/");

            if(path.startsWith("/")) {
                path = path.replaceFirst("/", "");
            }
            if(path.endsWith("/")) {
                path = path.substring(0, path.length()-1);
            }
            String[] pathItems = path.split("/");
            XFile item = null;
            if(treeRootFolder.getName().equals(pathItems[0])) {
                if(pathItems.length > 1) {
                    XFile cur = treeRootFolder;
                    for(int i = 1; i < pathItems.length; i++) {
                        String pathItem = pathItems[i];
                        XFile child = cur.getChild(pathItem);
                        if(child == null) {
                            break;
                        }
                        else {
                            cur = child;
                            if(i + 1 == pathItems.length) {
                                // last item
                                item = child;
                            }
                        }
                    }
                }
                else {
                    item = treeRootFolder;
                }
            }
            if(item != null) {
                updatedFolder(item);
                return true;
            }
        }
        return false;
    }

    public void goToPreviousFolder() {
        if(currentFolder != null) {
            if(currentFolder.parent != null) {
                currentFolder = currentFolder.parent;
            }
            else if(currentFolder.fileHandle != null && treeRootFolder == null) {
                FileHandle parent = currentFolder.fileHandle.parent();
                String path = parent.toString();
                boolean exists = parent.exists();
                if(exists) {
                    // TODO improve attaching child to parent.
                    // This will create a new empty folder that will be updated again with its children
                    updatedFolder(createFile(parent));
                }
            }
        }
    }

    public void deleteSelectedFiles() {
        if(currentFolder != null) {
            ObjectSet.ObjectSetIterator<XFile> iterator = selectedFiles.iterator();
            boolean haveItems = iterator.hasNext;
            while(iterator.hasNext) {
                XFile file = iterator.next();
                if(file.fileHandle != null) {
                    if(file.isDirectory()) {
                        file.fileHandle.deleteDirectory();
                    }
                    else {
                        file.fileHandle.delete();
                    }
                }
            }
            selectedFiles.clear();
            if(haveItems) {
                refreshCurrentFolder();
            }
        }
    }

    public void createNewFolder() {
        if(currentFolder != null) {
            FileHandle fileHandle = currentFolder.fileHandle;
            if(fileHandle != null) {
                String newFolderName = getNewFolderName();
                if(newFolderName != null) {
                    FileHandle childFolder = fileHandle.child(newFolderName);
                    childFolder.mkdirs();
                    refreshCurrentFolder();
                    XFile child = currentFolder.getChild(childFolder);
                    if(child != null) {
                        selectedFiles.add(child);
                        child.isRename = true;
                    }
                }
            }
        }
    }

    public void createNewTxtFile() {
        if(currentFolder != null) {
            FileHandle fileHandle = currentFolder.fileHandle;
            if(fileHandle != null) {
                String newFileName = getTxtFileName();
                if(newFileName != null) {
                    FileHandle childFolder = fileHandle.child(newFileName);
                    childFolder.writeString("", false);
                    refreshCurrentFolder();
                    XFile child = currentFolder.getChild(childFolder);
                    if(child != null) {
                        selectedFiles.add(child);
                        child.isRename = true;
                    }
                }
            }
        }
    }

    public void copySelectedFiles() {
        if(currentFolder != null) {
            copyFilesOperations.clear();
            cutFilesOperations.clear();
            copyFilesOperations.addAll(selectedFiles);
        }
    }

    public void cutSelectedFiles() {
        if(currentFolder != null) {
            copyFilesOperations.clear();
            cutFilesOperations.clear();
            cutFilesOperations.addAll(selectedFiles);
        }
    }

    public void setFileRenameState() {
        if(currentFolder != null) {
            if(selectedFiles.size == 1) {
                selectedFiles.first().isRename = true;
            }
        }
    }

    public String getFilePath() {
        if(currentFolder != null) {
            if(selectedFiles.size == 1) {
                return selectedFiles.first().getPath();
            }
        }
        return null;
    }

    public boolean isCutFile(XFile file) {
        if(currentFolder != null && !cutFilesOperations.isEmpty()) {
            return cutFilesOperations.contains(file);
        }
        return false;
    }

    public int pasteSelectedFiles() {
        if(currentFolder != null && currentFolder.fileHandle != null) {
            if(!copyFilesOperations.isEmpty()) {
                return copySelectedFilesToFolder(copyFilesOperations, currentFolder, false) ? 1 : 0;
            }
            else if(!cutFilesOperations.isEmpty()) {
                return copySelectedFilesToFolder(cutFilesOperations, currentFolder, true) ? 1 : 0;
            }
        }
        return -1;
    }

    public void renameFile(XFile file, String name) {
        FileHandle fileHandle = file.fileHandle;
        if(fileHandle != null) {
            FileHandle newNameFileHandle = fileHandle.sibling(name);
            if(!newNameFileHandle.exists()) {
                fileHandle.moveTo(newNameFileHandle);
                refreshCurrentFolder();
            }
        }
    }

    public boolean dragSelectedFilesToFolder(XFile folder) {
        boolean validFiles = isOperationValid(folder, dragFilesOperations);
        if(validFiles) {
            for(XFile file : dragFilesOperations) {
                if(file.fileHandle != null) {
                    if(file.fileHandle.exists()) {
                        file.fileHandle.moveTo(folder.fileHandle);
                    }
                }
            }
            dragFilesOperations.clear();
            refreshCurrentFolder();
        }
        return validFiles;
    }

    public void refreshCurrentFolder() {
        refreshFolder(currentFolder);
    }

    public void refreshFolder(XFile folder) {
        if(folder != null && folder.isDirectory()) {
            FileHandle fileHandle = folder.fileHandle;
            if(fileHandle != null) {
                folder.updateFiles = true;
                updatedFolder(folder);
            }
        }
    }

    public boolean haveCurOrPaste() {
        return !copyFilesOperations.isEmpty() || !cutFilesOperations.isEmpty();
    }

    private void updateAllFiles(XFile file) {
        addFolderItems(file);

        XLinkedListNode<XFile> cur = file.files.getHead();
        while(cur != null) {
            XFile child = cur.getValue();
            updateAllFiles(child);
            cur = cur.getNext();
        }
    }

    public XFile createFile(FileHandle fileHandle) {
        XFile xFile = new XFile(this, fileHandle);
        return xFile;
    }

    public XFile createFile(String name, boolean isDirectory, boolean isLocked) {
        XFile xFile = new XFile(this, name, isDirectory, isLocked);
        return xFile;
    }

    private boolean copySelectedFilesToFolder(ObjectSet<XFile> files, XFile folder, boolean move) {
        boolean validFiles = isOperationValid(folder, files);
        if(validFiles) {
            for(XFile file : files) {
                if(file.fileHandle != null) {
                    if(file.fileHandle.exists()) {
                        if(move) {
                            XFile parent = file.parent;
                            file.fileHandle.moveTo(folder.fileHandle);
                            if(parent != null) {
                                parent.removeChild(file);
                            }
                        }
                        else {
                            file.fileHandle.copyTo(folder.fileHandle);
                        }
                    }
                }
            }
            if(move) {
                files.clear();
            }
            refreshCurrentFolder();
        }
        return validFiles;
    }

    private String getNewFolderName() {
        if(currentFolder != null) {
            String newFolder = "New folder";
            int count = 2;
            String cur = newFolder;
            while(containsName(cur)) {
                cur = newFolder + "(" + count + ")";
                count++;
            }
            return cur;
        }
        return null;
    }

    private String getTxtFileName() {
        if(currentFolder != null) {
            String text = "text";
            int count = 2;
            String cur = text + ".txt";
            while(containsName(cur)) {
                cur = text + "(" + count + ").txt";
                count++;
            }
            return cur;
        }
        return null;
    }

    private boolean isOperationValid(XFile folder, ObjectSet<XFile> files) {
        if(currentFolder != null && files.size > 0 && folder.isDirectory() && folder.fileHandle != null) {
            if(!files.contains(folder) && folder.fileHandle.exists()) {
                boolean validFiles = isValidFiles(folder, files);
                return validFiles;
            }
        }
        return false;
    }

    private boolean isValidFiles(XFile folder, ObjectSet<XFile> files) {
        String path = folder.fileHandle.path();
        boolean isValid = true;
        for(XFile file : files) {
            if(file.fileHandle != null) {
                if(file.fileHandle.exists()) {
                    String parent = file.fileHandle.parent().path();
                    if(parent.equals(path)) {
                        // cannot paste files in the same folder
                        isValid = false;
                        break;
                    }
                    FileHandle cur = folder.fileHandle.parent();
                    while(!(cur.path().equals("/") || cur.path().isEmpty())) {
                        if(file.fileHandle.equals(cur)) {
                            // File parents cannot contains folder
                            isValid = false;
                            break;
                        }
                        cur = cur.parent();
                    }
                }
            }
        }
        return isValid;
    }

    private boolean containsName(String name) {
        if(currentFolder != null) {
            XLinkedListNode<XFile> cur = currentFolder.files.getHead();
            while(cur != null) {
                XFile file = cur.getValue();
                String folderName = file.getName();
                if(folderName.equals(name)) {
                    return true;
                }
                cur = cur.getNext();
            }
        }
        return false;
    }

    public XLinkedList<XFile> tmpFiles = new XLinkedList<>();

    private void addFolderItems(XFile curFile) {
        // If the current folder don't have items and contains fileHandle then look for its childrens
        if(curFile.isDirectory() && curFile.updateFiles) {
            curFile.updateFiles = false;
            if(curFile.fileHandle != null) {
                FileHandle[] list = curFile.fileHandle.list();
                for(int i = 0; i < list.length; i++) {
                    FileHandle childItems = list[i];
                    XFile child = curFile.getChild(childItems);
                    if(child != null) {
                        tmpFiles.addTail(child);
                    }
                    else {
                        tmpFiles.addTail(createFile(childItems));
                    }
                }
                curFile.files.clear();
                XLinkedListNode<XFile> cur = tmpFiles.getHead();
                while(cur != null) {
                    XFile val = cur.getValue();
                    curFile.addChild(val);
                    cur = cur.getNext();
                }
                tmpFiles.clear();

                XLinkedListNode<XFile> childCur = curFile.files.getHead();
                while(childCur != null) {
                    XFile childFile = childCur.getValue();
                    updateAllFiles(childFile);
                    childCur = childCur.getNext();
                }
            }
        }
    }

    private void updateTreeOpenFlag() {
        currentFolder.isTreeOpen = true;
        XFile cur = currentFolder.parent;
        while(cur != null) {
            cur.isTreeOpen = true;
            cur = cur.parent;
        }
    }
}