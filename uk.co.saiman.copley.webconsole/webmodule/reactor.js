import view from './view.js'
import actions from './actions.js'
import { onUpdate as onModelUpdate, propose } from './model.js'

const state = {
	init: () => {
		onModelUpdate(render)
	},
	render: (model) => {
		state.representation(model)
		state.nextAction(model);
	},
	nextAction: (model) => { },
	ready: (model) => true,
	representation: (model) => {
		var representation = 'oops... something went wrong, the system is in an invalid state';

		if (state.ready(model)) {
			const data = {

			}
			const intents = {
				doWhatever: actions.prepare(propose).whatever
			}
			representation = view.ready(data, intents);
		}

		view.display(representation);
	}
};

export const init = state.init
