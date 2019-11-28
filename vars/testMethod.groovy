import jenkins.utils.TestClassForCPS

def call() {
    def step = new TestClassForCPS(this)
    step.doRun()
}