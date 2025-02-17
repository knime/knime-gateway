/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ---------------------------------------------------------------------
 */
package org.knime.gateway.impl.webui.spaces.local;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;

import org.knime.gateway.api.webui.entity.SpaceGroupEnt;
import org.knime.gateway.api.webui.util.EntityFactory;
import org.knime.gateway.impl.util.Lazy;
import org.knime.gateway.impl.webui.spaces.SpaceGroup;
import org.knime.gateway.impl.webui.spaces.SpaceProvider;

/**
 * The "local" space provider. It especially always contains a single space group and space. The space is necessarily a
 * {@link LocalWorkspace}.
 */
public class LocalSpaceProvider implements SpaceProvider {

    /**
     * The single space in this provider
     */
    private final LocalSpace m_space;

    private final SpaceGroup<LocalSpace> m_group = new LocalSpaceGroup();

    private final Lazy.Init<LocalSpaceItemChangeNotifier> m_itemChangedNotifier =
        new Lazy.Init<>(() -> new LocalSpaceItemChangeNotifier(this));

    /**
     * @param space The single space instance that is provided by this provider
     */
    public LocalSpaceProvider(final LocalSpace space) {
        m_space = space;
    }

    /**
     * The single space group in this provider
     *
     * @return a SpaceGroup that represents the local group
     */
    @SuppressWarnings("java:S1188")
    public SpaceGroup<LocalSpace> getLocalSpaceGroup() {
        return m_group;
    }

    @Override
    public void init(final Consumer<String> loginErrorHandler) {
        // Do nothing
    }

    @Override
    public String getId() {
        return LOCAL_SPACE_PROVIDER_ID;
    }

    @Override
    public List<SpaceGroupEnt> toEntity() {
        return List.of(getLocalSpaceGroup().toEntity());
    }

    @Override
    public LocalSpace getSpace(final String spaceId) {
        return Optional.of(m_space).filter(space -> space.getId().equals(spaceId)).orElseThrow();
    }

    @Override
    public String getName() {
        return "Local space";
    }

    @Override
    public Optional<SpaceAndItemId> resolveSpaceAndItemId(final URI uri) {
        return getSpace(LocalSpace.LOCAL_SPACE_ID).getItemIdByURI(uri) //
            .map(itemId -> new SpaceAndItemId(LocalSpace.LOCAL_SPACE_ID, itemId));
    }

    @Override
    public SpaceGroup<LocalSpace> getSpaceGroup(final String spaceGroupName) {
        var localGroup = getLocalSpaceGroup();
        if (!spaceGroupName.equals(localGroup.getName())) {
            throw new NoSuchElementException("No group found with name " + spaceGroupName);
        }
        return localGroup;
    }

    @Override
    public Optional<SpaceItemChangeNotifier> getChangeNotifier() {
        if (LocalSpaceItemChangeNotifier.isEnabled()) {
            return Optional.of(m_itemChangedNotifier.get());
        } else {
            return Optional.empty();
        }
    }

    private final class LocalSpaceGroup implements SpaceGroup<LocalSpace> {

        private LocalSpaceGroup() {

        }

        private static final String ID = "Local-space-id";

        private static final String NAME = "local";

        @Override
        public SpaceGroupEnt toEntity() {
            return EntityFactory.Space.buildSpaceGroupEnt(ID, NAME, SpaceGroupEnt.TypeEnum.USER,
                List.of(m_space.toEntity()));
        }

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public SpaceGroupType getType() {
            return SpaceGroupType.USER;
        }

        @Override
        public List<LocalSpace> getSpaces() {
            return List.of(m_space);
        }

    }
}
