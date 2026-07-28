


- **Name**: `RemovingFromCollection`
- **Fully Qualified Name**: `org.openprovenance.templates.collections.RemovingFromCollection`
- **IRI**: <https://openprovenance.org/templates/org/openprovenance/templates/collections/RemovingFromCollection>
- **Purpose**: The template `RemovingFromCollection` describes the evolution of a collection as an item is removed from it.
- **Context**: The template helps describe common situations involving collections, such as deleting a row from a table, removing a file from a zip archive, taking a book from a shelf, or removing a box from a palette.
- **Design considerations**: The ability to describe the state of the collection (before and after removing an element) and the state of the element (when in the collection and after being removed).
- **Automation**: [ttfs/config-removing-from-collection.json](https://github.com/lucmoreau/provenance-templates/blob/main/provenance-templates-library/src/main/resources/ttfs/config-removing-from-collection.json)


![org.openprovenance.templates.collections.RemovingFromCollection](project/template-intro1/target/generated-templates/org/openprovenance/templates/collections/removing/collection-removing.qualified.png){#fig:org.openprovenance.templates.collections.RemovingFromCollection}



- **Details**:

    An activity `removing` operates on a collection `coll0` and an item `item0` within it. After this operation, the collection `coll1` has one fewer member, and the item `item1` is no longer in it.

    The template results from merging three instantiated templates. 

    - Triangle1-Entity-UGD describes the collection evolving from its initial state `coll0`
      to the state `coll1` with a removed member. All other aspects of the collection remain 
      unchanged, meaning that all other previous members remain members.

    - Triangle1-Entity-UGD describes the item `item0` initially present in the collection 
      and becoming the item `item1`, no longer a member of the collection, with all other aspects of the item unchanged.

    - Triangle1-Entity-UGD describes how the collection `coll1` is derived from the item `item0`, after its removal.

    As in the template `InsertingIntoCollection`, templates can be optionally enriched with invalidation relations and instantiations of the template Triangle2-Entity-SDS. 

    The templates InsertingIntoCollection and RemovingFromCollection capture the insertion or removal of an element (or several elements via value multiplicity) from one state of the collection to the next. Inferences about full collection membership can be drawn as multiple instances of these templates are successively applied.




