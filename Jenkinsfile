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
def MELT_ARTIFACTS = '/export/scratch/melt-jenkins/custom-stable-dump/'

node {

try {

  stage("Build") {

    // Checks out this repo and branch
    checkout scm

    def M2_REPO = pwd() + "/.m2repo"
    // -B  Run in non-interactive (batch) mode
    // -e  Produce execution error messages
    // -fae  Only fail the build afterwards; allow all non-impacted builds to continue
    // -Dmaven.test.failure.ignore=true  Ignore test failures (We look at them with the junit command later)
    // -Dmaven.repo.local=$M2_REPO  Use a local maven repo instead of in the homedir.
    sh "mvn clean verify -B -e -fae -Dmaven.test.failure.ignore=true -Dmaven.repo.local=$M2_REPO"

    junit allowEmptyResults: true, testResults:"**/target/*-reports/*.xml"
  }
  
  stage("Silver integration") {
    // Let's test against the current, stable, development version of Silver
    // TODO: maybe we should test against current non-stable version?
    // i.e. /export/scratch/melt-jenkins/custom-silver ?
    def SILVER_LATEST = "$MELT_ARTIFACTS/silver-latest.tar.gz"
    // Unpacks to 'silver-latest/'
    sh "tar zxvf $SILVER_LATEST"
    
    sh "cp target/Copper*.jar silver-latest/jars/"
    
    dir('silver-latest') {
      sh "./deep-rebuild"
    }
  }
  
  if (env.BRANCH_NAME == 'develop') {
    stage("Deploy stable") {
      sh "cp target/Copper*.jar $MELT_ARTIFACTS/"
    }
  }

} finally {
  
  // TODO: Possibly send emails to culprits? Or notify slack.

}

} // end node

