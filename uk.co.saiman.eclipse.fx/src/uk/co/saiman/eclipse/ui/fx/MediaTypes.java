/*
 * Copyright (C) 2018 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.eclipse.fx.
 *
 * uk.co.saiman.eclipse.fx is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.eclipse.fx is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.eclipse.ui.fx;

import java.util.stream.Stream;

import javafx.scene.input.DataFormat;
import uk.co.saiman.data.format.MediaType;

public class MediaTypes {
  private MediaTypes() {}

  public static DataFormat toDataFormat(MediaType mediaType) {
    DataFormat format = DataFormat.lookupMimeType(mediaType.toString());
    if (format == null) {
      format = new DataFormat(mediaType.toString());
    }
    return format;
  }

  public static Stream<MediaType> fromDataFormat(DataFormat dataFormat) {
    return dataFormat.getIdentifiers().stream().map(MediaType::valueOf);
  }
}
