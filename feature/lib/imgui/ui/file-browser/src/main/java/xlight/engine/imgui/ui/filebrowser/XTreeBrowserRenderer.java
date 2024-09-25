package xlight.engine.imgui.ui.filebrowser;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.imlayout.ImLayout;
import xlight.engine.list.XLinkedListNode;
import static imgui.ImGuiCond.ImGuiCond_Once;

public class XTreeBrowserRenderer {

    private Array<XLinkedListNode<XFile>> list = new Array<>();
    private final Texture folderTexture;

    public XTreeBrowserRenderer(Texture folderTexture) {
        this.folderTexture = folderTexture;
    }

    public void render(XFileManager fileData) {
        if(fileData.treeRootFolder == null) {
            return;
        }
        boolean isOpen = renderDirectory(fileData, fileData.treeRootFolder);
        if(isOpen) {
            XLinkedListNode<XFile> cur = fileData.treeRootFolder.files.getHead();
            while(cur != null) {
                XFile child = cur.getValue();
                if(child.isDirectory()) {
                    boolean isChildOpen = renderDirectory(fileData, child);
                    if(isChildOpen) {
                        list.add(cur);
                        cur = child.files.getHead();
                    }
                    else {
                        ImLayout.EndTree();
                        cur = cur.getNext();
                    }
                }
                else {
                    cur = cur.getNext();
                }
                if(cur == null) {
                    while(cur == null && !list.isEmpty()) {
                        cur = list.pop();
                        cur = cur.getNext();
                        ImLayout.EndTree();
                    }
                }
            }
        }
        if(list.size != 0) {
            throw new GdxRuntimeException("List size should be 0");
        }
        ImLayout.EndTree();
    }

    private boolean renderDirectory(XFileManager fileData, XFile cur) {
        if(cur == null) {
            return false;
        }

        int height = 10;
        String name = cur.name;

        boolean isLeaf = true;

        XLinkedListNode<XFile> curr = cur.files.getHead();
        while(curr != null) {
            // Check if cur folder contains subfolder
            XFile file = curr.getValue();
            if(file.isDirectory()) {
                isLeaf = false;
                break;
            }
            curr = curr.getNext();
        }

        boolean isSelected = fileData.currentFolder == cur;

        ImGui.SetNextItemOpen(true, ImGuiCond_Once);

        ImLayout.BeginTree(name);
        ImLayout.BeginTreeLayout(ImLayout.GetTreeHeight(height), isLeaf, isSelected, cur.isTreeOpen);
        {
            ImLayout.BeginAlign("FolderIcon", ImLayout.WRAP_PARENT, ImLayout.MATCH_PARENT, 0.0f, 0.5f);
            ImGui.Image(folderTexture.getTextureObjectHandle(), ImVec2.TMP_1.set(15,15));
            ImLayout.EndAlign();
            ImGui.SameLine();
            ImLayout.BeginAlign("FolderName", ImLayout.WRAP_PARENT, ImLayout.MATCH_PARENT, 0.0f, 0.5f);
            ImGui.Text(name);
            ImLayout.EndAlign();
        }
        if(ImLayout.EndTreeLayout()) {
            fileData.updatedFolder(cur);
        }

        boolean treeOpen = ImLayout.IsTreeOpen();
        cur.isTreeOpen = treeOpen;
        return treeOpen;
    }
}