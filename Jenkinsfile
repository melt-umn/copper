#!groovy

library "github.com/melt-umn/jenkins-lib"

melt.setProperties(silverBase: false)

node {
try {

  stage("Build") {
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
    // Test against the current silver 'develop' workspace that last successfully built:
    // (We need scripts like 'deep-rebuild' so we can't use silver-latest.tar.gz.)
    sh "rm -rf ./silver-latest || true"
    sh "cp -r ${melt.SILVER_WORKSPACE} silver-latest"
    
    sh "cp target/Copper*.jar silver-latest/jars/"
    
    dir('silver-latest') {
      sh "mkdir -p generated || true"
      sh "./deep-rebuild"
    }

    // Common case: clean up if successful
    sh "rm -rf ./silver-latest"
  }
  
  if (env.BRANCH_NAME == 'develop') {
    stage("Deploy stable") {
      sh "cp target/Copper*.jar ${melt.ARTIFACTS}/"
    }
  }

} catch(e) {
  melt.handle(e)
} finally {
  // August requests email notifications only for develop/master, not feature branches.
  melt.notify(job: 'copper', ignoreBranches: true)
}
} // end node

