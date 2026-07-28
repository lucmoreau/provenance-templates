package org.openprovenance.bookptm;

import org.openprovenance.book.fs.client.common.File_transformingProcessor;
import java.util.Map;
import java.util.HashMap;

public class CountFilename implements File_transformingProcessor<Integer> {
    static Map<String,Integer> map=new HashMap<>();

    @Override
    public Integer process(Integer transformed_file, String filename, Integer file, Integer method, Integer engineer, Integer transforming, String path, String time, String start, String end) {
        return map.merge(filename, 1, Integer::sum);
    }
}
