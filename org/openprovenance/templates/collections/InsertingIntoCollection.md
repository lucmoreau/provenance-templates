


- **Name**: `InsertingIntoCollection`
- **Fully Qualified Name**: `org.openprovenance.templates.collections.InsertingIntoCollection`
- **IRI**: <https://openprovenance.org/templates/org/openprovenance/templates/collections/InsertingIntoCollection>
- **Purpose**: The template `InsertingIntoCollection` describes the evolution of a collection as an item is added to it.
- **Context**: The template helps describe common situations involving collections, such as adding a row to a table, a file to a zip archive, a book to a shelf, or a box to a palette.
- **Design considerations**: The ability to describe the state of the collection (before and after inserting an element) and the state of the element (before being in the collection or after).
- **Automation**: [ttfs/config-insert-into-collection.json](project/template-intro1/src/main/resources/ttfs/config-inserting-into-collection.json)

![org.openprovenance.templates.collections.InsertingIntoCollection](project/template-intro1/target/generated-templates/org/openprovenance/templates/collections/inserting/collection-inserting.qualified.png){#fig:org.openprovenance.templates.collections.InsertingIntoCollection}


- **Details**:

    To begin with, there is a collection `coll0` and an item `item0`. There is an activity `inserting` that adds the item `item1` to the extended collection `coll1`. 

    The template is the result of merging four instantiated templates. 

    - Triangle1-Entity-UGD describes the collection evolving from its initial state `coll0`
      to the state `coll1` with a new member. All other aspects of the collection remain 
      unchanged, meaning that all pre-existing members remain members.

    - Triangle1-Entity-UGD describes the item `item0` initially not belonging to the collection 
      and becoming item `item1` included in the collection, with all other aspects of the 
      item unchanged.  

    - Triangle1-Entity-UGD describes how the collection after extension `coll1` is derived from the item `item0`, as it becomes one of its members.

    - Triangle5-GGM links the membership relation to the activity and generations of the new collection and its new member item.

    To distinguish the role of the collection and the entity in the inputs and outputs of `inserting`, we introduce `provext:asCollection` and `provext:asMember` as two roles in the extensibility namespace, denoted by `provext`. In addition, `provext:InsertingItemIntoCollection` is the type of the activity `inserting` and of derivation `der1`. The other two derivations have types `provext:InsertingInto_Item` and `provext:InsertingInto_Collection`, respectively.

    Domain-specific instantiations of the template must consider whether the entity `item0` still exists after it is added to the collection. In a physical context, when a book is put in a box, there is a single instance of the book, and therefore, it no longer exists outside the box. Alternatively, in the digital world, adding a file to a ZIP archive keeps its original copy available.

    Note that the first template instantiation (for the collection) may be further enriched by applying Triangle2-Entity-SDS to the collection, to mark that `coll0` and `coll1` are variants of a more general collection. Likewise, the second template instantiation (for the item) may be enriched in the same way. Whether applying Triangle2-Entity-SDS is appropriate depends on the context. Extending an immutable collection (as in Scala) yields a new collection independent of the original, so Triangle2-Entity-SDS would not be desirable.

    With these additional annotations, a reasoner can now be confident that this template is being applied and that the assumption of continued membership of the remaining items holds. With this, if there were a succession of such `inserting` activities (and removing operations of [Section @sec:templates.removing.from.collection]), a reasoner would be able to infer the exact membership of the collection at any point in its lifetime, back to its initial state as an empty collection.  
