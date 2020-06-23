#!groovy
def BN = BRANCH_NAME == "master" || BRANCH_NAME.startsWith("releases/") ? BRANCH_NAME : "master"

library "knime-pipeline@$BN"

properties([
    pipelineTriggers([
        upstream('knime-json/' + env.BRANCH_NAME.replaceAll('/', '%2F')),
        upstream('knime-js-base/' + env.BRANCH_NAME.replaceAll('/', '%2F')),
        upstream('knime-svg/' + env.BRANCH_NAME.replaceAll('/', '%2F')),
        upstream('knime-expressions/' + env.BRANCH_NAME.replaceAll('/', '%2F')),
        upstream('knime-server-client/' + env.BRANCH_NAME.replaceAll('/', '%2F')),
        upstream('knime-executor/' + env.BRANCH_NAME.replaceAll('/', '%2F')),
        upstream('knime-reporting/' + env.BRANCH_NAME.replaceAll('/', '%2F')),
        upstream('knime-product/' + env.BRANCH_NAME.replaceAll('/', '%2F'))
    ]),
    buildDiscarder(logRotator(numToKeepStr: '5')),
    disableConcurrentBuilds()
])

try {
    knimetools.defaultTychoBuild('com.knime.update.gateway')

    stage('Sonarqube analysis') {
        env.lastStage = env.STAGE_NAME
        workflowTests.runSonar([])
    }
} catch (ex) {
    currentBuild.result = 'FAILURE'
    throw ex
} finally {
    notifications.notifyBuild(currentBuild.result);
}

/* vim: set shiftwidth=4 expandtab smarttab: */
