


- **Name**: `RemovingFromCollection`
- **Fully Qualified Name**: `org.openprovenance.templates.collections.RemovingFromCollection`
- **IRI**: <https://openprovenance.org/templates/org/openprovenance/templates/collections/RemovingFromCollection>
- **Purpose**: The template `RemovingFromCollection` describes how a collection evolves as an item is removed from it.
- **Context**: The template helps describe common situations involving collections, such as deleting a row from a table, removing a file from a zip archive, taking a book from a shelf, or removing a box from a pallet.
- **Design considerations**: The ability to describe the state of the collection (before and after an element is removed) and the state of the element (when in the collection and after removal).
- **Automation**: [ttfs/config-removing-from-collection.json](https://github.com/lucmoreau/provenance-templates/blob/main/provenance-templates-library/src/main/resources/ttfs/config-removing-from-collection.json)


![org.openprovenance.templates.collections.RemovingFromCollection](project/template-intro1/target/generated-templates/org/openprovenance/templates/collections/removing/collection-removing.qualified.svg){#fig:org.openprovenance.templates.collections.RemovingFromCollection}



- **Details**:

    To begin with, there is a collection `coll0` of which `item0` is a member. An activity `removing` takes `item0` out of `coll0`, resulting in a collection `coll1` that no longer contains it, and in `item1`, the item as it is once out of the collection.

    The template principally results from merging four instantiated templates, together with an agent associated with `removing`, to whom `coll1` and `item1` are attributed. 

    - Triangle1-Entity-UGD describes the collection evolving from its initial state `coll0` to the state `coll1`, with a member removed. All other aspects of the collection remain unchanged, meaning that all other previous members remain members.

    - Triangle1-Entity-UGD describes the item `item0` initially present in the collection and becoming the item `item1`, no longer a member of the collection. Some aspects of the item may change as it is removed from the collection.

    - Triangle1-Entity-UGD describes how the collection after removal `coll1` is derived from the item `item0`, as `item0` stops being one of its members.

    - Qualified-Membership0 states that `item0` is a member of `coll0` before the removal.

    As in `InsertingIntoCollection`, the roles `openprov:asCollection` and `openprov:asMember` distinguish the collection and the item in the inputs and outputs of `removing`. `openprov:RemovingElementFromCollection` is the type of the activity `removing` and of the derivations between `coll0` and `coll1`, and between `item0` and `item1`; the derivation from `item0` to `coll1` has type `openprov:RemovingElement`.

    As in the template `InsertingIntoCollection`, the instantiations for the collection and for the item may be enriched with Triangle2-Entity-SDS, marking `coll0` and `coll1` (or `item0` and `item1`) as variants of a more general entity. They may also be enriched with invalidation relations, for instance stating that `removing` invalidates `coll0`.

    The templates InsertingIntoCollection and RemovingFromCollection capture the insertion or removal of an element (or several elements via value multiplicity) from one state of the collection to the next. Inferences about full collection membership can be drawn as multiple instances of these templates are applied in succession.




