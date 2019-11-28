import jenkins.utils.Validation

def call(Map params = [:]) {
    def tool = params.get('tool')
    def task = params.get('task')
    def args = params.get('args')
    new Validation().validateBuildTool(tool)
    def utils = new jenkins.utils.Utilities()
    //utils.runTask(tool, task, args)
    echo utils.runBuild('master').get('test')
}