package xlight.editor.core.project;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import xlight.engine.json.XJson;
import xlight.engine.json.XJsonValue;
import xlight.engine.list.XList;
import xlight.engine.list.XStringArray;
import xlight.engine.pool.XPoolController;
import xlight.engine.properties.XProperties;

public class XProjectOptions {
    public final Array<String> buildPath = new Array<>();
    public String mainApplication;
    private String assetsPath;
    private String projectAssetPath;

    public XProjectOptions() {
    }

    public boolean loadProject(XPoolController poolController, FileHandle projectPath) {
        if(projectPath.exists() && projectPath.isDirectory()) {
            FileHandle projectFile = projectPath.child("project.x");
            if(projectFile.exists() && !projectFile.isDirectory()) {
                String jsonProject = projectFile.readString();
                XJson xJson = XJson.create();
                XJsonValue xJsonValue = xJson.loadJson(jsonProject);
                XProperties properties = XProperties.obtain(poolController);
                properties.loadJson(xJsonValue);

                if(Gdx.app.getType() != Application.ApplicationType.WebGL) {
                    XStringArray buildPath = properties.getStringArray("buildPath");
                    XList<String> list = buildPath.getList();
                    for(String s : list) {
                        FileHandle child = projectPath.child(s).child("build/classes/java/main");
                        if(child.exists()) {
                            s = child.path();
                        }
                        this.buildPath.add(s);
                    }
                }
                mainApplication = properties.get("mainApplication", "");
                String assetsPath = properties.get("assetsPath", "");
                FileHandle assetsFileHandle = projectPath.child(assetsPath);
                this.assetsPath = assetsFileHandle.path();
                if(!this.assetsPath.endsWith("/")) {
                    // Asset must end with /
                    this.assetsPath += "/";
                }
                getProjectAssetPath();
                return true;
            }
        }
        return false;
    }

    public void setAssetsPath(String path) {
        assetsPath = path;
        projectAssetPath = null;
    }

    public String getProjectAssetPath() {
        if(assetsPath != null && projectAssetPath == null) {
            projectAssetPath = assetsPath;
            if(projectAssetPath.endsWith("/")) {
                projectAssetPath = projectAssetPath.substring(0, projectAssetPath.length() - 1);
            }
            projectAssetPath = projectAssetPath + "_raw/";
        }
        return projectAssetPath;
    }
}