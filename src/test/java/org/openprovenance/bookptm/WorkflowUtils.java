package org.openprovenance.bookptm;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WorkflowUtils {
    List<Function<Object, TemplateConnection>> connectionFuns = new LinkedList<>();


    // filter list of connections to remove those with an out_template of agent_init
    List<TemplateConnection> filterOutAgentInit(List<TemplateConnection> connections) {
        return filterOutTemplate(connections, "agent_init");
    }

    List<TemplateConnection> filterOutTemplate(List<TemplateConnection> connections, String templateName) {
        return connections.stream()
                .filter(tc -> !tc.out_template.equals(templateName))
                .collect(Collectors.toList());
    }

    void newTemplate() {
        connectionFuns = new LinkedList<Function<Object, TemplateConnection>>();
    }

    List<TemplateConnection> generateConnections(Object toTemplate, int count) {
        // select first count elements of connectionFuns and remove them from the list
        List<Function<Object, TemplateConnection>> selectedFuns = new LinkedList<Function<Object, TemplateConnection>>();
        for (int i = 0; i < count; i++) {
            selectedFuns.add(connectionFuns.remove(0));
        }
        List<TemplateConnection> conns = selectedFuns.stream().map(f -> f.apply(toTemplate)).collect(Collectors.toList());
        return conns;
    }

    List<TemplateConnection> generateConnections(Object toTemplate) {
        List<TemplateConnection> conns = connectionFuns.stream().map(f -> f.apply(toTemplate)).collect(Collectors.toList());
        newTemplate();
        return conns;
    }

    Function<Object, TemplateConnection> flowToFrom(Object toInputBean, String toProperty, Object fromTemplate, String fromProperty) {
        //tc0.out_id= fromTemplate.ID;
        //tc0.out_template= fromTemplate.isA;
        //tc0.out_property= fromProperty;
        //tc0.in_id= toTemplate.ID;
        //tc0.in_template= toTemplate.isA;
        //tc0.in_property= toProperty;

        // using reflection, assign toProperty of toTemplate object with the value of fromProperty of fromTemplate object

        try {

            Field fromPropertyField = fromTemplate.getClass().getDeclaredField(fromProperty);
            fromPropertyField.setAccessible(true);
            Object fromPropertyFieldValue = fromPropertyField.get(fromTemplate);

            Field toPropertyField = toInputBean.getClass().getDeclaredField(toProperty);
            toPropertyField.setAccessible(true);
            toPropertyField.set(toInputBean, fromPropertyFieldValue);

            Function<Object, TemplateConnection> fun = (Object toTemplate) -> {
                TemplateConnection tc0 = new TemplateConnection();

                try {
                    Field fromID = fromTemplate.getClass().getDeclaredField("ID");
                    fromID.setAccessible(true);
                    tc0.out_id = (Integer) fromID.get(fromTemplate);
                    Field fromIsA = fromTemplate.getClass().getDeclaredField("isA");
                    fromIsA.setAccessible(true);
                    tc0.out_template = (String) fromIsA.get(fromTemplate);
                    tc0.out_property = fromProperty;

                    Field toID = toTemplate.getClass().getDeclaredField("ID");
                    toID.setAccessible(true);
                    tc0.in_id = (Integer) toID.get(toTemplate);
                    Field toIsa = toTemplate.getClass().getDeclaredField("isA");
                    toIsa.setAccessible(true);
                    tc0.in_template = (String) toIsa.get(toTemplate);
                    tc0.in_property = toProperty;

                    return tc0;
                } catch (NoSuchFieldException | IllegalAccessException e1) {
                    throw new RuntimeException(e1);
                }
            };

            connectionFuns.add(fun);

            return fun;

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }
}