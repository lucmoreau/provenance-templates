


- **Name**: `FileFiltering`
- **Fully Qualified Name**: `org.openprovenance.templates.fs.FileFiltering`
- **IRI**: <https://openprovenance.org/templates/org/openprovenance/templates/fs/FileFiltering>
- **Purpose**: The template `FileFiltering` describes a filtering operation selecting parts (e.g, lines) of a file.
- **Context**: The template is useful to describe operations in a file system.
- **Design considerations**: The ability to identify the file (before and after filtering), whether the filtering is in place or generates a new file.
- **Automation**: [ttfs/config-fs.json](https://github.com/lucmoreau/provenance-templates/blob/main/provenance-templates-library/src/main/resources/ttfs/config-fs.json)


![org.openprovenance.templates.fs.FileFiltering](project/template-intro1/target/generated-templates/org/openprovenance/templates/fs/file-filtering.svg){#fig:org.openprovenance.templates.fs.FileFiltering}


- **Details**:


    Filtering is a special kind of file transformation. At the start, there is a `file`; after the `filtering` activity, there is a `filtered_file`. An agent `engineer` controls the `filtering` activity and uses a method (such as a script or programme).

    Some pre-defined, self-explanatory attributes have been adopted, such as `filename` and `path`.

    An example is a Unix filtering command, e.g., `egrep '(bindings|template)' file > filtered_file`{.sh}, which selects lines containing the words `bindings` or `template` in `file`.
