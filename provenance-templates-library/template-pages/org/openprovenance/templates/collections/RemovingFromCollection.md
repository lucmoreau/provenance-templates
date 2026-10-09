


- **Name**: `RemovingFromCollection`
- **Fully Qualified Name**: `org.openprovenance.templates.collections.RemovingFromCollection`
- **IRI**: <https://openprovenance.org/templates/org/openprovenance/templates/collections/RemovingFromCollection>
- **Purpose**: The template `RemovingFromCollection` describes how a collection evolves as an item is removed from it.
- **Context**: The template helps describe common situations involving collections, such as deleting a row from a table, removing a file from a zip archive, taking a book from a shelf, or removing a box from a pallet.
- **Design considerations**: The ability to describe the state of the collection (before and after an element is removed) and the state of the element (when in the collection and after removal).
- **Automation**: [ttfs/config-removing-from-collection.json](https://github.com/lucmoreau/provenance-templates/blob/main/provenance-templates-library/src/main/resources/ttfs/config-removing-from-collection.json)


![org.openprovenance.templates.collections.RemovingFromCollection](project/template-intro1/target/generated-templates/org/openprovenance/templates/collections/removing/collection-removing.qualified.svg){#fig:org.openprovenance.templates.collections.RemovingFromCollection}



- **Details**:

    An activity `removing` operates on a collection `coll0` and an item `item0` within it. After this operation, the collection `coll1` has one fewer member, and the item `item1` no longer belongs to `coll1`.

    The template results from merging three instantiated templates. 

    - Triangle1-Entity-UGD describes the collection evolving from its initial state `coll0` to the state `coll1`, with a member removed. All other aspects of the collection remain unchanged, meaning that all other previous members remain members.

    - Triangle1-Entity-UGD describes the item `item0` initially present in the collection and becoming the item `item1`, no longer a member of the collection. Some aspects of the item may change as it is removed from the collection.

    - Triangle1-Entity-UGD describes how the collection `coll1` is derived from the item `item0` after the item's removal.

    As in the template `InsertingIntoCollection`, templates can optionally be enriched with invalidation relations and instantiations of the template Triangle2-Entity-SDS. 

    The templates InsertingIntoCollection and RemovingFromCollection capture the insertion or removal of an element (or several elements via value multiplicity) from one state of the collection to the next. Inferences about full collection membership can be drawn as multiple instances of these templates are applied in succession.




