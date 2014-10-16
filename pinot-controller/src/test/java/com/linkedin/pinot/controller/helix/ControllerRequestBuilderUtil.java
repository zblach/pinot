package com.linkedin.pinot.controller.helix;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.linkedin.pinot.controller.helix.core.PinotHelixResourceManager;


/**
 * @author Dhaval Patel<dpatel@linkedin.com>
 * Sep 29, 2014
 */

public class ControllerRequestBuilderUtil {

  public static JSONObject buildCreateResourceJSON(String resourceName, int numInstances, int numReplicas)
      throws JSONException {
    final JSONObject ret = new JSONObject();
    ret.put("resourceName", resourceName);
    ret.put("tableName", resourceName);
    ret.put("timeColumnName", "day");
    ret.put("timeType", "daysSinceEpoch");
    ret.put("numInstances", numInstances);
    ret.put("numReplicas", numReplicas);
    ret.put("retentionTimeUnit", "DAY");
    ret.put("retentionTimeValue", "90");
    ret.put("pushFrequency", "daily");
    ret.put("segmentAssignmentStrategy", "BalanceNumSegmentAssignmentStrategy");
    return ret;
  }

  public static JSONObject buildInstanceCreateRequestJSON(String host, String port, String tag) throws JSONException {
    final JSONObject ret = new JSONObject();
    ret.put("host", host);
    ret.put("port", port);
    ret.put("tag", tag);
    return ret;
  }

  public static JSONArray buildBulkInstanceCreateRequestJSON(int start, int end) throws JSONException {
    final JSONArray ret = new JSONArray();
    for (int i = start; i <= end; i++) {
      final JSONObject ins = new JSONObject();
      ins.put("host", "localhost");
      ins.put("port", i);
      ins.put("tag", PinotHelixResourceManager.UNTAGGED);
      ret.put(ins);
    }

    return ret;
  }

}