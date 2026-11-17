package com.adobe.aem.guides.demo.core.utills;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.sling.api.resource.Resource;

public final class RegionTreeUtil {

    public static final String BASE_PATH   = "/conf/region-tree";
    public static final String TEMP_PATH   = BASE_PATH + "/temp";
    public static final String ACTUAL_PATH = BASE_PATH + "/actual";
    public static final String DATA_PATH   = BASE_PATH + "/data";

    public static final String TOPIC_TEMP   = "com/jet2/region-tree/temp";
    public static final String TOPIC_UPDATE = "com/jet2/region-tree/update";
    public static final String SUBSERVICE   = "region-tree-service";

    public static final String REGION = "region";
    public static final String AREA   = "area";
    public static final String RESORT = "resort";

    private static final String[] LEVELS = {REGION, AREA, RESORT};
    private static final Pattern VALID_ID = Pattern.compile("[A-Za-z0-9_-]+");

    private RegionTreeUtil() { }

    /** Ids become JCR node names, so only safe characters are allowed. */
    public static boolean isValidId(String id) {
        return id != null && VALID_ID.matcher(id).matches();
    }

    /** Builds the properties of one node. relPath is the id chain, e.g. "1/49/180". */
    public static Map<String, Object> buildProperties(String id, String name, String title,
            String regionId, String areaId, String relPath, String damPath, String tagPath) {
        Map<String, Object> props = new HashMap<>();
        props.put("jcr:primaryType", "nt:unstructured");
        props.put("id", id);
        props.put("name", name);
        props.put("title", title != null ? title : name);
        props.put("regionId", regionId);
        props.put("areaId", areaId);
        props.put("damPath", damPath != null ? damPath : "/content/dam/region-tree/" + relPath);
        props.put("tagPath", tagPath != null ? tagPath : "region-tree:" + relPath);
        props.put("lastModified", Calendar.getInstance());
        return props;
    }

    /** A flattened view of one node in the tree. */
    public static class NodeInfo {
        public final String key;       // level:id
        public final String id;
        public final String regionId;
        public final String areaId;
        public final String title;
        public final String relPath;   // e.g. /1/49/180

        NodeInfo(String key, String id, String regionId, String areaId, String title, String relPath) {
            this.key = key;
            this.id = id;
            this.regionId = regionId;
            this.areaId = areaId;
            this.title = title;
            this.relPath = relPath;
        }
    }

    /** Walks region > area > resort and returns every node keyed by level:id. */
    public static Map<String, NodeInfo> flatten(Resource root) {
        Map<String, NodeInfo> result = new LinkedHashMap<>();
        if (root != null) {
            walk(root, root.getPath(), 0, result);
        }
        return result;
    }

    private static void walk(Resource parent, String rootPath, int depth, Map<String, NodeInfo> result) {
        if (depth >= LEVELS.length) {
            return;
        }
        for (Resource child : parent.getChildren()) {
            String id = child.getValueMap().get("id", child.getName());
            String key = LEVELS[depth] + ":" + id;
            String relPath = child.getPath().substring(rootPath.length());
            result.put(key, new NodeInfo(key, id,
                    child.getValueMap().get("regionId", ""),
                    child.getValueMap().get("areaId", ""),
                    child.getValueMap().get("title", ""),
                    relPath));
            walk(child, rootPath, depth + 1, result);
        }
    }

    /** Compares temp with actual and fills the three lists. */
    public static void compare(Map<String, NodeInfo> temp, Map<String, NodeInfo> actual,
            List<String> created, List<String> updated, List<String> moved) {

        for (NodeInfo t : temp.values()) {
            NodeInfo a = actual.get(t.key);
            String newPath = ACTUAL_PATH + t.relPath;

            if (a == null) {
                created.add(newPath);
            } else if (!t.regionId.equals(a.regionId) || !t.areaId.equals(a.areaId)) {
                moved.add(ACTUAL_PATH + a.relPath + ">" + newPath);   // moved wins over updated
            } else if (!t.title.equals(a.title)) {
                updated.add(newPath);
            }
        }
    }
}