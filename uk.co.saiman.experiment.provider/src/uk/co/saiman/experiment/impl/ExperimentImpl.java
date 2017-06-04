package uk.co.saiman.experiment.impl;

import uk.co.saiman.experiment.Experiment;
import uk.co.saiman.experiment.ExperimentConfiguration;
import uk.co.saiman.experiment.ExperimentRoot;

public class ExperimentImpl extends ExperimentNodeImpl<ExperimentRoot, ExperimentConfiguration>
		implements Experiment {
	protected ExperimentImpl(
			ExperimentRoot type,
			ExperimentWorkspaceImpl workspace,
			PersistedStateImpl persistedState) {
		super(type, workspace, persistedState);
	}
}