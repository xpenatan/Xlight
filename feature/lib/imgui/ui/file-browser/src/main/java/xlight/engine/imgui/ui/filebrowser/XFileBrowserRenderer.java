package xlight.engine.imgui.ui.filebrowser;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import imgui.ImGui;
import imgui.ImGuiContext;
import imgui.ImGuiInputTextFlags;
import imgui.ImGuiInternal;
import imgui.ImGuiKey;
import imgui.ImGuiPayload;
import imgui.ImGuiString;
import imgui.ImGuiStyle;
import imgui.ImGuiWindow;
import imgui.ImGuiWindowFlags;
import imgui.ImRect;
import imgui.ImVec2;
import imgui.extension.imlayout.ImGuiLayoutOptions;
import imgui.extension.imlayout.ImLayout;
import xlight.editor.imgui.util.XImGuiButton;
import xlight.engine.core.editor.ui.XDragDropTarget;
import xlight.engine.core.editor.ui.XUIData;
import xlight.engine.list.XLinkedListNode;
import xlight.engine.math.XColor;
import static imgui.ImGuiHoveredFlags.ImGuiHoveredFlags_None;
import static imgui.ImGuiMouseButton.ImGuiMouseButton_Left;
import static imgui.ImGuiMouseButton.ImGuiMouseButton_Right;
import static imgui.ImGuiStyleVar.ImGuiStyleVar_Alpha;

public class XFileBrowserRenderer {

    int selectedColor = XColor.toABGRIntBits(255, 255, 255, 50);
    int hoveredColor = XColor.toABGRIntBits(255, 255, 255, 30);
    int hoveredStrokeColor = XColor.toABGRIntBits(255, 255, 255, 35);

    private static final String CONTEXT_MENU_RIGHT_CLICK = "CONTEXT_MENU_RIGHT_CLICK";
    private static final String CONTEXT_MENU_RENAME_FILE = "CONTEXT_MENU_RENAME_FILE";
    private static final String MODAL_INVALID_FILE_OPERATION = "Invalid File operation";
    private static final String MODAL_DELETE = "Delete";

    private ImGuiLayoutOptions op;

    private boolean isFileDragging;

    private XFileOpenListener fileOpenListener;

    private ImGuiString imGuiString;

    private final Texture folderTexture;
    private final Texture fileTexture;

    public XFileBrowserRenderer(Texture folderTexture, Texture fileTexture) {
        this.folderTexture = folderTexture;
        this.fileTexture = fileTexture;
        op = new ImGuiLayoutOptions();
        op.set_paddingBottom(5);
        op.set_paddingLeft(4);
        op.set_paddingRight(4);
        op.set_clipping(false);
        imGuiString = new ImGuiString();
    }

    public void setFileOpenListener(XFileOpenListener fileOpenListener) {
        this.fileOpenListener = fileOpenListener;
    }

    public void render(XFileManager fileData, XUIData uiData) {
        XFile currentFolder = fileData.currentFolder;
        if(currentFolder == null || !currentFolder.isDirectory()) {
            return;
        }
        ObjectSet<XFile> selectedFiles = fileData.selectedFiles;

        XFile updateFolder = null;

        ImGuiContext imGuiContext = ImGui.GetCurrentContext();
        float availSizeX = ImGui.GetContentRegionAvail().get_x();
        ImGuiStyle style = ImGui.GetStyle();
        int curLine = 0;

        float padding = fileData.thumbnailPadding;
        float thumbnailSize = fileData.thumbnailSize;

        ImGuiWindow window = ImGuiInternal.GetCurrentWindow();
        boolean isLeftCtrl = ImGui.IsKeyDown(ImGuiKey.ImGuiKey_LeftCtrl);
        boolean isMouseReleasedLeft = ImGui.IsMouseReleased(ImGuiMouseButton_Left);
        boolean isMouseReleasedRight = ImGui.IsMouseReleased(ImGuiMouseButton_Right);
        int activeID = ImGuiInternal.GetActiveID();

        ImVec2 cursorPosStart = ImGui.GetCursorScreenPos();
        float cursorX = cursorPosStart.get_x();
        boolean openRightClickMenu = false;
        boolean isDraggingFile = false;
        boolean invalidFileOperation = false;
        boolean first = true;

        ImLayout.BeginLayout("root" + curLine, ImLayout.MATCH_PARENT, ImLayout.MATCH_WRAP_PARENT);
        if(fileData.debugThumbnail == 1) {
            ImLayout.ShowLayoutDebug();
        }
        XLinkedListNode<XFile> cur = currentFolder.files.getHead();
        int size = currentFolder.files.getSize();
        for(int n = 0; cur != null; n++) {
            if(first) {
                first = false;
                ImLayout.BeginLayout("line" + curLine, ImLayout.WRAP_PARENT, ImLayout.WRAP_PARENT);
            }
            // PushID is required to not duplicate right click action menu
            ImGui.PushID(n);

            XFile xFile = cur.getValue();

            boolean isDirectory = xFile.isDirectory();
            boolean isSelected = selectedFiles.contains(xFile);
            boolean isCutFile = fileData.isCutFile(xFile);

            Texture texture = isDirectory ? folderTexture : fileTexture;
            int textureHandle = texture.getTextureObjectHandle();

            if(isCutFile) {
                ImGui.PushStyleVar(ImGuiStyleVar_Alpha, 0.5f);
            }
            renderItem(fileData, xFile, textureHandle, thumbnailSize, false);
            if(isCutFile) {
                ImGui.PopStyleVar();
            }

            if(ImGui.BeginDragDropSource()) {
                isDraggingFile = true;

                ImGuiPayload payload = ImGui.GetDragDropPayload();
                if(payload == null)
                {
                    if(!selectedFiles.contains(xFile)) {
                        selectedFiles.clear();
                        selectedFiles.add(xFile);
                    }
                    ImGui.SetDragDropPayload(XDragDropTarget.FILE_SOURCE, 0);

                    uiData.setDropTarget(xFile.getPath());
                }

                renderItem(fileData, xFile, textureHandle, thumbnailSize, true);
                ImGui.EndDragDropSource();
            }

            if(isDirectory) {
                if(!isSelected && ImGui.BeginDragDropTarget()) {
                    ImGuiPayload dragDropPayload = ImGui.AcceptDragDropPayload(XDragDropTarget.FILE_SOURCE);
                    if(dragDropPayload != null) {
                        if(!fileData.dragSelectedFilesToFolder(xFile)) {
                            invalidFileOperation = true;
                        }
                    }
                    ImGui.EndDragDropTarget();
                }
            }

            int layoutId = ImGui.GetItemID();

            ImRect rect = imGuiContext.get_LastItemData().get_Rect();
            ImVec2 min = rect.get_Min();
            ImVec2 max = rect.get_Max();
            float minX = min.get_x();
            float minY = min.get_y();
            float maxX = max.get_x();
            float maxY = max.get_y();
            ImRect.TMP_1.get_Min().set(minX, minY);
            ImRect.TMP_1.get_Max().set(maxX, maxY);

            boolean clickedLeft = ImGui.IsItemClicked(ImGuiMouseButton_Left);
            boolean clickedRight = ImGui.IsItemClicked(ImGuiMouseButton_Right);
            boolean clickReleasedLeft = isMouseReleasedLeft && ImGui.IsItemHovered(ImGuiHoveredFlags_None) && activeID == layoutId;
            boolean clickReleasedRight = isMouseReleasedRight && ImGui.IsItemHovered(ImGuiHoveredFlags_None) && activeID == layoutId;
            boolean doubleClicked = isDirectory && ImGui.IsMouseDoubleClicked(ImGuiMouseButton_Left) && ImGui.IsItemHovered(ImGuiHoveredFlags_None);;
            boolean hovered = ImGui.IsItemHovered() && !isSelected;
            if(clickedLeft || clickedRight) {
                ImGuiWindow imGuiWindow = ImGuiInternal.GetCurrentWindow();
                ImGuiInternal.SetActiveID(layoutId, imGuiWindow);
            }

            if(doubleClicked) {
                updateFolder = xFile;
                selectedFiles.clear();
            }
            if(isMouseReleasedLeft || isMouseReleasedRight) {
                if (activeID == layoutId) {
                    ImGuiInternal.SetActiveID(0, ImGuiInternal.GetCurrentWindow());
                }
            }
            if(updateFolder == null) {
                if(clickReleasedLeft) {
                    if(isLeftCtrl) {
                        if(selectedFiles.contains(xFile)) {
                            selectedFiles.remove(xFile);
                        }
                        else {
                            selectedFiles.add(xFile);
                        }
                    }
                    else {
                        selectedFiles.clear();
                        selectedFiles.add(xFile);
                    }
                }
                else if(clickReleasedRight) {
                    if(!isSelected) {
                        selectedFiles.clear();
                        selectedFiles.add(xFile);
                    }
                    openRightClickMenu = true;
                }
            }

            if(isSelected) {
                ImGui.GetWindowDrawList().AddRectFilled(ImRect.TMP_1.get_Min(), ImRect.TMP_1.get_Max(), selectedColor);
            }
            if(hovered) {
                ImGui.GetWindowDrawList().AddRectFilled(ImRect.TMP_1.get_Min(), ImRect.TMP_1.get_Max(), hoveredColor);
                ImGui.GetWindowDrawList().AddRect(ImRect.TMP_1.get_Min(), ImRect.TMP_1.get_Max(), hoveredStrokeColor);
            }

            float lastItemX = maxX - cursorX;
            float padThumbSize = thumbnailSize + padding;
            float finalX = lastItemX + padThumbSize;
            if (n + 1 < size && finalX < availSizeX) {
                ImGui.SameLine(0, padding);
            }
            else {
                curLine++;
                ImLayout.EndLayout();
                ImLayout.BeginLayout("line" + curLine, ImLayout.WRAP_PARENT, ImLayout.WRAP_PARENT);
            }

            ImGui.PopID();

            cur = cur.getNext();
        }
        if(!first) {
            ImLayout.EndLayout();
        }

        if(isDraggingFile != isFileDragging) {
            isFileDragging = isDraggingFile;
            if(isFileDragging) {
                fileData.dragFilesOperations.clear();
                if(fileData.selectedFiles.size != 0) {
                    fileData.dragFilesOperations.addAll(fileData.selectedFiles);
                }
            }
            else {
                // Clear move files because drop may fail
                fileData.dragFilesOperations.clear();
            }
        }

        if(updateFolder != null) {
            fileData.updatedFolder(updateFolder);
        }

        ImLayout.EndLayout(); // Root

        if(ImGui.IsItemClicked(ImGuiMouseButton_Left)) {
            selectedFiles.clear();
        }
        if(ImGui.IsItemClicked(ImGuiMouseButton_Right)) {
            selectedFiles.clear();
            openRightClickMenu = true;
        }

        if(openRightClickMenu) {
            ImGui.OpenPopup(CONTEXT_MENU_RIGHT_CLICK);
        }
        int menuFlag = ImGuiWindowFlags.ImGuiWindowFlags_NoResize | ImGuiWindowFlags.ImGuiWindowFlags_NoTitleBar | ImGuiWindowFlags.ImGuiWindowFlags_NoSavedSettings;
        if(ImGui.BeginPopup(CONTEXT_MENU_RIGHT_CLICK, menuFlag)) {
            if(selectedFiles.isEmpty()) {
                int i = renderPaste(fileData);
                if(i == 0) {
                    invalidFileOperation = true;
                }
                renderNewFolder(fileData);
                renderNewTextFile(fileData);
            }
            else {
                renderOpenDirectory(fileData);
                renderOpenFile(fileData);
                renderRename(fileData);
                renderCopy(fileData);
                renderCopyPath(fileData);
                renderCut(fileData);
                renderDeleteModal(fileData);
            }
            ImGui.EndPopup();
        }

        if(invalidFileOperation) {
            ImGui.OpenPopup(MODAL_INVALID_FILE_OPERATION);
        }
        renderInvalidFileOperation();
    }

    private int renderPaste(XFileManager fileData) {
        if(fileData.haveCurOrPaste()) {
            if(XImGuiButton.buttonMatchOrWrap("Paste")) {
                ImGui.CloseCurrentPopup();
                return fileData.pasteSelectedFiles();
            }
        }
        return -1;
    }
    private void renderNewFolder(XFileManager fileData) {
        if(XImGuiButton.buttonMatchOrWrap("New Folder")) {
            ImGui.CloseCurrentPopup();
            fileData.createNewFolder();
        }
    }
    private void renderNewTextFile(XFileManager fileData) {
        if(XImGuiButton.buttonMatchOrWrap("New text")) {
            ImGui.CloseCurrentPopup();
            fileData.createNewTxtFile();
        }
    }

    private void renderCopy(XFileManager fileData) {
        if(XImGuiButton.buttonMatchOrWrap("Copy")) {
            ImGui.CloseCurrentPopup();
            fileData.copySelectedFiles();
        }
    }

    private void renderCopyPath(XFileManager fileData) {
        if(XImGuiButton.buttonMatchOrWrap("Copy Path")) {
            ImGui.CloseCurrentPopup();
            String path = fileData.getFilePath();
            if(path != null) {
                Gdx.app.getClipboard().setContents(path);
            }
        }
    }

    private void renderCut(XFileManager fileData) {
        if(XImGuiButton.buttonMatchOrWrap("Cut")) {
            ImGui.CloseCurrentPopup();
            fileData.cutSelectedFiles();
        }
    }

    private void renderRename(XFileManager fileData) {
        if(XImGuiButton.buttonMatchOrWrap("Rename")) {
            ImGui.CloseCurrentPopup();
            fileData.setFileRenameState();
        }
    }

    private void renderOpenDirectory(XFileManager fileData) {
        if(fileData.selectedFiles.size == 1) {
            XFile selected = fileData.selectedFiles.first();
            boolean isDirectory = selected.isDirectory();
            if(isDirectory) {
                if(XImGuiButton.buttonMatchOrWrap("Open")) {
                    ImGui.CloseCurrentPopup();
                    fileData.updatedFolder(selected);
                }
            }
        }
    }

    private void renderOpenFile(XFileManager fileData) {
        if(fileData.selectedFiles.size == 1) {
            XFile selected = fileData.selectedFiles.first();
            boolean isDirectory = selected.isDirectory();
            if(!isDirectory && fileOpenListener != null) {
                Array<String> actions = fileOpenListener.allowFile(selected);
                if(actions != null && actions.size > 0) {
                    for(int i = 0; i < actions.size; i++) {
                        String action = actions.get(i);
                        if(XImGuiButton.buttonMatchOrWrap(action)) {
                            ImGui.CloseCurrentPopup();
                            if(isDirectory) {
                                fileData.updatedFolder(selected);
                            }
                            else {
                                fileOpenListener.onOpenFile(selected, i);
                            }
                        }
                    }
                }
            }
        }
    }

    private void renderDeleteModal(XFileManager fileData) {
        boolean closeDeleteMenu = false;
        if(XImGuiButton.buttonMatchOrWrap("Delete")) {
            ImGui.OpenPopup(MODAL_DELETE);
        }
        int flag = ImGuiWindowFlags.ImGuiWindowFlags_NoResize | ImGuiWindowFlags.ImGuiWindowFlags_NoSavedSettings;
        if(ImGui.BeginPopupModal(MODAL_DELETE, null, flag)) {
            ImGui.Text("Are you sure to delete?");
            if(ImGui.Button("Yes")) {
                ImGui.CloseCurrentPopup();
                fileData.deleteSelectedFiles();
                closeDeleteMenu = true;
            }
            ImGui.SameLine();
            if(ImGui.Button("No")) {
                ImGui.CloseCurrentPopup();
                closeDeleteMenu = true;
            }
            ImGui.EndPopup();
        }
        if(closeDeleteMenu) {
            ImGui.CloseCurrentPopup();
        }
    }

    private void renderInvalidFileOperation() {
        int flag = ImGuiWindowFlags.ImGuiWindowFlags_NoResize | ImGuiWindowFlags.ImGuiWindowFlags_NoSavedSettings;
        if(ImGui.BeginPopupModal(MODAL_INVALID_FILE_OPERATION, null, flag)) {
            if(ImGui.Button("Ok", ImVec2.TMP_1.set(150, 0))) {
                ImGui.CloseCurrentPopup();
            }
            ImGui.EndPopup();
        }
    }

    private void renderItem(XFileManager fileData, XFile xFile, int textureHandle, float thumbnailSize, boolean isFileDragging) {
        String name1 = xFile.getName();

        boolean isRename = false;

        ObjectSet<XFile> selectedFiles = fileData.selectedFiles;
        if(isFileDragging && selectedFiles.size > 0) {
            if(selectedFiles.size > 1) {
                name1 = selectedFiles.size + " Files";
            }
        }

        if(fileData.selectedFiles.size == 1) {
            if(selectedFiles.first() == xFile) {
                if(xFile.isRename) {
                    isRename = true;
                }
            }
        }

        if(fileData.debugThumbnail == 1 || fileData.debugThumbnail == 2) {
            ImLayout.ShowLayoutDebug();
        }
        ImLayout.BeginLayout(name1, thumbnailSize, ImLayout.WRAP_PARENT);
        ImGui.Image(textureHandle, ImVec2.TMP_1.set(thumbnailSize, thumbnailSize));
        {
            ImLayout.BeginAlign(name1, ImLayout.MATCH_PARENT, ImLayout.WRAP_PARENT, 0.5f, 0, 0, 0, op);
            if(fileData.debugThumbnail == 1 || fileData.debugThumbnail == 4) {
                ImLayout.ShowLayoutDebug();
            }
            ImGui.TextWrapped(name1);
            ImLayout.EndAlign();
        }
        if(fileData.debugThumbnail == 1 || fileData.debugThumbnail == 3) {
            ImLayout.ShowLayoutDebug();
        }
        ImLayout.EndLayout();

        if(isRename) {
            ImGui.OpenPopup(CONTEXT_MENU_RENAME_FILE);
            xFile.isRename = false;
        }
        int menuFlag = ImGuiWindowFlags.ImGuiWindowFlags_NoResize | ImGuiWindowFlags.ImGuiWindowFlags_NoSavedSettings;
        if(ImGui.BeginPopup(CONTEXT_MENU_RENAME_FILE, menuFlag)) {
            ImGui.Text("Rename:");
            ImGui.SetNextItemWidth(150);
            imGuiString.setValue(name1);
            ImGui.SetKeyboardFocusHere();
            int flag = ImGuiInputTextFlags.ImGuiInputTextFlags_EnterReturnsTrue;
            if(ImGui.InputText("##renameText", imGuiString, imGuiString.getSize(), flag)) {
                String newName = imGuiString.getValue().trim();
                if(!name1.equals(newName)) {
                    fileData.renameFile(xFile, newName);
                }
                ImGui.CloseCurrentPopup();
            }
            ImGui.EndPopup();
        }
    }
}