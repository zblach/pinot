package com.linkedin.pinot.controller.api.reslet.resources.v2;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.linkedin.pinot.common.config.AbstractTableConfig;
import com.linkedin.pinot.common.utils.CommonConstants.Helix.TableType;
import com.linkedin.pinot.controller.ControllerConf;
import com.linkedin.pinot.controller.api.reslet.resources.PinotFileUpload;
import com.linkedin.pinot.controller.helix.core.PinotHelixResourceManager;


public class PinotTableRestletResource extends ServerResource {
  private static final Logger LOGGER = LoggerFactory.getLogger(PinotTableRestletResource.class);
  private final ControllerConf conf;
  private final PinotHelixResourceManager manager;
  private final File baseDataDir;
  private final File tempDir;

  public PinotTableRestletResource() throws IOException {
    conf = (ControllerConf) getApplication().getContext().getAttributes().get(ControllerConf.class.toString());
    manager =
        (PinotHelixResourceManager) getApplication().getContext().getAttributes()
            .get(PinotHelixResourceManager.class.toString());
    baseDataDir = new File(conf.getDataDir());
    if (!baseDataDir.exists()) {
      FileUtils.forceMkdir(baseDataDir);
    }
    tempDir = new File(baseDataDir, "schemasTemp");
    if (!tempDir.exists()) {
      FileUtils.forceMkdir(tempDir);
    }
  }

  @Override
  @Post("json")
  public Representation post(Representation entity) {
    AbstractTableConfig config = null;
    try {
      String jsonRequest = entity.getText();
      config = AbstractTableConfig.init(jsonRequest);
      try {
        manager.addTable(config);
      } catch (Exception e) {
        e.printStackTrace();
        return new StringRepresentation("Failed: " + e.getMessage());
      }
      return new StringRepresentation("Success");
    } catch (Exception e) {
      LOGGER.error("error reading/serializing requestJSON", e);
      return new StringRepresentation("Failed: " + e.getMessage());
    }
  }

  @Override
  @Get
  public Representation get() {
    final String tableName = (String) getRequest().getAttributes().get("tableName");
    if (tableName == null) {
      return new StringRepresentation("tableName is not present");
    }
    try {
      JSONArray ret = new JSONArray();

      if (manager.hasOfflineTable(tableName)) {
        ret.add(manager.getTableConfig(tableName, TableType.OFFLINE).toJSON());
      }

      if (manager.hasRealtimeTable(tableName)) {
        ret.add(manager.getTableConfig(tableName, TableType.REALTIME).toJSON());
      }

      return new StringRepresentation(ret.toString());
    } catch (Exception e) {
      LOGGER.error("error processing get table config", e);
      return PinotFileUpload.exceptionToStringRepresentation(e);
    }
  }

  @Override
  @Delete
  public Representation delete() {
    StringRepresentation presentation = null;

    final String tableName = (String) getRequest().getAttributes().get("tableName");
    if (tableName == null) {
      return new StringRepresentation("tableName is not present");
    }
    final String type = getReference().getQueryAsForm().getValues("type");
    if (type == null || type.equalsIgnoreCase("offline")) {
      manager.deleteOfflineTable(tableName);
    }
    if (type == null || type.equalsIgnoreCase("realtime")) {
      manager.deleteRealtimeTable(tableName);
    }
    return presentation;
  }
}