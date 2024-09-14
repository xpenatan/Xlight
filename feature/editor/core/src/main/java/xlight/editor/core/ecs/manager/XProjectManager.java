package xlight.editor.core.ecs.manager;

import xlight.editor.core.project.XProjectOptions;

public interface XProjectManager {
    XProjectOptions getProject();
    void newProject(XProjectOptions options);
}