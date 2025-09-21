
package org.openprovenance.bookptm;


import org.openprovenance.bk.physical.client.common.HandoverBuilder;
import org.openprovenance.bk.physical.client.common.TransportingBuilder;
import org.openprovenance.bk.physical.client.configurator.TableConfigurator;
import org.openprovenance.prov.template.log2prov.FileBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TableConfiguratorForSuccessors implements TableConfigurator<Map<String, List<String>>> {

  private final Map<String, FileBuilder> documentBuilderDispatcher;

  public TableConfiguratorForSuccessors(Map<String, FileBuilder> documentBuilderDispatcher) {
    this.documentBuilderDispatcher=documentBuilderDispatcher;
  }


    @Override
    public Map<String, List<String>> transporting(TransportingBuilder builder) {
        String[] order=TransportingBuilder.propertyOrder;
        return TransportingBuilder.__successors.keySet().stream()
                .collect(Collectors.toMap(
                        k -> order[k],
                        k -> Arrays.stream(TransportingBuilder.__successors.get(k))
                                .mapToObj(v -> order[v])
                                .collect(Collectors.toList())));
    }

    @Override
    public Map<String, List<String>> handover(HandoverBuilder builder) {
        String[] order=HandoverBuilder.propertyOrder;
        return HandoverBuilder.__successors.keySet().stream()
                .collect(Collectors.toMap(
                        k -> order[k],
                        k -> Arrays.stream(HandoverBuilder.__successors.get(k))
                                .mapToObj(v -> order[v])
                                .collect(Collectors.toList())));
    }


}
