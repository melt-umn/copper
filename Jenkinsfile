// An excellent resource is Maven's own Jenkinsfile: https://github.com/apache/maven/blob/master/Jenkinsfile
// This is also helpful: https://github.com/jenkinsci/pipeline-plugin/blob/master/TUTORIAL.md

properties([
  /* If we don't set this, everything is preserved forever.
     We don't bother discarding build logs (because they're small),
     but if this job keeps artifacts, we ask them to only stick around
     for awhile. */
  [ $class: 'BuildDiscarderProperty',
    strategy:
      [ $class: 'LogRotator',
        artifactDaysToKeepStr: '120',
        artifactNumToKeepStr: '20'
      ]
  ]
])

// Location where we dump stable artifacts: jars, tarballs
def MELT_ARTIFACTS = '/export/scratch/melt-jenkins/custom-stable-dump'
// Location of a Silver checkout (w/ jars)
def MELT_SILVER_WORKSPACE = '/export/scratch/melt-jenkins/custom-silver'

node {

try {

  stage("Build") {

    // Checks out this repo and branch
    checkout scm

    // -B  Run in non-interactive (batch) mode
    // -e  Produce execution error messages
    // -fae  Only fail the build afterwards; allow all non-impacted builds to continue
    // -Dmaven.test.failure.ignore=true  Ignore test failures (We look at them with the junit command later)
    sh "mvn clean verify -B -e -fae -Dmaven.test.failure.ignore=true"

    // I decided to remove this.
    // This means our cache is persistent, but also that it might grow unbounded, since nothing prunes it.
    //def M2_REPO = pwd() + "/.m2repo"
    // -Dmaven.repo.local=$M2_REPO  Use a local maven repo instead of in the homedir.

    junit allowEmptyResults: true, testResults:"**/target/*-reports/*.xml"
  }
  
  stage("Silver integration") {
    // Let's test against the current, (though possibly unstable!), development version of Silver
    // (We need scripts like 'deep-rebuild' so we can't use silver-latest.tar.gz.)
    sh "rm -rf ./silver-latest || true"
    sh "cp -r $MELT_SILVER_WORKSPACE silver-latest"
    
    sh "cp target/Copper*.jar silver-latest/jars/"
    
    dir('silver-latest') {
      sh "./deep-rebuild"
    }

    // Common case: clean up if successful
    sh "rm -rf ./silver-latest"
  }
  
  if (env.BRANCH_NAME == 'develop') {
    stage("Deploy stable") {
      sh "cp target/Copper*.jar $MELT_ARTIFACTS/"
    }
  }

} catch(e) {

  // JENKINS-28822. Not sure if this works exactly as intended or not
  if(currentBuild.result == null) {
    echo "Setting failure flag"
    currentBuild.result = 'FAILURE'
  }

} finally {
  
  // August requests email notifications only for develop/master, not feature branches.
  if( (env.BRANCH_NAME == 'develop' || env.BRANCH_NAME == 'master') &&
      currentBuild.result == 'FAILURE') {
    // env.JOB_NAME gives things like 'melt-umn/copper/feature%2Fjenkins' which is ugly
    def job = "copper"
    def subject = "Build failed: '${job}' (${env.BRANCH_NAME}) [${env.BUILD_NUMBER}]"
    def body = """${env.BUILD_URL}"""
    emailext(
      subject: subject,
      body: body,
      recipientProviders: [[$class: 'CulpritsRecipientProvider']]
    )
  }
}

} // end node

