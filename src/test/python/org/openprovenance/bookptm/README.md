# BoxWorkflow Python Example

This directory contains the Python equivalent of the Java `BoxWorkflow` class and related components.

## Files

- **`box_workflow.py`** - Python translation of `BoxWorkflow.java`
- **`local_enactor.py`** - Python implementation of `LocalEnactor` (extends `BeanLocalEnactor2`)
- **`run_box_workflow.py`** - Example script showing how to instantiate and run the workflow

## Requirements

The workflow depends on the generated Python classes in:
```
target/generated-python/org/openprovenance/
```

Make sure your Python path includes the `target/generated-python` directory.

## Usage

### Basic Usage

```python
from local_enactor import LocalEnactor
from box_workflow import BoxWorkflow

# Create a local enactor
enactor = LocalEnactor(negative=True)

# Create and run the workflow
workflow = BoxWorkflow(template_invoker=enactor, query=None)
results = workflow.run()

# Access results
print(f"Total connections: {len(workflow.connections)}")
print(f"Connections without agent_init: {len(workflow.connections_no_agent)}")
```

### Running the Example

```bash
# Set up Python path (adjust path as needed)
export PYTHONPATH=/Users/luc/git-papers/papers/book-ptm/project/template-intro1/target/generated-python:$PYTHONPATH

# Navigate to the test directory
cd /Users/luc/git-papers/papers/book-ptm/project/template-intro1/src/test/python/org/openprovenance/bookptm

# Run the example
python3 run_box_workflow.py
```

## What the Workflow Does

The `BoxWorkflow` class simulates a package delivery workflow that includes:

1. **Initialization**
   - Creating a box
   - Creating two books
   - Initializing agents (box owner, transporters, depot manager, recipient)
   - Initializing scales

2. **Packing**
   - Packing both books into the box
   - Using composite packing operations

3. **Weighing & Transport**
   - First weighing (10.0 weight units)
   - Handover to first transporter
   - Transport to depot
   - Handover to depot manager
   - Second weighing at depot (10.0 weight units)
   - Handover to second transporter
   - Transport to recipient
   - Handover to recipient

4. **Final Operations**
   - Third weighing by recipient (15.0 weight units - discrepancy!)
   - Unpacking both books from the box

## Key Components

### BoxWorkflow

The main workflow class that:
- Orchestrates the entire package delivery process
- Tracks connections between templates
- Returns all inputs and outputs

### LocalEnactor

A simple implementation of the template processor that:
- Generates unique identifiers for entities and activities
- Implements the `newIdentifier` method for ID generation
- Supports both positive and negative ID sequences

### TemplateConnection

Represents connections between templates:
- `in_id`, `in_template`, `in_property` - destination
- `out_id`, `out_template`, `out_property` - source

## Results Structure

The `workflow.run()` method returns a list containing (in order):

1. Box initialization (inputs & outputs)
2. Book 1 initialization (inputs & outputs)
3. Book 2 initialization (inputs & outputs)
4. Packing composite operations (inputs & outputs)
5. All agent initializations (inputs & outputs for 5 agents + 3 scales)
6. All handover operations (inputs & outputs)
7. All transport operations (inputs & outputs)
8. All weighing operations (inputs & outputs)
9. Unpacking composite operations (inputs & outputs)
10. List of all connections
11. List of connections excluding agent_init

## Differences from Java Version

- Python uses `snake_case` for method names instead of `camelCase`
- Python uses `getattr`/`setattr` instead of Java reflection
- Python lists instead of Java `LinkedList`
- Python uses `@singledispatch` in `BeanLocalEnactor2` for method overloading
- No explicit type declarations

## Notes

- The workflow uses markers (`MARKER1 = -1`, `MARKER2 = -2`) for shared variables in composite operations
- The third weighing shows a discrepancy (15.0 vs expected 10.0) to demonstrate anomaly detection
- Connection tracking helps visualize the flow of data between templates
