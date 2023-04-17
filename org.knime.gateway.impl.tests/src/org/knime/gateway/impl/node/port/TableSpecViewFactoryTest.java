package org.knime.gateway.impl.node.port;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;

import org.junit.Test;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.knime.core.data.def.BooleanCell;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.LongCell;
import org.knime.core.data.def.StringCell;

@SuppressWarnings({"restriction"})
public class TableSpecViewFactoryTest {

    private static final DataColumnSpec[] COLSPECS = { //
        new DataColumnSpecCreator("int", IntCell.TYPE).createSpec(),
        new DataColumnSpecCreator("string", StringCell.TYPE).createSpec(),
        new DataColumnSpecCreator("long", LongCell.TYPE).createSpec(),
        new DataColumnSpecCreator("double", DoubleCell.TYPE).createSpec(),
        new DataColumnSpecCreator("boolean", BooleanCell.TYPE).createSpec(),
        new DataColumnSpecCreator("mixed-type", DataType.getCommonSuperType(StringCell.TYPE, DoubleCell.TYPE))
            .createSpec() //
    };

    @Test
    public void testTableSpecViewPage() throws IOException {
        var portView = new TableSpecViewFactory().createPortView(new DataTableSpec(COLSPECS));
        var page = portView.getPage();
        assertThat(page.getContentType().toString(), is("VUE_COMPONENT_REFERENCE"));
        var pageId = page.getPageIdForReusablePage().orElse(null);
        assertThat(pageId, is("TableSpecView"));
    }

    @Test
    public void testTableSpecInitialData() throws IOException {
        var portView = new TableSpecViewFactory().createPortView(new DataTableSpec(COLSPECS));
        var initialData = portView.createInitialDataService().get().getInitialData();
        var jsonNode = new ObjectMapper().readTree(initialData);
        var res = jsonNode.get("result");
        assertThat(res.size(), is(COLSPECS.length));
        for (var i = 0; i < COLSPECS.length; i++) {
            assertThat(res.get(i).get("name").textValue(), is(COLSPECS[i].getName()));
            assertThat(res.get(i).get("dataType").textValue(), is(COLSPECS[i].getType().getName()));
        }
    }

}
