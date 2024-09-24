package xlight.editor.core.selection;

import xlight.engine.list.XDataArray;
import xlight.engine.list.XList;
import xlight.engine.pool.XPool;

public abstract class XObjectSelection<T, NODE extends XDataArray.XDataArrayNode<T>>  {

    final private XDataArray<T, NODE> selectedTargets;

    public XObjectSelection(XPool<NODE> pool) {
        selectedTargets = new XDataArray<>(pool);
    }

    public boolean isSelected(T target) {
        return selectedTargets.contains(target, true);
    }

    protected boolean addSelectedTargetInternal(T target) {
        if(!selectedTargets.contains(target, true)) {
            selectedTargets.add(target);
            return true;
        }
        return false;
    }

    protected boolean removeSelectedTargetInternal(T target) {
        int index = selectedTargets.indexOf(target, true);
        if(index >= 0) {
            selectedTargets.removeIndex(index);
        }
        return false;
    }

    public void selectTarget(T target, boolean multiSelect) {
        int index = selectedTargets.indexOf(target, true);
        if(index >= 0) {
            if(multiSelect) {
                removeSelectedTargetInternal(target);
            }
            else {
                boolean hadMultipleSelectedTargets = selectedTargets.getSize() > 1;
                unselectAllTargets();
                if(hadMultipleSelectedTargets) {
                    addSelectedTargetInternal(target);
                }
            }
        }
        else {
            if(!multiSelect) {
                unselectAllTargets();
            }
            addSelectedTargetInternal(target);
        }
    }

    public void unselectAllTargets() {
        while(selectedTargets.getSize() > 0) {
            T t = selectedTargets.get(0);
            removeSelectedTargetInternal(t);
        }
    }

    public void unselectTarget(T t) {
        removeSelectedTargetInternal(t);
    }

    public T getCurrentSelectedTarget() {
        if(selectedTargets.getSize() > 0) {
            return selectedTargets.get(0);
        }
        return null;
    }

    public XList<T> getSelectedTargets() {
        return selectedTargets.getList();
    }

    public XList<NODE> getSelectedTargetsNode() {
        return selectedTargets.getNodeList();
    }

    public NODE getSelectedIndex(int index) {
        return selectedTargets.getNode(index);
    }

    public boolean changeCurrentSelectedTarget(T target) {
        for(int i = 1; i < selectedTargets.getSize(); i++) {
            T selectedTarget = selectedTargets.get(i);
            if(target == selectedTarget) {
                selectedTargets.swap(0, i);
                return true;
            }
        }
        return false;
    }
}