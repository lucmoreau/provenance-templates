#!/usr/bin/env python3
"""
Example script showing how to create an instance of BoxWorkflow and run it.

This script demonstrates:
1. Creating a LocalEnactor instance (which implements the InputOutputProcessor interface)
2. Creating a BoxWorkflow instance with the enactor
3. Running the workflow
4. Accessing the results
"""

import sys
import json
import os
from pathlib import Path
from local_enactor import LocalEnactor
from box_workflow import BoxWorkflow


def main():
    """
    Main function to run the BoxWorkflow example.
    """
    print("Creating LocalEnactor...")
    # Create a local enactor with negative=True (uses negative IDs)
    local_enactor = LocalEnactor(negative=True)

    print("Creating BoxWorkflow...")
    # Create a BoxWorkflow instance
    # - template_invoker: the LocalEnactor that processes template inputs
    # - query: query function (set to None for this example)
    workflow = BoxWorkflow(
        template_invoker=local_enactor,
        query=None
    )

    print("Running workflow...")
    # Run the workflow - this executes all the packing, transporting, weighing operations
    results = workflow.run()

    print(f"\nWorkflow completed successfully!")
    print(f"Total results: {len(results)}")
    print(f"Total connections: {len(workflow.connections)}")
    print(f"Total connections (no agent): {len(workflow.connections_no_agent)}")

    # Print some statistics
    print("\n=== Workflow Statistics ===")
    print(f"Counter map: {local_enactor.get_counter_map()}")
    print(f"\nRecorded values:")
    for field, values in local_enactor.get_recorded_values().items():
        print(f"  {field}: {len(values)} values")

    # Print first few connections
    print("\n=== Sample Connections ===")
    for i, conn in enumerate(workflow.connections[:5]):
        print(f"\nConnection {i}:")
        print(f"  From: {conn.out_template} (ID: {conn.out_id}) -> {conn.out_property}")
        print(f"  To:   {conn.in_template} (ID: {conn.in_id}) -> {conn.in_property}")

    # Print connections without agent_init
    print(f"\n=== Connections (excluding agent_init) ===")
    print(f"Total: {len(workflow.connections_no_agent)}")
    for i, conn in enumerate(workflow.connections_no_agent[:3]):
        print(f"\nConnection {i}:")
        print(f"  From: {conn.out_template} (ID: {conn.out_id}) -> {conn.out_property}")
        print(f"  To:   {conn.in_template} (ID: {conn.in_id}) -> {conn.in_property}")

    # You can also examine specific results
    print("\n=== Sample Results ===")
    box_init_inputs = results[0]
    box_init_outputs = results[1]
    print(f"Box init inputs type: {box_init_inputs.type}")
    print(f"Box init outputs ID: {box_init_outputs.ID}")
    print(f"Box init outputs entity: {box_init_outputs.entity}")

    # Save results to JSON file
    print("\n=== Saving Results ===")

    # Helper function to convert objects to JSON-serializable format
    def make_serializable(obj):
        """Convert an object to a JSON-serializable format."""
        if obj is None:
            return None
        elif isinstance(obj, (str, int, float, bool)):
            return obj
        elif isinstance(obj, dict):
            return {k: make_serializable(v) for k, v in obj.items()}
        elif isinstance(obj, (list, tuple)):
            return [make_serializable(item) for item in obj]
        elif hasattr(obj, '__iter__') and not isinstance(obj, str):
            # Handle List and other iterables
            try:
                return [make_serializable(item) for item in obj]
            except:
                return str(obj)
        elif hasattr(obj, '__dict__'):
            # Handle objects with __dict__
            return {k: make_serializable(v) for k, v in obj.__dict__.items()
                    if not k.startswith('_')}
        else:
            # Fallback to string representation
            return str(obj)

    # Convert history to JSON-serializable format
    history_data = []
    for entry in local_enactor.get_history():
        history_entry = {
            "type": entry['type'],
            "input": make_serializable(entry['input']),
            "output": make_serializable(entry['output'])
        }
        history_data.append(history_entry)

    output_data = {
        "workflow_results": {
            "total_results": len(results),
            "total_connections": len(workflow.connections),
            "total_connections_no_agent": len(workflow.connections_no_agent),
            "total_history_entries": len(local_enactor.get_history())
        },
        "counter_map": local_enactor.get_counter_map(),
        "recorded_values": local_enactor.get_recorded_values(),
        "history": history_data,
        "connections": [
            {
                "in_id": conn.in_id,
                "in_template": conn.in_template,
                "in_property": conn.in_property,
                "out_id": conn.out_id,
                "out_template": conn.out_template,
                "out_property": conn.out_property
            }
            for conn in workflow.connections
        ],
        "connections_no_agent": [
            {
                "in_id": conn.in_id,
                "in_template": conn.in_template,
                "in_property": conn.in_property,
                "out_id": conn.out_id,
                "out_template": conn.out_template,
                "out_property": conn.out_property
            }
            for conn in workflow.connections_no_agent
        ]
    }

    # Determine the target directory (go up to project root and find target)
    script_dir = Path(__file__).parent
    project_root = script_dir.parent.parent.parent.parent.parent.parent.parent
    target_dir = project_root / "target"

    # Create target directory if it doesn't exist
    target_dir.mkdir(parents=True, exist_ok=True)

    output_file = target_dir / "pytestBox.json"

    with open(output_file, 'w') as f:
        json.dump(output_data, f, indent=2)

    print(f"Results saved to: {output_file}")
    print(f"File size: {output_file.stat().st_size} bytes")

    return 0


if __name__ == "__main__":
    try:
        sys.exit(main())
    except Exception as e:
        print(f"Error: {e}")
        import traceback
        traceback.print_exc()
        sys.exit(1)
