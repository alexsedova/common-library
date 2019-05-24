def call(Map params) {
    def branchName = params.get('name', 'master')
    def utils = new jenkins.utils.Utilities()
    utils.runBuild(branchName)
}