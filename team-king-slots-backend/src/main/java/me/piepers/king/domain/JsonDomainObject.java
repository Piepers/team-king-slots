package me.piepers.king.domain;

import io.vertx.core.json.JsonObject;

public interface JsonDomainObject {
    default JsonObject toJson() {
        return JsonObject.mapFrom(this);
    }
}
