// Used to allow us to redirect to an internal mirror. Proxy setup did not
// work out of the box.
def githubURL = params.GITHUB_URL ?: "https://github.com/DanielWeber/jenkins-issues.git"

folder("Test")
for ( def i=1; i< 11; ++i) {
  folder("Test/$i")
  for ( def j=1; j< 21; ++j) {
    pipelineJob("Test/$i/Test_${i*j}") {
      concurrentBuild(false)
      configure {
         it / 'properties' / 'org.jenkinsci.plugins.workflow.job.properties.BuildDiscarderProperty' {
            strategy(class: 'hudson.tasks.LogRotator') {
               'daysToKeep'('-1')
               'numToKeep'('5')
               'artifactDaysToKeep'('-1')
               'artifactNumToKeep'('-1')
            }
         }
      }
      if ( "true" == enableTrigger ) {
	      triggers {
    	    cron "* * * * *"
         }
      }
      definition {
         cps { script("""
            node {
               git url: '${githubURL}', poll: false, changelog: false
               load 'pipeline-memory-leak/mypipeline.groovy'
            }
         """)
         }
      }
   }
 }
}
