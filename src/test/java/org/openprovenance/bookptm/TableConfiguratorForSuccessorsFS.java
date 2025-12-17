
package org.openprovenance.bookptm;


import org.openprovenance.book.fs.client.common.*;
import org.openprovenance.prov.template.log2prov.FileBuilder;
import org.openprovenance.templates.catalogue.fs.configurator.TableConfigurator;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TableConfiguratorForSuccessorsFS implements TableConfigurator<Map<String, List<String>>> {

  private final Map<String, FileBuilder> documentBuilderDispatcher;

  public TableConfiguratorForSuccessorsFS(Map<String, FileBuilder> documentBuilderDispatcher) {
    this.documentBuilderDispatcher=documentBuilderDispatcher;
  }

    @Override
    public Map<String, List<String>> file_init(File_initBuilder builder) {
        String[] order=File_initBuilder.propertyOrder;
        return File_initBuilder.__successors.keySet().stream()
                .collect(Collectors.toMap(
                        k -> order[k],
                        k -> Arrays.stream(File_initBuilder.__successors.get(k))
                                .mapToObj(v -> order[v])
                                .collect(Collectors.toList())));
    }

    @Override
    public Map<String, List<String>> file_transforming(File_transformingBuilder builder) {
        String[] order=File_transformingBuilder.propertyOrder;
        return File_transformingBuilder.__successors.keySet().stream()
                .collect(Collectors.toMap(
                        k -> order[k],
                        k -> Arrays.stream(File_transformingBuilder.__successors.get(k))
                                .mapToObj(v -> order[v])
                                .collect(Collectors.toList())));
    }

    @Override
    public Map<String, List<String>> file_filtering(File_filteringBuilder builder) {
        String[] order=File_filteringBuilder.propertyOrder;
        return File_filteringBuilder.__successors.keySet().stream()
                .collect(Collectors.toMap(
                        k -> order[k],
                        k -> Arrays.stream(File_filteringBuilder.__successors.get(k))
                                .mapToObj(v -> order[v])
                                .collect(Collectors.toList())));
    }

    @Override
    public Map<String, List<String>> file_training(File_trainingBuilder builder) {
        String[] order=File_trainingBuilder.propertyOrder;
        return File_trainingBuilder.__successors.keySet().stream()
                .collect(Collectors.toMap(
                        k -> order[k],
                        k -> Arrays.stream(File_trainingBuilder.__successors.get(k))
                                .mapToObj(v -> order[v])
                                .collect(Collectors.toList())));
    }

    @Override
    public Map<String, List<String>> file_validating(File_validatingBuilder builder) {
        String[] order=File_validatingBuilder.propertyOrder;
        return File_validatingBuilder.__successors.keySet().stream()
                .collect(Collectors.toMap(
                        k -> order[k],
                        k -> Arrays.stream(File_validatingBuilder.__successors.get(k))
                                .mapToObj(v -> order[v])
                                .collect(Collectors.toList())));
    }

    @Override
    public Map<String, List<String>> file_approving(File_approvingBuilder builder) {
        String[] order=File_approvingBuilder.propertyOrder;
        return File_approvingBuilder.__successors.keySet().stream()
                .collect(Collectors.toMap(
                        k -> order[k],
                        k -> Arrays.stream(File_approvingBuilder.__successors.get(k))
                                .mapToObj(v -> order[v])
                                .collect(Collectors.toList())));
    }

    @Override
    public Map<String, List<String>> file_splitting(File_splittingBuilder builder) {
        String[] order=File_splittingBuilder.propertyOrder;
        return File_splittingBuilder.__successors.keySet().stream()
                .collect(Collectors.toMap(
                        k -> order[k],
                        k -> Arrays.stream(File_splittingBuilder.__successors.get(k))
                                .mapToObj(v -> order[v])
                                .collect(Collectors.toList())));
    }

    @Override
    public Map<String, List<String>> file_transforming_composite(File_transforming_compositeBuilder builder) {
        return null;
    }
}
