#!groovy
def BN = (BRANCH_NAME == 'master' || BRANCH_NAME.startsWith('releases/')) ? BRANCH_NAME : 'releases/2023-12'

library "knime-pipeline@$BN"

properties([
    pipelineTriggers([
        upstream("knime-json/${env.BRANCH_NAME.replaceAll('/', '%2F')}" +
            ", knime-reporting/${env.BRANCH_NAME.replaceAll('/', '%2F')}" +
            ", knime-js-base/${env.BRANCH_NAME.replaceAll('/', '%2F')}" +
            ", knime-product/${env.BRANCH_NAME.replaceAll('/', '%2F')}" +
            ", knime-core-ui/${env.BRANCH_NAME.replaceAll('/', '%2F')}")
    ]),
    parameters(workflowTests.getConfigurationsAsParameters()),
    buildDiscarder(logRotator(numToKeepStr: '5')),
    disableConcurrentBuilds()
])

try {
    parallel (
        'Tycho Build': {
                knimetools.defaultTychoBuild('org.knime.update.gateway')
        },
        'Integration Tests': {
                workflowTests.runIntegratedWorkflowTests(
                    profile: 'test',  nodeType: 'maven', configurations: workflowTests.DEFAULT_FEATURE_BRANCH_CONFIGURATIONS)
        },
     )

    stage('Sonarqube analysis') {
        env.lastStage = env.STAGE_NAME
        workflowTests.runSonar()
    }
} catch (ex) {
    currentBuild.result = 'FAILURE'
    throw ex
} finally {
    notifications.notifyBuild(currentBuild.result);
}

/* vim: set shiftwidth=4 expandtab smarttab: */
