


- **Name**: `FileMerging`
- **Fully Qualified Name**: `org.openprovenance.templates.fs.FileMerging`
- **IRI**: <https://openprovenance.org/templates/org/openprovenance/templates/fs/FileMerging>
- **Purpose**: The template `FileMerging` describes the transformation of two files into a single file.
- **Context**: The template is useful for describing operations in a file system.
- **Design considerations**: The ability to identify the files (before and after merging).
- **Automation**: [ttfs/config-fs.json](https://github.com/lucmoreau/provenance-templates/blob/main/provenance-templates-library/src/main/resources/ttfs/config-fs.json)

![org.openprovenance.templates.fs.FileMerging](project/template-intro1/target/generated-templates/org/openprovenance/templates/fs/file-merging.svg){#fig:org.openprovenance.templates.fs.FileMerging}


- **Details**:


    At the start, there are two files, `infile1` and `infile2`; after the activity `merging`, there is a file `merged_file`. An agent `engineer` controls the `merging` activity and uses a method (such as a script or programme).

    Some pre-defined, self-explanatory attributes have been adopted, such as `filename` and `path`.

    An example of this template is the description of a call to the Unix command, e.g., `cat infile1 infile2 > outfile`{.sh}, which reads files `infile1` and `infile2` sequentially and writes them to `outfile`. 
