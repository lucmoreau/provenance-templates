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
from org.openprovenance.book.workflows.PleadWorkflow import PleadWorkflow



def main():
    """
    Main function to run the BoxWorkflow example.
    """
    print("Creating LocalEnactor...")
    # Create a local enactor with negative=True (uses negative IDs)
    local_enactor = LocalEnactor(negative=True)

    print("Creating PleadWorkflow...")
    # Create a BoxWorkflow instance
    # - template_invoker: the LocalEnactor that processes template inputs
    # - query: query function (set to None for this example)
    workflow = PleadWorkflow(
        templateInstantiation=local_enactor,
        inputs=[],
        outputs=[]
    )

    workflow.workflow(engineer=111,
                      manager=222,
                      filenameRoot="a",
                      oldFileId=1234,
                      tmethod=5,
                      fmethod=6,
                      n_rows=123,
                      n_cols=423,
                      path="/home/bob",
                      start=0,
                      end=1000)

    print("Running workflow...")
    # Run the workflow - this executes all the packing, transporting, weighing operations
    results = workflow.run()


    return 0


if __name__ == "__main__":
    try:
        sys.exit(main())
    except Exception as e:
        print(f"Error: {e}")
        import traceback
        traceback.print_exc()
        sys.exit(1)
