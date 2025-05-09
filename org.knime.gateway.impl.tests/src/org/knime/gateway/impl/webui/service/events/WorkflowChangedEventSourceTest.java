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
 *
 * History
 *   Apr 3, 2024 (hornm): created
 */
package org.knime.gateway.impl.webui.service.events;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.junit.Test;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.util.DependentNodeProperties;
import org.knime.gateway.api.util.VersionId;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventTypeEnt.WorkflowChangedEventTypeEntBuilder;
import org.knime.gateway.api.webui.util.WorkflowBuildContext;
import org.knime.gateway.impl.project.Origin;
import org.knime.gateway.impl.project.Project;
import org.knime.gateway.impl.project.ProjectManager;
import org.knime.gateway.impl.service.util.DefaultServiceUtil.ProjectVersionException;
import org.knime.gateway.impl.webui.WorkflowKey;
import org.knime.gateway.impl.webui.WorkflowMiddleware;
import org.knime.shared.workflow.storage.clipboard.InvalidDefClipboardContentVersionException;
import org.knime.shared.workflow.storage.clipboard.SystemClipboardFormat;
import org.knime.shared.workflow.storage.clipboard.SystemClipboardFormat.ObfuscatorException;
import org.knime.testing.util.WorkflowManagerUtil;

/**
 * Tests aspects of the {@link WorkflowChangedEventSource}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class WorkflowChangedEventSourceTest {

    /**
     * Tests that workflow changed listeners are removed when the respective project is removed from the
     * {@link ProjectManager}.
     *
     * @throws IOException
     */
    @Test
    public void testRemoveListenerWhenProjectIsRemoved() throws IOException {
        var projectManager = ProjectManager.getInstance();
        var wfm = WorkflowManagerUtil.createEmptyWorkflow();
        var origin = new Origin("providerId", "spaceId", "itemId");
        projectManager.addProject(Project.builder().setWfm(wfm).setId("id1").setOrigin(origin).build());
        projectManager.addProject(Project.builder().setWfm(wfm).setId("id2").setOrigin(origin).build());

        // create event source
        var workflowMiddleware = new WorkflowMiddleware(projectManager);
        var eventSource = new WorkflowChangedEventSource(mock(EventConsumer.class), workflowMiddleware, projectManager);

        // set active project
        projectManager.setProjectActive("id1", VersionId.currentState());

        // add event listener with active project
        var snapshotId1 = workflowMiddleware
            .buildWorkflowSnapshotEnt(new WorkflowKey("id1", NodeIDEnt.getRootID()), WorkflowBuildContext::builder)
            .getSnapshotId();
        eventSource.addEventListenerAndGetInitialEventFor(builder(WorkflowChangedEventTypeEntBuilder.class)
            .setProjectId("id1").setWorkflowId(NodeIDEnt.getRootID()).setSnapshotId(snapshotId1).build(), null);

        // add another listener, not the active project, should throw
        var snapshotId2 = workflowMiddleware.buildWorkflowSnapshotEnt(new WorkflowKey("id2", NodeIDEnt.getRootID()),
            () -> WorkflowBuildContext.builder()).getSnapshotId();
        var ex1 = assertThrows(ProjectVersionException.class,
            () -> eventSource.addEventListenerAndGetInitialEventFor(builder(WorkflowChangedEventTypeEntBuilder.class)
                .setProjectId("id2").setWorkflowId(NodeIDEnt.getRootID()).setSnapshotId(snapshotId2).build(), null));
        assertThat(ex1.getMessage(), containsString("Project version \"current-state\" is not active"));

        // add another listener with active project
        projectManager.setProjectActive("id2", VersionId.currentState());
        var snapshotId3 = workflowMiddleware
            .buildWorkflowSnapshotEnt(new WorkflowKey("id2", NodeIDEnt.getRootID()), WorkflowBuildContext::builder)
            .getSnapshotId();
        eventSource.addEventListenerAndGetInitialEventFor(builder(WorkflowChangedEventTypeEntBuilder.class)
            .setProjectId("id2").setWorkflowId(NodeIDEnt.getRootID()).setSnapshotId(snapshotId3).build(), null);

        // set active project to a version, should throw
        projectManager.setProjectActive("id2", VersionId.parse("2"));
        var ex2 = assertThrows(ProjectVersionException.class,
            () -> eventSource.addEventListenerAndGetInitialEventFor(builder(WorkflowChangedEventTypeEntBuilder.class)
                .setProjectId("id2").setWorkflowId(NodeIDEnt.getRootID()).setSnapshotId(snapshotId2).build(), null));
        assertThat(ex2.getMessage(), containsString("Project version \"current-state\" is not active"));

        // check
        assertThat(eventSource.getNumRegisteredListeners(), is(2));
        projectManager.removeProject("id1");
        assertThat(eventSource.getNumRegisteredListeners(), is(1));
        projectManager.removeProject("id2");
        assertThat(eventSource.getNumRegisteredListeners(), is(0));

        // clean-up
        WorkflowManagerUtil.disposeWorkflow(wfm);
    }

    /**
     * Test for bug NXT-2977: Cached {@link DependentNodeProperties} (e.g. the 'can execute' 'allowed action' of a node)
     * are cleared when, e.g., going into a metanode/component (which removes the event listener from the parent
     * workflow) and back up while the node states in the parent workflow have changed (which also changes dependent
     * node properties which need to be re-computed).
     *
     * @throws IOException
     * @throws ObfuscatorException
     * @throws InvalidDefClipboardContentVersionException
     * @throws IllegalArgumentException
     * @throws InterruptedException
     */
    @Test
    public void testClearDependentNodePropertiesCacheOnListenerRemoval() throws IOException, IllegalArgumentException,
        InvalidDefClipboardContentVersionException, ObfuscatorException, InterruptedException {
        var projectManager = ProjectManager.getInstance();
        var wfm = WorkflowManagerUtil.createEmptyWorkflow();
        var content = SystemClipboardFormat.deserialize(CLIPBOARD_CONTENT_WAIT_NODES);
        wfm.paste(content);
        var projectId = "1";
        projectManager.addProject(Project.builder().setWfm(wfm).setId(projectId).build());

        // create event source, add and remove event listener
        var workflowMiddleware = new WorkflowMiddleware(projectManager);
        var eventSource = new WorkflowChangedEventSource(mock(EventConsumer.class), workflowMiddleware, projectManager);
        var wfKey = new WorkflowKey(projectId, NodeIDEnt.getRootID());
        var workflowSnapshot = workflowMiddleware.buildWorkflowSnapshotEnt(wfKey,
            () -> WorkflowBuildContext.builder().includeInteractionInfo(true));
        var eventType = builder(WorkflowChangedEventTypeEntBuilder.class).setProjectId(projectId)
            .setWorkflowId(NodeIDEnt.getRootID()).setSnapshotId(workflowSnapshot.getSnapshotId()).build();

        projectManager.setProjectActive(projectId, VersionId.currentState());
        var event = eventSource.addEventListenerAndGetInitialEventFor(eventType, projectId);
        eventSource.removeEventListener(eventType, projectId);
        assertThat(event.isEmpty(), is(true));

        // execute workflow -> changes 'dependent node properties' (e.g. the 'allowed action' 'can execute' of a node)
        wfm.executeAll();
        Awaitility.await().until(() -> wfm.getNodeContainerState().isExecutionInProgress());

        // get current workflow state (dependent node properties are re-computed)
        workflowSnapshot = workflowMiddleware.buildWorkflowSnapshotEnt(wfKey,
            () -> WorkflowBuildContext.builder().includeInteractionInfo(true));
        eventType = builder(WorkflowChangedEventTypeEntBuilder.class).setProjectId("1")
            .setWorkflowId(NodeIDEnt.getRootID()).setSnapshotId(workflowSnapshot.getSnapshotId()).build();

        // the actual test: there must not be any events, because the cached dependent node properties have been cleared
        // and are expected to be re-computed when creating the workflow snapshot in order to determine the returned 'workflow changed event'
        event = eventSource.addEventListenerAndGetInitialEventFor(eventType, projectId);
        assertThat("No workflow changes expected", event.isEmpty(), is(true));

        // clean-up
        wfm.getParent().cancelExecution(wfm);
        wfm.waitWhileInExecution(5, TimeUnit.SECONDS);
        WorkflowManagerUtil.disposeWorkflow(wfm);
    }

    private static final String CLIPBOARD_CONTENT_WAIT_NODES =
        "0102Ip2Yu4hb_QRHKnB404qXQ1l_f7CCJAGaA_ZLxx9XiTrzZ58A0L9fpTdsmxiYoMjAEdpe6Ihy8B8M0UaDlU9nemLaI1Bo3QusDRjRW04BtZ9o6LFwvgjqm4kuddwxVsop2tvMcn_f7YuJgwGAJ1rKv_Q516BzrHb-oFBekcoW5hvGdEX7Jmhe6RkPB2KIyttU6ZiaP7cjY8tSAJnaoUzx_Pj7eaQ2iF52hi5Zudo6ks08rPONyFL1JIB95nXeExjReAa4ygK-O3wNHPUgPAn9KzvQm1EW__dssUHpgophWJP1mHRiQyQeZ63NP3QCHqRofeGEo3zOZTdRt4hmqP-xl41ln__g5nFhGwN2V4SbBuEJTjM-kle1qQcJzcQjNLyUOwfXQU7v6KMzvbzBne2yGawEaHFJeHCC9KLx6Njx9E3zDAenmoqfNaUHY1onYYjzARM9yMIZ7um8ggWCxSWu2cQItplfBQPcl8Z9uW_m4EIKy6-E0rBXUkle8N6k2Y2tPHajtCu8AsGgaoc6zIuSC0o1UdyBtCUM_EVEmEL0t8kN7GsM5IEynqPgJWHg8YBvFywSas2Qt7VRPxG26aaBR2tD-ySn4L4nNOMdZGkytPHQXeNc5ixMC_4wK3ZA0r9NBVLoCFJhfHZVacv6gK7yoVewzyqyKPw5bvtpYGasN3mHGn6AteaOy-3rKbG2Z2Q1IxzJ9y7vFs710fge46eHomSAPTOXbN4QDGGU_eZT79QlFWNdQ43OrEm62gQDs5nSxXOARB245TFQ8StlkiJ7byzofvjXgaxYnG26MbEIKP-JPzv6_EsSS0jRnpXPPTA34NxQCSOFBoe_TlPednFB16-KfDKTwbCIYPxuEFy7f2nh-ZAtM6V7qmOp2L6OJTyTrv1olMkoo5lja9maJ5oHjgtzZv08RhsmBhbon8Zmt-K9S-8BCelEYY4ljGFHJy7kMMjzL9hFCh1dQbYrLtzcRMvJhaCd5JY6ioITuUBedUqZ7LcbhaAyyXEMYtregPblznLQyS1jcbBeb3b3zGOYmx9L0RouKB1eW_vV3DtDJe9QkWSi3G-vux0E3IeXycnc1F3oJhYGgfzWyrxRChFtr6cZuRgDueqhQ1MPBNzjJCvlqo9xMvFwrarSRhcNvJyM8R2Zw5Miq-ETaPgKe2nSUTsGR-A2FASuX1LJoapxL1tlUlwEyhxNMmNnHo5t1QDWzPLXV2vg6XlVOVXUNKPG8K6KWU7xMMPXHZNJS3mehotSrgJ7jVg1uj9vSK71bToGTzefNaNdCE4NKBy4wU2WUEJzOuYHC1L9K3RZEra44a0gyo1De0EpSUyDr7rQfe4DW5oURFOfgd_mAJMm-PfONsm6VF9I6XnS_EEuE1hwwdzn_bXG4EkOnN7nPXC67TpSSV-OuG4baKVU36K8dzKm5MOpHcOgjyu-woyKHCj89MnGdy0lnpmnCQNZd6fATd2McYfstRSQh_8Zo0vyhBLJYmZcHsJvOwsNa3Uebftg_tJbWfVASKxHcgg9RQkfzHhSSxuj-juCOU3pdpYGqqIigOnXZ3dziJsMbpSTSjJ6Bmlmvba_RWnG4Xev3-rQ0iBIxEaupJwkOpGQzx132Gqwyge8kaoR8a8cqjDPJmAmqgdyE6rt98GQs3fL0ySklx80dyQRzyl8QnAMcejgCbwQbs11UpI-81_oOgmRvdNtF_7_EdKYv0kR3mNzcq1xNP7zSjjyaROKofdedsligEQNnzXZvg1GGF5KYZ-VilcoodzRbDv4pGYTv-JqcNeLGJh698OZ9K9Qn3moH31eWmaEuzO6ivvmjoTskHIgvhWR9Jx7O4ghZvpLJ76IWFY8AnZzQ3NgBtjvELylrpZAEMgWWJkOr1u2js9MEECP2ynXIbr-HZ6CTj7RUeoLG8of3_Df9APxXvsbcT60fGreyjtCVGjuUybKhFWGx9ewEaKcXMo-m606Q9HqvPXRLJnyKt3PLn_6b1aIe9X4XG-C174F6rD8L3gaQsz5KOwkb5RplypPItA5dKw-Y7JjWaMIVWYdsFLhxENt5eFGv1u2YTJJwUaMXqy37pqO-vUEtCGS23t5COSHgixvmBVD-QHjT1nFG3A5j6qZAZY4-7F2ZLBKtSeOhBQaUIBMFHZPRoTSxQzNUFUZ97h-Vt4r-ZU4I-rLFP2CGzF6MxF_HIJPofc6Hg_xCjsCyIaFV8bM495hbgJ-lvX7TLZEWKwNFfYODvR0HBp9tF4lNnAvMK07LS-i_d7lhHf8bsWSWa2X6Kuesk4PfgCnXZ5guav5Zup50kOca8--aijBw_omDIBWLDPn2GDZ8wqKCIDWsBW8_9YN3XnDpighGYrIOBdJF__U56gDYj7ZY3SocPF9VlrLDxjlR4c9DztVaSadjSRdvYiH7RzjG2-aXpkuidFT7UoVJghCXRLlMhXhSvvC7c_dYbqL7j5OdyeJSlgwfn7VnDlKjftpsty3RuiNQ5h0rYeRCDjXPop4JSnpmrnpnq85JPosrXYOKGuux2Cwgck9cqmXMXUTmiBmkhs-tlwzSwkHH3boitr7IhXQVSZcCZjMwBK1X4Wt_iJolec_JOZd9cCPJQnxyRVf1olqb7OWhReIPeEbd9FbX73TkBVbtWXetMBdpY-_Ev7MhtUyXmR7OLHTrzzodaGMjrmDPKo7HOp7mkH8nYwrWOVpHUI7mg1n8pUo9QkDh1kkVw7iOMAdHKe6osaMFbTc_BQ0GqkJMExI--5-WPLwqVbOlbbvrYn3s8_Sh-3xxptrBnDdifMRWMNZP8-Q0vFVgCskKz3ZHgq4Ppk9aFMqDnMnaVD_Atpz-azn1X3SokPjRvcvN1w49b-Liy2G8W5rrGdmPZnfamAIOEIIiNgaM_puK8cg4b-WJghchw0IgMaJvs4HXLFsRuHknzouZdwCYasQOXC45JNdrbbFwQOE43rYYq46KO6stJZsU1i-MhhdaOKiMly7o9uJ7mHI07bsziD9ayA2NAf2HeHAlLNbnctBYxCv_5IgfyXH5qnombmG83ebi5MFKgOgcO12w5Vvg9VbKb98JZv267eISW-DuFZn-306HuWNKHyhpBtygJeX-8D6s7qA8bPx6FancrK-6sYDAA5hio6kngKFDLEsc9GLA4xvfl_HIXCtvxa0Eh_CrKYeHWXJFCNSJDnSBMMWv1k7peZHw6k1NyIMPLFGJeM4gZqSk_KfusuolC3RESx3O12cno8WZfEJQzHK5jAsjpTpHZv76dDIte56YxWKa8nYugfAZ3shc5whB3AyQ8IVxbiEJSxMaKJKZvWEu2y1XYZIA6CB4zSIla26Q16Vg1uy8bkudUsVeP3jFKuuvpnwYr6pO7PKrH6R-Z3oUD87zh6hadMBrVGJ0a2HdPnNXIU1tO5D9EjmXW42dYTS3SRY9dWwJwHR0qGRlww9Twv43iUSwArQUqdnNjhMLQPF9E_yezRJELhtDu9hkFNeDBBpc92P-K9UqAFaM34VZr6_dL1gXKF-dxvqEbcIF0Ic4i0Ozx-jg5V03S62kR3tG5-d6j4Zq1PEdCSfdlf3P1q6VuDieEM8Tc07G3MEcelc-FJCLN9o53dYNav8ajMBwOIxAha3NUt9-LgKoI9CETj2JTisiBM0jhEfvn-VscvkKD4tJ_6SdyGEP4h01OUL4W5rfACCPJfNUM7f5mGOm_fTaZ7MdL1NFbLcrQCkozhHxIuTnJJYYK9cUOD_x7PCbJj8kzDqo65WCj5SXmqHtLxFSDepycCdYd_HxsTSGIacGVAn05uActvgmp_KTSX-QQFufoYvQtIoW_sV7A0S9ZnmyMxcq8-pqPxWG2CaDvZzErOWzOSIUSmR6y3Nd1VKhc6cm8A4DDG90RljtRB73gBUevYorlMi7zpaTn7_6MQ1DuFVhTO7SBCNCSAIjTKnV8xKnsrvxw8wNmSdgqjt86mKBw1gCUoCJ97ZrVLPQtxB3897Ykr95mZwlQIF0OAepaKE9QfF10PUX1plYYtvImJ6RJAveXCxvMBELlf7wFhdZ-8RRMIC3zTz5rGy1esciNav0lsdkpCBNgL_OIBg7rLb6QHq5jcwdCnAB8MuR-wHWrDFEYvjhcaep7Sk4V5jZQ7HL6gBNJOPsUGADxoDopdNiRW7vMn1m9UPHq5U6Pjw_Y0hsIa6ANHDHTYGYbaA2_TIR7OzPlMArOKbO2CF3LzWXcuq_poT5cP0LwsD_SOJ_JTq011IaE_qdbr1y4gX9gtplrYWc5CTblspJINlQNGQEI2c3EvrgEZxSkn7fP9I_N39HMYcCfS98NecPWBbcNRG3aUnpMuex4F7uEBhwVnXaXYTZ4HdT7KPpgQlGDVd9KxNg1JFUtjnOfl14XDz6HqSHyo0JjNZQ8xZKV-CseLZwL3lNx7P17j3lL2FOqGBciJA0ljkkuQPbnAedLUp_08DiEJOz3AYUd5aDhMCWR1AdTpjvZWOoMEr5Tj7PB4uLT3qWSyM6m9lJJhRpFSt9e1tNTz_F9383Me3dU2yQTqTqD9UOdCzNyfN7ED9bV1K8zSxP8OkgkayCUjFKeriItmED2WgF9QPx4QecfDdeqcramqmGTM7Yn0OoVmNPU7JGZTbhmlY45lqDlRQlZiUCFXaXWEowvL-3z9RJzBgX8j0sUVl_dqCepF8zvWDJUWtFqnx3tTTEGlEVfaEWJOjD_9FIHm-4jz4ASCz0K6rhZwET54Meye9p06yTbbIWu-C3_CSPhL-MvzcXL4HeC9SHtVjZVL_-VImLv-4H5O2FzaQOwjNUXMqdda_VL_pzOBSdVoDLW-RE02X06Ah1pyFwzkIHS1FtHILSFd2srD8VbBeeF0LVLN-kooDRJJjsXtPqmdWYJv_bTDlELx90AviEjEVXZdW3CJaIg6Ky_sCmt7CFvWrfpDqUS3RdGt2DbO-ukelFkvn4VexIgbvdAe0dUBzj4GptzP8k-oqZkF5s1ceXet7p_DbQnI6hQjERhHYYtebWmwj9Y2zYKBgdfHFk3LkHZT8gqWz9TjbS7rVOLHG8rP6kGZLo-eBdRquu9LeI5jmCoeLI2gl9ro36iMDLrjfDCYZMKlg4EG-H3mDIdHLhXE4nPzICZCchX5U5jwQm0DVWuMEZ8Jiys0IfOR8YnCVM1ezFXLJTrNXj_yt6JNDq4gcE2YYKtBoQJnkyd4lIlQEnpWbzdR7OC9tbkzORBiKm42N9WHLXES-oYtx-488SDyZ4FtZYu5uvnkFDY9EBXbCk5cEZZjTTx3FU8luqoGat0WE5bdcf3WBBq1qjQYogRFOq812I1ASnGK-_q2VEV8aRIiMQSKSNGUY8N5zwgNK3vuJbrHtUzcuHCRCjddYSfMsxg5BozLr-D5KINB5IL7rSNLjNzQU4Fs-txPBytUPMdlnZgb6-d3NWAYLh-Wsl1OLbjBul5TlAEekMchBPye3kKIpcZT0gKzLtISqR9dResP5koPfNMxl2GaMfJqb4Tawu7CtrHoNl8s1UW8xY8yIZawQjnZt-5r8evHRcaRfk9jtXb-q2mY-G3xk_2m2ZvE4nCY04HPfKQfqoves7ogaNkUuaWa7Xmvrxwd8pyczUKgJZnKP2vx-Gk6DHIrZlbR9mhtiQYPNjp9OPMsWe9AUgsvg_q8kpS7dm2lqmsbDotCVBjxkdrY5ur_voRapjcZrglMvTj_5K6X8NwdQvIfX-GvE-JKSHxTRLocKWXssP814aqIxjD23VijSryrCzrvIdi-H54gAJ14N4TulnWzW4hL3e1uM-nDtLK_8YjM5E6hStGZlyz4G6mUlfeO723JHk6vvE6iCdB67COV0TF6r823VovHZgnEzLZ0J79HWjqgTL6X0soLoxCgIZ5FICD0NwRtoM5k8L_R669xrXel97p8-qbGaCvkDOYuzhnjd3pklza_QxnQBotZ74VNFaE3MytWFn7ISrlcotESwR2dH27xwwhUfKPK6CtfDJmAX85KWtwotlNorJiI6EFrSFNrc4GeqSQW6ldbNuB9s1axGUSrJ69ipTQib3iUVsSaLk7nvc9BghD-lCrmNse2FpWn6tFWhwEXFq_0gUmtAYuS7bw5EJ_ZAL6kwwgzSFazQusze2SMZIumddkP-_diDIgU8yzO7Aj8aZ_sZMlc3jRASzBr09MF2_5bhoUuxWDTjQ-hMtCI0U9bKqJfKI5Wn13nVBOt1-5vu5PQf2ta8dzMN3Xcjw3ss6qKADlNaWtKJSS02gCcJFKChOFoY_Pn0klOGEfjJAfQ-i9flXMt7KFMaUa_D1yBQa-eiHRQZEZVMcYKXHEfHP8N0aRNwdVfJt5l_b1-GLIx7l7oNgWex3__0slznk4yeIzrYwXzoAqg0lr5E0aFCJlfuswXvL9p6Ykq2b79OP2T18k06JKdye6WQMAw2jSCML-qUfm6403cWCmtj4J4-YNniVGlGvCOsuiZuRTpBKJHg5e1Ic2YqolVwXL_0Sj1rBhjkAHqbiXlCqOJ32IYt40l640HD968rRTBFMbLh3PmIATNrZV6nsQBm_gPPIKBvf_kF9wJm0qWbOIiJZM2eSnUbkbrVmio0ChKninqrdUH3WqyAqoJUsbhS1jPU0EMvizdihDLq4TxjF-U34DKZFWLyT74y-5ayTYALq7dG9IMIiy4SNGzBUYGvNzEY3_F6Ffb6BO-e28Cau9R87WW1lXc3RZE5prnx5pkoEs_gimRJ9nhtjfUk69FlSz6FGMmSSb1W2IyHa5O-uFbo6ih-fUKqvFCFnx3C6R8sgazeyhP2ANAhqPfEGcWHLIhhheLh8y3htM6wsZ-vZP4oreybakyIUOjqAhM3qS_fFwhH1IsBtJJSCLnbXoYZPDWKYWumzylTbmO3KBhKgSmM9GASw4FXMqyHmVlyNmncHI0FQDkUld3qLgkCU5GEDUUw222Wisk6l50ivAq5L2UMhgFqYQLnsFvZQwd2Y_tqnVWXx5DlrfAW9tNGowkKbpruPvOQUsQxEyBRf4YWHzLLngdgjVFLIx32ZFMHLhXmRWf31U85rNXObgxw0nnagPf5ja1jPyWwWapx3vH1eqFCLgWLWoVGS96UleaCQCFMhN8GfTSnhr9bENaFwecKYee9_lai8GQnMzjOQ8ya6MDYTJ-Afm6lt8sebHn_QzZ6GNc3G9wkbvsNlrxwWC8ZiT46YOjWfCXNlzZNEdbcD837f485adnL6JCvFLqE-LTRX3VK86qg8wX6PU4PCEa0d7QK6t19BI4CtVZMmxM-rXaCo_MIgG7DwTEYB2weZI9HG-Qhb4YlOpUUC7Wa5QGjZd3yULEvKs5-BDJILDlT8d0zsOF2TNrHFcTj2yu6ZJCX5fV_J-jMzvoBeYv7pu6S09t_YeYP8A7I5QJhYYFr7h2Xzo99Si_upjLkPaY2b7uiON0sGmIRXHiJOumJcIlFfq2FDU67pTWxgrm-xq6EtdqVT8STS-YQQ7QXC2G28YTIkN32ZaZ0F3fXMU9PhX7oMWu7bfWCIieRzKDMLjPxJAu-TVX-WC-r62angCYgLmQn19C2gbWReoSczVxYCdxsdrcbRvoqmCPYTTYgpd6qkgkZzJ_DdMn3WCtOXJQKWWHK18ofbNM7Z8n0tEC3nWj0hFj1HJIWr6nOXlv8vb4Vt3WBurMb2y5ERLUSeKOhBSOPt-9i3vpY1T7peDi5AnTniSTYQ3x8D9uIMCWL19StORXw-LnEJYc_Z1gw7O1nHuUEbLX6L-d0ibaJHIidnfI4wrFDsHQy0zC26q4UrY_Wo_1US8Q8b7O0pJ2bKB0qSFCTz01bRpaD5h7jGzDYJ1v6AW1rT_SSxJmV9JWYuxhjpwh_410_npF-a3nL3yiMHljtZtZD3IMMrTYfAB0zMh1So2VCKioXpJ7jmEwqEoW7x2Ogg4A7zIGqhRj_FBQwsFpOaLR6PkooYtT89QwS_dGtHPeqjeOphAwwyARRPKNm8guHTu6Nf62AeXgrQxqnxD9MLwegKA6cyJDtW4wyNno5fNi3bfBjKDbqbVmLB1eJbBWsFtIDiQqlf0TBPg0ypzik_c9kke7KiERptJJlWY-rNgxtZYtIIDguQsLRoAoznbdSso95lniH2mb1KPqlL7ooBkrgiu06mTChXhypvORIRgN8a0ipiHN41eopEzNC6QEKYNsMr-a0kAQ07lG0-yKPI7Tor2gUrXCtqbSGOIKl0wvcqWtwWT7u_ZAbpxxn6FaSc6uW0z0UFfPj7PRdOgqcoXrQ-CzNbEvVv6BBhe_IlVqehUKXF57PokyZNkwjmfXoYHOdhGSdYW5_itbg7ShcGa-X";

}
