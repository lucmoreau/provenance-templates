from org.openprovenance.templates.catalogue.fs.integrator.BeanLocalEnactor2 import BeanLocalEnactor2
from org.openprovenance.book.fs.client.integrator.File_transformingInputs import File_transformingInputs
from org.openprovenance.book.fs.client.integrator.File_initInputs import File_initInputs


class LocalEnactor(BeanLocalEnactor2):
    """
    A simple local enactor that extends BeanLocalEnactor2 to provide
    identifier generation for workflow execution.
    """

    def __init__(self, negative=True):
        #super().__init__()
        self.negative = negative
        self.counter_initial_value = self.sign() * 10000
        self.counter_map = {}
        self.recorded_values = {}
        self.history = []
        self.id2object = {}
        self.id2array = {}
        self.csv = []
        self.csv_inputs = []

    def sign(self):
        return -1 if self.negative else 1

    def newIdentifier(self, field, counter):
        """
        Generate a new integer identifier for the given field and counter.
        """
        if counter not in self.counter_map:
            self.counter_initial_value = self.counter_initial_value + self.sign() * 10000
            self.counter_map[counter] = self.counter_initial_value

        if self.negative:
            new_value = self.counter_map[counter]
            self.counter_map[counter] -= 1
        else:
            new_value = self.counter_map[counter]
            self.counter_map[counter] += 1

        if field not in self.recorded_values:
            self.recorded_values[field] = []
        self.recorded_values[field].append(new_value)

        return new_value

    def newSIdentifier(self, field, counter):
        """
        Generate a new string identifier for the given field and counter.
        """
        if counter not in self.counter_map:
            self.counter_initial_value = self.counter_initial_value + self.sign() * 10000
            self.counter_map[counter] = self.counter_initial_value

        if self.negative:
            new_value = self.counter_map[counter]
            self.counter_map[counter] -= 1
        else:
            new_value = self.counter_map[counter]
            self.counter_map[counter] += 1

        if field not in self.recorded_values:
            self.recorded_values[field] = []
        self.recorded_values[field].append(new_value)

        return str(new_value)

    def get_counter_map(self):
        return self.counter_map

    def get_recorded_values(self):
        return self.recorded_values

    def get_history(self):
        return self.history

    def get_id2object(self):
        return self.id2object

    def get_id2array(self):
        return self.id2array

    def get_csv(self):
        return self.csv

    def get_counter_initial_value(self):
        return self.counter_initial_value

    def get_csv_inputs(self):
        return self.csv_inputs

    def is_negative(self):
        return self.negative


    def process_file_transforming_inputs(self, bean):
        # Call the parent's process method to get the output
        out = BeanLocalEnactor2.process_file_transforming_inputs(self, bean)

        # Add history tracking based on bean type
        bean_type = bean.isA
        self.history.append({'type': bean_type, 'input': bean, 'output': out})

        if hasattr(out, 'ID') and out.ID is not None:
            self.id2object[out.ID] = {'type': bean_type, 'output': out}
        return out
        pass

    def process_file_filtering_inputs(self, bean):
        # Call the parent's process method to get the output
        out = BeanLocalEnactor2.process_file_filtering_inputs(self, bean)

        # Add history tracking based on bean type
        bean_type = bean.isA
        self.history.append({'type': bean_type, 'input': bean, 'output': out})

        if hasattr(out, 'ID') and out.ID is not None:
            self.id2object[out.ID] = {'type': bean_type, 'output': out}
        return out
        pass

    def process_file_splitting_inputs(self, bean):
        # Call the parent's process method to get the output
        out = BeanLocalEnactor2.process_file_splitting_inputs(self, bean)

        # Add history tracking based on bean type
        bean_type = bean.isA
        self.history.append({'type': bean_type, 'input': bean, 'output': out})

        if hasattr(out, 'ID') and out.ID is not None:
            self.id2object[out.ID] = {'type': bean_type, 'output': out}
        return out
        pass

    def process_file_training_inputs(self, bean):
        # Call the parent's process method to get the output
        out = BeanLocalEnactor2.process_file_training_inputs(self, bean)

        # Add history tracking based on bean type
        bean_type = bean.isA
        self.history.append({'type': bean_type, 'input': bean, 'output': out})

        if hasattr(out, 'ID') and out.ID is not None:
            self.id2object[out.ID] = {'type': bean_type, 'output': out}
        return out
        pass


    def process_file_validating_inputs(self, bean):
        # Call the parent's process method to get the output
        out = BeanLocalEnactor2.process_file_validating_inputs(self, bean)

        # Add history tracking based on bean type
        bean_type = bean.isA
        self.history.append({'type': bean_type, 'input': bean, 'output': out})

        if hasattr(out, 'ID') and out.ID is not None:
            self.id2object[out.ID] = {'type': bean_type, 'output': out}
        return out
        pass

    def process_file_init_inputs(self, bean):
        # Call the parent's process method to get the output
        out = BeanLocalEnactor2.process_file_init_inputs(self, bean)

        # Add history tracking based on bean type
        bean_type = bean.isA
        self.history.append({'type': bean_type, 'input': bean, 'output': out})

        if hasattr(out, 'ID') and out.ID is not None:
            self.id2object[out.ID] = {'type': bean_type, 'output': out}
        return out
        pass

    def process_file_approving_inputs(self, bean):
        # Call the parent's process method to get the output
        out = BeanLocalEnactor2.process_file_approving_inputs(self, bean)

        # Add history tracking based on bean type
        bean_type = bean.isA
        self.history.append({'type': bean_type, 'input': bean, 'output': out})

        if hasattr(out, 'ID') and out.ID is not None:
            self.id2object[out.ID] = {'type': bean_type, 'output': out}
        return out
        pass
