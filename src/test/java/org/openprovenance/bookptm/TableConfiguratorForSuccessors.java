
package org.openprovenance.bookptm;


import org.openprovenance.templates.catalogue.transport.configurator.TableConfigurator;
import org.openprovenance.book.physical.client.common.*;
import org.openprovenance.prov.template.log2prov.FileBuilder;
import org.openprovenance.book.responsibility.client.common.HandingoverBuilder;

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
    public Map<String, List<String>> handingover(HandingoverBuilder builder) {
        String[] order=HandingoverBuilder.propertyOrder;
        return HandingoverBuilder.__successors.keySet().stream()
                .collect(Collectors.toMap(
                        k -> order[k],
                        k -> Arrays.stream(HandingoverBuilder.__successors.get(k))
                                .mapToObj(v -> order[v])
                                .collect(Collectors.toList())));
    }

    @Override
    public Map<String, List<String>> weighing(WeighingBuilder builder) {
        String[] order=WeighingBuilder.propertyOrder;
        return WeighingBuilder.__successors.keySet().stream()
                .collect(Collectors.toMap(
                        k -> order[k],
                        k -> Arrays.stream(WeighingBuilder.__successors.get(k))
                                .mapToObj(v -> order[v])
                                .collect(Collectors.toList())));
    }

    @Override
    public Map<String, List<String>> agent_init(Agent_initBuilder builder) {
        String[] order=Agent_initBuilder.propertyOrder;
        return Agent_initBuilder.__successors.keySet().stream()
                .collect(Collectors.toMap(
                        k -> order[k],
                        k -> Arrays.stream(Agent_initBuilder.__successors.get(k))
                                .mapToObj(v -> order[v])
                                .collect(Collectors.toList())));
    }

    @Override
    public Map<String, List<String>> item_init(Item_initBuilder builder) {
        String[] order=Item_initBuilder.propertyOrder;
        return Item_initBuilder.__successors.keySet().stream()
                .collect(Collectors.toMap(
                        k -> order[k],
                        k -> Arrays.stream(Item_initBuilder.__successors.get(k))
                                .mapToObj(v -> order[v])
                                .collect(Collectors.toList())));
    }

    @Override
    public Map<String, List<String>> packing(PackingBuilder builder) {
        String[] order=PackingBuilder.propertyOrder;
        return PackingBuilder.__successors.keySet().stream()
                .collect(Collectors.toMap(
                        k -> order[k],
                        k -> Arrays.stream(PackingBuilder.__successors.get(k))
                                .mapToObj(v -> order[v])
                                .collect(Collectors.toList())));
    }


    @Override
    public Map<String, List<String>> unpacking(UnpackingBuilder builder) {
        String[] order=UnpackingBuilder.propertyOrder;
        return UnpackingBuilder.__successors.keySet().stream()
                .collect(Collectors.toMap(
                        k -> order[k],
                        k -> Arrays.stream(UnpackingBuilder.__successors.get(k))
                                .mapToObj(v -> order[v])
                                .collect(Collectors.toList())));
    }
    @Override
    public Map<String, List<String>> packing_composite(Packing_compositeBuilder builder) {
        return null;
    }

    @Override
    public Map<String, List<String>> unpacking_composite(Unpacking_compositeBuilder builder) {
        return null;
    }


}
