package ai.ksoot.rest.mcp.server.common;

import java.io.Serializable;

/**
 * @param <V> Version type
 * @author Rajveer Singh
 */
public interface Versionable<V extends Comparable<V> & Serializable> {

  V getVersion();
}
