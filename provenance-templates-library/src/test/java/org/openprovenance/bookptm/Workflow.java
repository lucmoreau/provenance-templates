package org.openprovenance.bookptm;

import java.util.List;

public interface Workflow {
    List<TemplateConnection> getConnections();
    List<TemplateConnection> getConnectionsNoAgent();
    List<Object> run();
}
