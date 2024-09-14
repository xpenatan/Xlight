package xlight.editor.core;

import xlight.editor.impl.XEditorImpl;
import xlight.engine.core.XApplication;
import xlight.engine.core.XEngine;

public interface XEditor extends XApplication {
    static XEditor newInstance() { return new XEditorImpl(); }

    XEngine getEditorEngine();
}