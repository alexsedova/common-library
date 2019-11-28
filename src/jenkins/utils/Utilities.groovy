package jenkins.utils
/**
 *
 */
class Utilities implements Serializable {
    def steps
    Utilities(steps) { this.steps = steps }

    def runBuild(branchName) {
        String testStr = 'test'
        Map map = [:]
        map.put('test', testStr)
        if (branchName == 'master') {
            //steps.sh 'We do not run any builds on master'
        } else {
            //steps.sh 'gradle build'
        }
        return map
    }

    def runTask(tool, task, args) {

    }
}
