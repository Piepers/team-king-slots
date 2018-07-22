package me.piepers.king.domain;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Representation of a row of cells in a reel.
 *
 * @author Bas Piepers
 *
 */
@DataObject
public class ReelRow implements JsonDomainObject {

    List<ReelCell> cell;

    public ReelRow(JsonObject jsonObject) {

    }
}
