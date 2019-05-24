package jenkins.utils
/**
 *
 */
class Utilities implements Serializable {
    def steps
    Utilities(steps) { this.steps = steps }

    def runBuild(branchName) {
        if (branchName == 'master') {
            steps.sh 'We do not run any builds on master'
        } else {
            steps.sh 'gradle build'
        }
        return 'No build will be run'
    }
}
