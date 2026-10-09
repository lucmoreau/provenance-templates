

- **Name**: `FileTransforming`
- **Fully Qualified Name**: `org.openprovenance.templates.fs.FileTransforming`
- **IRI**: <https://openprovenance.org/templates/org/openprovenance/templates/fs/FileTransforming>
- **Purpose**: The template `FileTransforming` describes the transformation of a file into another file.
- **Context**: The template is useful for describing a general file operation within a file system.
- **Design considerations**: The ability to identify the file (before and after transformation) and whether the transformation is in place or generates a new file.
- **Automation**: [ttfs/config-fs.json](https://github.com/lucmoreau/provenance-templates/blob/main/provenance-templates-library/src/main/resources/ttfs/config-fs.json)


![org.openprovenance.templates.fs.FileTransforming](project/template-intro1/target/generated-templates/org/openprovenance/templates/fs/file-transforming.svg){#fig:org.openprovenance.templates.fs.FileTransforming}




- **Details**:



    At the start, there is a `file`; after the `transforming` activity, there is an item `transformed_file`. An agent `engineer` is involved in the `transforming` activity and uses a plan `method` (such as a script or programme) to drive it.

    Some predefined, self-explanatory attributes have been adopted, such as `filename` and `path`.

    This template can be used to describe how running the Unix command `gzip file`{.sh} produces a compressed file.

    This template serves as a running example throughout the book. 

   

