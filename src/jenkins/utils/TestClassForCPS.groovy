package jenkins.utils

class TestClassForCPS {
    def steps

    TestClassForCPS(steps) {
        this.steps = steps
    }

    def doRun() {
        Map map = [:]
        map.put('test', 'some output')
        steps.sh "echo ${map.get('test')}"
    }
}
