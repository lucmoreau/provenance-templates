#!/usr/bin/env python3
"""
Example script showing how to create an instance of BoxWorkflow and run it.

This script demonstrates:
1. Creating a LocalEnactor instance (which implements the InputOutputProcessor interface)
2. Creating a PleadWorkflow instance with the enactor
3. Running the workflow
4. Accessing and saving the history
"""

import sys
import json
import os
from pathlib import Path
from fs_local_enactor import LocalEnactor
from fs_WebTemplateInvoker import WebTemplateInvoker
from org.openprovenance.book.workflows.PleadWorkflow import PleadWorkflow



def main():
    """
    Main function to run the FsWorkflow example.
    """

    mode = sys.argv[1] if len(sys.argv) > 1 else ""

    if mode == "local":
        # Create a local enactor with negative=True (uses negative IDs)
        template_instantiator = LocalEnactor(negative=True)
    elif mode == "remote":
        # read ~/.keycloak_token and assign it to variable keycloak_token
        keycloak_token = None
        keycloak_token_path = os.path.expanduser("~/.keycloak_token")
        if os.path.exists(keycloak_token_path):
            with open(keycloak_token_path, 'r') as f:
                keycloak_token = f.read().strip()
        else:
            print(f"Keycloak token file not found at {keycloak_token_path}. Proceeding without token.")
        template_instantiator = WebTemplateInvoker("http://localhost:7075/book/provapi/statements", keycloak_token)
    else:
        print(f"Unknown mode: {mode!r}. Use 'local' or 'remote'.")
        return 1

    inputs = []
    outputs = []

    workflow = PleadWorkflow(
        templateInstantiation=template_instantiator,
        inputs=inputs,
        outputs=outputs)


    print("Running workflow...")
    workflow.workflow(engineer=111,manager=222, filenameRoot="a", oldFileId=1234, tmethod=5,   fmethod=6,    n_rows=123, n_cols=423, path="/home/bob", start="2026-03-01T09:03:51.168987Z", end="2026-03-01T09:03:51.168987Z");

    # Convert history to JSON-serializable format
    history_data = []
    for entry in template_instantiator.get_history():
        history_entry = {
            "type": entry['type'],
            "input": make_serializable(entry['input']),
            "output": make_serializable(entry['output'])
        }
        history_data.append(history_entry)


    # Determine the target directory (go up to project root and find target)
    script_dir = Path(__file__).parent
    project_root = script_dir.parent.parent.parent.parent.parent.parent
    target_dir = project_root / "target"

    # Create target directory if it doesn't exist
    target_dir.mkdir(parents=True, exist_ok=True)

    output_file = target_dir / ("pytest-fs-" + mode + ".json")

    with open(output_file, 'w') as f:
        json.dump(history_data, f, indent=2)

    print(f"Results saved to: {output_file}")
    print(f"File size: {output_file.stat().st_size} bytes")



    return 0

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



if __name__ == "__main__":
    try:
        sys.exit(main())
    except Exception as e:
        print(f"Error: {e}")
        import traceback
        traceback.print_exc()
        sys.exit(1)
