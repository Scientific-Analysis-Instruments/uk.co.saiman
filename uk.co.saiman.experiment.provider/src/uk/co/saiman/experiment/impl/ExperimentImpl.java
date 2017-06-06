/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,'========\     ,'===\    /========== \
 *      /== \___/== \  ,'==.== \   \__/== \___\/
 *     /==_/____\__\/,'==__|== |     /==  /
 *     \========`. ,'========= |    /==  /
 *   ___`-___)== ,'== \____|== |   /==  /
 *  /== \__.-==,'==  ,'    |== '__/==  /_
 *  \======== /==  ,'      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.experiment.provider.
 *
 * uk.co.saiman.experiment.provider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.provider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.impl;

import static java.nio.file.Files.newInputStream;
import static java.nio.file.Files.newOutputStream;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import uk.co.saiman.experiment.Experiment;
import uk.co.saiman.experiment.ExperimentConfiguration;
import uk.co.saiman.experiment.ExperimentException;
import uk.co.saiman.experiment.ExperimentRoot;

public class ExperimentImpl extends ExperimentNodeImpl<ExperimentRoot, ExperimentConfiguration>
		implements Experiment {
	static final String EXPERIMENT_EXTENSION = ".exml";

	private static final String EXPERIMENT_ELEMENT = "experiment";
	private static final String ID_ATTRIBUTE = "id";

	protected ExperimentImpl(
			ExperimentRoot type,
			String id,
			WorkspaceImpl workspace,
			PersistedStateImpl persistedState) {
		super(type, id, workspace, persistedState);
	}

	protected Path save() {
		Path location = getExperimentWorkspace().getWorkspaceDataPath().resolve(
				getID() + EXPERIMENT_EXTENSION);

		try (OutputStream output = newOutputStream(location, CREATE, TRUNCATE_EXISTING, WRITE)) {
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element element = document.createElement(EXPERIMENT_ELEMENT);
			saveNode(element);
			document.appendChild(element);

			Transformer tr = TransformerFactory.newInstance().newTransformer();
			tr.setOutputProperty(OutputKeys.INDENT, "yes");
			tr.setOutputProperty(OutputKeys.METHOD, "xml");
			tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			tr.transform(new DOMSource(document), new StreamResult(output));

			return location;
		} catch (Exception e) {
			throw new ExperimentException(getText().exception().cannotPersistState(this), e);
		}
	}

	protected static ExperimentImpl load(WorkspaceImpl workspace, String name) {
		return load(workspace, workspace.getWorkspaceDataPath().resolve(name + EXPERIMENT_EXTENSION));
	}

	static ExperimentImpl load(WorkspaceImpl workspace, Path path) {
		try (InputStream input = newInputStream(path, READ)) {
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);
			XPath xPath = XPathFactory.newInstance().newXPath();

			Element root = document.getDocumentElement();
			ExperimentImpl experiment = workspace
					.addExperiment(root.getAttribute(ID_ATTRIBUTE), PersistedStateImpl.load(root, xPath));

			experiment.loadChildNodes(root, xPath);

			return experiment;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ExperimentException(workspace.getText().exception().cannotLoadExperiment(path), e);
		}
	}
}
